describe('MapView Functionality', () => {
  beforeEach(() => {
    cy.intercept('GET', '/api/items/search*').as('searchItems');
    cy.intercept('GET', '/api/category/all').as('getCategories');
    cy.intercept('GET', '/api/users/profile').as('getUserProfile');

    cy.visit('/map');

    cy.wait('@getCategories', { timeout: 10000 });
    cy.get('.map-container', { timeout: 20000 }).should('be.visible');
    cy.wait('@searchItems', { timeout: 10000 });
  });

  it('should load the map and display initial markers', () => {
    cy.get('.map-container').should('be.visible');
    cy.get('.leaflet-control-zoom-in').should('be.visible');
    cy.get('.map-container .leaflet-marker-icon', { timeout: 15000 }).should('have.length.greaterThan', 0);
  });

  it('should open a popup with item details when a marker is clicked', () => {
    cy.get('.map-container .leaflet-marker-icon', { timeout: 15000 })
    .should('exist')
    .first()
    .click({ force: true });

    cy.get('.leaflet-popup-content .popup-content', { timeout: 5000 })
    .should('be.visible')
    .and('contain.text', 'kr');

    cy.get('.leaflet-popup-content .popup-content a')
    .should('have.attr', 'href')
    .and('include', '/item-detail/');
  });

  it('should update markers when filtering by price', () => {
    const minPrice = '1000';
    const maxPrice = '5000';

    cy.intercept('GET', `/api/items/search*minPrice=${minPrice}*`).as('minPriceSearch');
    cy.intercept('GET', `/api/items/search*maxPrice=${maxPrice}*`).as('maxPriceSearch');

    cy.get('.advanced-toggle-button').should('be.visible').click();
    cy.get('.advanced-options', { timeout: 5000 }).should('be.visible');

    cy.get('.advanced-options input#min-price').should('be.visible').clear().type(minPrice).blur();
    cy.wait('@minPriceSearch', { timeout: 10000 });

    cy.get('.advanced-options input#max-price').should('be.visible').clear().type(maxPrice).blur();
    cy.wait('@maxPriceSearch', { timeout: 10000 });

    cy.wait(500);

    cy.get('.map-container .leaflet-marker-icon', { timeout: 10000 }).should('exist');
    cy.get('.map-container .leaflet-marker-icon').first().click({ force: true });
    cy.get('.leaflet-popup-content .popup-content a', { timeout: 5000 })
    .should('be.visible')
    .invoke('text')
    .then((text) => {
      const priceMatch = text.match(/(\d+[\s.,]?\d*)\s*kr/);
      if (priceMatch && priceMatch[1]) {
        const price = parseFloat(priceMatch[1].replace(/\s/g, '').replace('.', '').replace(',', '.'));
        expect(price).to.be.at.least(parseFloat(minPrice));
        expect(price).to.be.at.most(parseFloat(maxPrice));
      } else {
        cy.log('Could not extract price from popup for verification.');
      }
    });
  });

  it('should update markers when filtering by category', () => {
    const categoryName = 'Computer';
    const categoryLabel = 'Computers';
    cy.intercept('GET', `/api/items/search*categoryName=${categoryName}*`).as('categorySearch');

    cy.get('.category-sidebar-desktop .category-button')
    .contains('.category-label', categoryLabel)
    .click();

    cy.wait('@categorySearch');
    cy.wait(500);

    cy.get('.map-container .leaflet-marker-icon', { timeout: 10000 }).should('exist');
    cy.get('.clear-category-button').should('be.visible');
  });

  it('should clear category filter and update markers', () => {
    const categoryLabel = 'Computers';
    const categoryName = 'Computer';
    cy.intercept('GET', `/api/items/search*categoryName=${categoryName}*`).as('categorySearch');

    cy.get('.category-sidebar-desktop .category-button')
    .contains('.category-label', categoryLabel)
    .click();
    cy.wait('@categorySearch');
    cy.get('.map-container .leaflet-marker-icon', { timeout: 10000 }).should('exist');
    cy.get('.clear-category-button').should('be.visible');

    cy.intercept('GET', '/api/items/search*').as('searchAfterClear');
    cy.get('.clear-category-button').click();

    cy.wait('@searchAfterClear', { timeout: 10000 }).then((interception) => {
      expect(interception.request.url).not.to.include('categoryName=');
    });
    cy.wait(500);

    cy.get('.clear-category-button').should('not.exist');
    cy.get('.map-container .leaflet-marker-icon', { timeout: 10000 }).should('exist');
  });

  context('Map Drawing (Circle Search)', () => {
    it('should allow drawing a circle search area and update markers', () => {
      cy.get('.leaflet-draw-draw-circle').should('be.visible').click();
      cy.get('.map-container')
      .trigger('mousedown', { which: 1, clientX: 300, clientY: 300 })
      .trigger('mousemove', { clientX: 400, clientY: 400 })
      .trigger('mouseup', { force: true });

      cy.wait('@searchItems', { timeout: 10000 }).its('request.url')
      .should('include', 'userLatitude=')
      .and('include', 'userLongitude=')
      .and('include', 'maxDistance=');

      cy.wait(1000);

      cy.get('body').then($body => {
        if ($body.find('.map-container .leaflet-marker-icon').length > 0) {
          cy.get('.map-container .leaflet-marker-icon', { timeout: 15000 }).should('exist');
          cy.log('Markers found after drawing.');
        } else {
          cy.log('No markers found after drawing for this area.');
        }
      });

      cy.get('.map-container .leaflet-interactive[stroke="#007bff"]').should('be.visible');
      cy.get('.clear-drawn-area-button').should('be.visible');
    });

    it('should clear the drawn search area and reset search', () => {
      cy.get('.leaflet-draw-draw-circle').click();
      cy.get('.map-container')
      .trigger('mousedown', { which: 1, clientX: 300, clientY: 300 })
      .trigger('mousemove', { clientX: 400, clientY: 400 })
      .trigger('mouseup', { force: true });
      cy.wait('@searchItems');
      cy.get('.clear-drawn-area-button').should('be.visible');
      cy.get('.map-container .leaflet-interactive[stroke="#007bff"]').should('be.visible');

      cy.intercept('GET', '/api/items/search*').as('searchAfterClearDraw');
      cy.get('.clear-drawn-area-button').click();

      cy.get('.clear-drawn-area-button').should('not.exist');
      cy.get('.map-container .leaflet-interactive[stroke="#007bff"]').should('not.exist');

      cy.wait('@searchAfterClearDraw', { timeout: 10000 }).then((interception) => {
        expect(interception.request.url).not.to.include('maxDistance=');
      });
      cy.wait(500);

      cy.get('.map-container .leaflet-marker-icon', { timeout: 10000 }).should('exist');
    });
  });

  it('should highlight an item if highlightItemId is provided in URL', () => {
    const itemIdToHighlight = '10';
    cy.visit(`/map?highlightItem=${itemIdToHighlight}`);

    cy.get('.map-container', { timeout: 20000 }).should('be.visible');
    cy.wait('@searchItems', { timeout: 10000 });

    cy.get('.leaflet-popup-content .popup-content a', { timeout: 18000 })
    .should('be.visible')
    .and('have.attr', 'href', `/item-detail/${itemIdToHighlight}`);
  });

});
