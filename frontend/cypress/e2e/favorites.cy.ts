describe('Favorite Functionality E2E Tests', () => {
  // Use 'ola' who starts with no favorites
  const testUser = {
    email: 'ola@norge.no',
    password: 'Password123',
  };
  const itemToFavorite = {
    id: '9', // 'Diskotek', owned by Jakob (ID 2)
    title: 'Diskotek',
  };

  const emptyFavoritesMessage = "You haven't added any items to your favorites yet.";
  const loginPageHeader = 'Log In';

  // --- Helper Selectors ---
  const getItemPreviewContainer = (itemTitle: string) => {
    return cy.contains('.item-preview .item-title', itemTitle, { timeout: 15000 })
    .should('be.visible')
    .parents('.item-preview');
  };

  const heartIconSelector = (itemTitle: string) => getItemPreviewContainer(itemTitle).find('.heart-icon');

  // Checks state using data-is-favorite attribute
  const checkHeartState = (itemTitle: string, state: 'favorited' | 'not_favorited' | 'loading') => {
    const expectedValue = state === 'favorited' ? 'true' : (state === 'not_favorited' ? 'false' : 'loading');
    cy.log(`Checking heart state for "${itemTitle}" to be ${state} (data-is-favorite=${expectedValue})`);
    heartIconSelector(itemTitle)
    .should('be.visible')
    .and('have.attr', 'data-is-favorite', expectedValue, { timeout: 12000 });
  };

  // Selector for the container holding item previews on the favorites page
  const favoritesListContainerSelector = '.profile-favorites-view .items-container';

  context('Logged-In User (New User Per Test)', () => {
    let testUserCredentials: { email: string; password: string; displayName: string; };

    beforeEach(() => {
      // 1. Generate unique user details
      const timestamp = Date.now();
      testUserCredentials = {
        email: `test_fav_${timestamp}@example.com`,
        password: 'Password123',
        displayName: `FavTester_${timestamp}`
      };
      cy.log(`Using user: ${testUserCredentials.email}`);

      // 2. Register the new user via UI
      cy.visit('/register');
      cy.get('#displayName').type(testUserCredentials.displayName);
      cy.get('#email').type(testUserCredentials.email);
      cy.get('#password').type(testUserCredentials.password);
      cy.get('#firstName').type('TestFav');
      cy.get('#lastName').type('UserFav');
      cy.get('#phoneNumber').type('+4798765432');
      cy.get('form').submit();

      // 3. Wait for redirection
      cy.url({ timeout: 15000 }).should('eq', Cypress.config().baseUrl + '/');
      cy.contains('button', 'Log Out', { matchCase: false }).should('be.visible');
      cy.log(`User ${testUserCredentials.email} registered and logged in.`);

      // 4. Define API intercepts
      cy.intercept('POST', `/api/favorite/${itemToFavorite.id}`).as('addFavorite');
      cy.intercept('DELETE', `/api/favorite/${itemToFavorite.id}`).as('removeFavorite');
      cy.intercept('GET', '/api/favorite?page=*').as('getFavoritesPage');
      cy.intercept('GET', `/api/favorite/status/${itemToFavorite.id}`).as('checkSpecificFavoriteStatus');

      // 5. Navigate to home and wait for item + status check
      cy.visit('/');
      cy.contains('.item-preview .item-title', itemToFavorite.title, { timeout: 15000 }).should('be.visible');
      cy.log('Waiting for initial favorite status check...');
      cy.wait('@checkSpecificFavoriteStatus', { timeout: 10000 });
      heartIconSelector(itemToFavorite.title)
      .should('not.have.attr', 'data-is-favorite', 'loading', { timeout: 10000 });
      checkHeartState(itemToFavorite.title, 'not_favorited');
      cy.log('Initial status check complete and verified.');
    });

    // Test Passed Previously
    it('should display an empty message initially when no items are favorited', () => {
      cy.log(`Test (Empty Initial): Visiting favorites for ${testUserCredentials.email}`);
      cy.visit('/profile/favorites');
      cy.wait('@getFavoritesPage');
      cy.get(favoritesListContainerSelector).find('.item-preview').should('not.exist');
      cy.contains('.profile-favorites-view', emptyFavoritesMessage, { timeout: 10000 }).should('be.visible');
      cy.log('Test (Empty Initial): Verified empty message.');
    });

    // Test Passed Previously
    it('should allow adding an item to favorites from item list', () => {
      cy.log(`Test (Add): Starting add test for ${testUserCredentials.email}`);
      checkHeartState(itemToFavorite.title, 'not_favorited');
      heartIconSelector(itemToFavorite.title).click({ force: true });
      cy.wait('@addFavorite').its('response.statusCode').should('eq', 204);
      checkHeartState(itemToFavorite.title, 'favorited');
      cy.log('Test (Add): Item added, state verified.');

      cy.visit('/profile/favorites');
      cy.wait('@getFavoritesPage').then((interception) => {
        cy.log('API Response (@getFavoritesPage):', interception.response?.body);
        expect(interception.response?.body).to.have.property('content');
      });
      cy.reload();
      cy.wait('@getFavoritesPage');

      cy.log(`Verifying page contains "${itemToFavorite.title}"...`);
      cy.contains(itemToFavorite.title, { timeout: 15000 }).should('be.visible');
      cy.contains('.profile-favorites-view', emptyFavoritesMessage).should('not.exist');
      cy.log('Test (Add): Verified item text present on favorites page after reload.');
    });

    // Test Passed Previously
    it('should allow removing an item from favorites from item list (after adding it)', () => {
      // --- Setup ---
      cy.log(`Test (Remove List): Adding item first for ${testUserCredentials.email}`);
      checkHeartState(itemToFavorite.title, 'not_favorited');
      heartIconSelector(itemToFavorite.title).click({ force: true });
      cy.wait('@addFavorite');
      checkHeartState(itemToFavorite.title, 'favorited');
      cy.log('Test (Remove List): Setup complete.');
      // --- End Setup ---

      heartIconSelector(itemToFavorite.title).click({ force: true });
      cy.wait('@removeFavorite').its('response.statusCode').should('eq', 204);
      checkHeartState(itemToFavorite.title, 'not_favorited');
      cy.log('Test (Remove List): Item removed, state verified.');

      cy.visit('/profile/favorites');
      cy.wait('@getFavoritesPage');
      cy.contains(itemToFavorite.title).should('not.exist');
      cy.contains('.profile-favorites-view', emptyFavoritesMessage, { timeout: 10000 }).should('be.visible');
      cy.log('Test (Remove List): Verified item text absent and empty message present.');
    });

    // Test 3 - Was Failing
    it('should allow removing an item from the favorites page itself (after adding it)', () => {
      // --- Setup ---
      cy.log(`Test (Remove Fav Page): Adding item first for ${testUserCredentials.email}`);
      checkHeartState(itemToFavorite.title, 'not_favorited');
      heartIconSelector(itemToFavorite.title).click({ force: true });
      cy.wait('@addFavorite');
      checkHeartState(itemToFavorite.title, 'favorited');
      cy.log('Test (Remove Fav Page): Setup complete.');
      // --- End Setup ---

      cy.visit('/profile/favorites');
      cy.wait('@getFavoritesPage').then((interception) => {
        cy.log('API Response (@getFavoritesPage - Before Remove):', interception.response?.body);
      });
      cy.reload();
      cy.wait('@getFavoritesPage');

      cy.log(`Verifying page contains "${itemToFavorite.title}" before removal...`);
      cy.contains(itemToFavorite.title, { timeout: 15000 }).should('be.visible');

      // Wait for status check and verify state
      cy.wait('@checkSpecificFavoriteStatus', { timeout: 10000 });
      heartIconSelector(itemToFavorite.title)
      .should('not.have.attr', 'data-is-favorite', 'loading', { timeout: 10000 });
      checkHeartState(itemToFavorite.title, 'favorited');
      cy.log('Test (Remove Fav Page): Verified item present on favorites page.');

      // Click the heart icon *on the favorites page*
      heartIconSelector(itemToFavorite.title).click({ force: true });
      cy.wait('@removeFavorite').its('response.statusCode').should('eq', 204);
      cy.log('Test (Remove Fav Page): Remove API call successful.');

      cy.log('Reloading favorites page to verify visual removal...');
      cy.reload();
      cy.wait('@getFavoritesPage'); // Wait for data after reload

      // Verify removal from view after reload
      cy.log(`Verifying page does NOT contain "${itemToFavorite.title}" after removal and reload...`);
      cy.contains(itemToFavorite.title).should('not.exist');
      cy.contains('.profile-favorites-view', emptyFavoritesMessage, { timeout: 10000 }).should('be.visible');
      cy.log('Test (Remove Fav Page): Item removed, emptiness verified after reload.');
    });
  });

  // Non-logged-in context remains the same
  context('Non-Logged-In User', () => {
    beforeEach(() => {
      cy.visit('/');
      cy.contains('.item-preview .item-title', itemToFavorite.title, { timeout: 15000 }).should('be.visible');
    });

    it('should redirect to login page when trying to favorite an item', () => {
      heartIconSelector(itemToFavorite.title).click({ force: true });
      cy.url({ timeout: 10000 }).should('include', '/login');
      cy.contains('h1', loginPageHeader, { timeout: 10000 }).should('be.visible');
    });
  });
});
