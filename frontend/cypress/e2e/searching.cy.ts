describe('Search Functionality', () => {
  beforeEach(() => {
    cy.intercept('GET', '/api/items/search*').as('searchItems');
    cy.intercept('GET', '/api/category/all').as('getCategories');
    cy.visit('/');
    // Wait for initial loads
    cy.wait(['@searchItems', '@getCategories'], { timeout: 10000 });
    // Wait for UI elements dependent on initial load
    cy.get('.items-container', { timeout: 10000 }).should('exist');
    cy.get('.category-row', { timeout: 10000 }).should('exist');
  });

  it('should allow typing in the search bar and display relevant items', () => {
    const searchTerm = 'Laptop';
    cy.get('#search-term')
    .should('be.visible')
    .type(searchTerm)
    .blur(); // Add blur to simulate focus loss

    // Instead of waiting for a potentially intermediate network request,
    // wait for the UI to update and reflect the search term.
    cy.get('.items-container .item-preview .item-title', { timeout: 10000 }) // Wait for titles to potentially update
    .should('exist') // Ensure results are rendered
    .first()
    .invoke('text')
    .should('match', /Laptop/i); // Assert the content reflects the search
  });

  it('should toggle advanced search options', () => {
    cy.get('.advanced-options').should('not.exist');
    cy.get('.advanced-toggle-button').should('be.visible').click();
    cy.get('.advanced-options').should('be.visible');
    cy.get('#min-price').should('be.visible');
    cy.get('.advanced-toggle-button').click();
    cy.get('.advanced-options').should('not.exist');
  });

  it('should filter by min and max price and verify results', () => {
    cy.get('.advanced-toggle-button').click();
    cy.get('.advanced-options').should('be.visible');

    const minPrice = '1000';
    const maxPrice = '5000';

    cy.get('#min-price')
    .type(minPrice)
    .blur(); // Add blur

    cy.get('#max-price')
    .type(maxPrice)
    .blur(); // Add blur

    // Wait for the items container to potentially update after the inputs and blurs
    // Use cy.wait('@searchItems') here AFTER the last input/blur if a final request is guaranteed
    // Or rely on waiting for the UI assertion below. Let's wait for the last searchItems call.
    cy.wait('@searchItems', {timeout: 10000}); // Wait for the search triggered by maxPrice input/blur

    // Assert the UI reflects the price range
    cy.get('.items-container', { timeout: 10000 }).should('exist'); // Ensure container exists
    cy.get('.items-container .item-preview').then(($items) => {
      if ($items.length > 0) {
        cy.get('.items-container .item-preview .item-price').each(($el) => {
          const priceText = $el.text().replace(/\s*kr/i, '').replace(/,/g, '.').trim();
          const price = parseFloat(priceText);
          cy.log(`Found price: ${priceText}, Parsed: ${price}`);
          if (!isNaN(price)) {
            expect(price).to.be.at.least(parseFloat(minPrice));
            expect(price).to.be.at.most(parseFloat(maxPrice));
          }
        });
      } else {
        cy.log('No items found to verify price range.');
        // If no items are expected, assert that:
        // cy.get('.items-container .item-preview').should('not.exist');
        // cy.get('.no-items-message').should('be.visible');
      }
    });
  });

  // --- Other tests remain the same ---

  it('should change the sort option and verify results', () => {
    cy.get('.advanced-toggle-button').click();
    cy.get('.advanced-options').should('be.visible');

    const sortOptionValue = 'price_asc';
    const backendSortParam = 'price,asc'; // Expecting non-encoded comma

    cy.get('#sort-select').select(sortOptionValue);
    // Wait for the sort request and assert URL param
    cy.wait('@searchItems', { timeout: 10000 })
    .its('request.url')
    .should('include', `sort=${backendSortParam}`);

    // Assert UI sort order
    let prices: number[] = [];
    cy.get('.items-container .item-preview .item-price').should('exist').each(($el) => {
      const priceText = $el.text().replace(/\s*kr/i, '').replace(/,/g, '.').trim();
      const price = parseFloat(priceText);
      if (!isNaN(price)) {
        prices.push(price);
      }
    }).then(() => {
      cy.log(`Collected prices: ${prices}`);
      if (prices.length > 1) {
        const sortedPrices = [...prices].sort((a, b) => a - b);
        expect(prices).to.deep.equal(sortedPrices);
      } else {
        cy.log('Not enough items to verify sorting.');
      }
    });
  });

  it('should request user location', () => {
    cy.window().then((win) => {
      cy.stub(win.navigator.geolocation, 'getCurrentPosition').callsFake((successCallback) => {
        const position = { coords: { latitude: 63.4305, longitude: 10.3951 } };
        successCallback(position);
      }).as('getCurrentPosition');
    });

    cy.get('.advanced-toggle-button').click();
    cy.get('.advanced-options').should('be.visible');
    cy.get('.location-button').click();
    cy.get('@getCurrentPosition').should('have.been.calledOnce');
    cy.get('.location-error').should('not.exist');
    cy.wait('@searchItems').its('request.url').should('include', 'userLatitude=63.4305');
    cy.get('#distance-slider').should('not.be.disabled');
  });

  it('should handle geolocation error', () => {
    cy.window().then((win) => {
      cy.stub(win.navigator.geolocation, 'getCurrentPosition').callsFake((success, errorCallback) => {
        const error = { code: 1, message: 'User denied Geolocation' };
        if (errorCallback) errorCallback(error);
      }).as('getCurrentPositionError');
    });

    cy.get('.advanced-toggle-button').click();
    cy.get('.location-button').click();
    cy.get('@getCurrentPositionError').should('have.been.calledOnce');
    cy.get('.location-error').should('be.visible').and('contain', 'Failed to get your location.');
    cy.get('#distance-slider').should('be.disabled');
  });

  it('should allow adjusting the distance slider after location is available', () => {
    cy.window().then((win) => {
      cy.stub(win.navigator.geolocation, 'getCurrentPosition').callsFake((successCallback) => {
        const position = { coords: { latitude: 63.4305, longitude: 10.3951 } };
        successCallback(position);
      });
    });
    cy.get('.advanced-toggle-button').click();
    cy.get('.location-button').click();
    cy.wait('@searchItems');

    const newDistance = 200;
    cy.get('#distance-slider')
    .should('not.be.disabled')
    .invoke('val', newDistance)
    .trigger('input');

    // Wait for search triggered by slider change
    cy.wait('@searchItems', { timeout: 10000 }).its('request.url').should('include', `maxDistance=${newDistance}`);
  });

  it('should filter by category when a category is clicked', () => {
    const categoryName = 'Computer';
    const categoryLabel = 'Computers';

    cy.get('.category-wrapper .category-button .category-label')
    .contains(categoryLabel)
    .parents('.category-wrapper')
    .click();

    cy.wait('@searchItems')
    .its('request.url')
    .should('include', `categoryName=${categoryName}`);

    cy.get('.category-wrapper .category-button .category-label')
    .contains(categoryLabel)
    .parents('.category-button')
    .should('have.class', 'active');

    cy.get('.clear-category-button').should('be.visible');
    cy.get('.items-container .item-preview').should('exist');
  });

  it('should clear category filter when "Clear Category Filter" is clicked', () => {
    const categoryLabel = 'Computers';

    cy.get('.category-wrapper .category-button .category-label')
    .contains(categoryLabel)
    .parents('.category-wrapper')
    .click();
    cy.wait('@searchItems');
    cy.get('.clear-category-button').should('be.visible').click();
    cy.wait('@searchItems').its('request.url').should('not.include', 'categoryName');
    cy.get('.clear-category-button').should('not.exist');
    cy.get('.all-categories-button').should('have.class', 'active');
    cy.get('.category-wrapper .category-button .category-label')
    .contains(categoryLabel)
    .parents('.category-button')
    .should('not.have.class', 'active');
  });

  it('should toggle pagination/infinite scroll', () => {
    cy.get('.pagination-controls').should('be.visible');
    cy.get('.button-stack .toggle-scroll-button').eq(1).click();
    cy.get('.pagination-controls').should('not.exist');
    cy.wait('@searchItems');
    cy.get('.button-stack .toggle-scroll-button').eq(1).click();
    cy.get('.pagination-controls').should('be.visible');
    cy.wait('@searchItems');
  });

  it('should toggle thumbnail size', () => {
    cy.get('.items-container').should('not.have.class', 'small-thumbnails');
    cy.get('.button-stack .toggle-scroll-button').eq(0).click();
    cy.get('.items-container').should('have.class', 'small-thumbnails');
    cy.get('.button-stack .toggle-scroll-button').eq(0).click();
    cy.get('.items-container').should('not.have.class', 'small-thumbnails');
  });

  it('should handle pagination clicks', () => {
    cy.get('.pagination-controls').should('be.visible');
    let totalPages = 1;
    cy.get('.page-info', { timeout: 10000 }).invoke('text').then((text) => {
      const match = text.match(/of (\d+)/);
      if (match) {
        totalPages = parseInt(match[1], 10);
      }
      cy.log(`Total Pages detected: ${totalPages}`);

      if (totalPages > 1) {
        cy.get('.pagination-button:contains("Next")').should('not.be.disabled').click();
        cy.wait('@searchItems').its('request.url').should('include', 'page=1');
        cy.get('.page-info').should('contain', 'Page 2');

        cy.get('.pagination-button:contains("Previous")').should('not.be.disabled').click();
        cy.wait('@searchItems').its('request.url').should('include', 'page=0');
        cy.get('.page-info').should('contain', 'Page 1');
      } else {
        cy.log('Skipping pagination clicks as total pages <= 1');
        cy.get('.pagination-button:contains("Next")').should('be.disabled');
        cy.get('.pagination-button:contains("Previous")').should('be.disabled');
      }
    });
  });

  it('should navigate to item detail when an item preview is clicked', () => {
    cy.get('.items-container .item-preview', { timeout: 10000 }).should('have.length.greaterThan', 0);

    let firstItemTitle: string = '';
    cy.intercept('GET', '/api/items/details/*').as('getItemDetails');

    cy.get('.items-container .item-preview').first().within(() => {
      cy.get('.item-title').invoke('text').then(title => firstItemTitle = title);
    }).click();

    cy.wait('@getItemDetails');
    cy.url().should('include', '/item-detail/');
    cy.get('h1.item-title', { timeout: 10000 }).should('contain', firstItemTitle);
  });
});
