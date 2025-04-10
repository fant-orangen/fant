describe('Create New Listing', () => {
  beforeEach(() => {
    // Intercept API calls
    cy.intercept('GET', '/api/category/all').as('getCategories');
    cy.intercept('POST', '/api/items').as('createItem');

    // Login using the custom command.
    cy.login('alice@example.com', 'Password123');

    // Visit the create listing page
    cy.visit('/create-listing/start');

    // Wait for categories to load
    cy.wait('@getCategories');
    cy.get('select#category').should('be.visible');
    cy.get('input#briefDescription').should('be.visible');
  });

  it('should allow a logged-in user to create a new listing without images', () => {
    const listingData = {
      title: `Cypress Test No Img ${Date.now()}`,
      description: 'This is a detailed description created during an e2e test (no image).',
      category: 'Computer',
      price: '175.50',
      latitude: '63.4305',
      longitude: '10.3951',
    };
    // Fill Form Fields
    cy.get('input#briefDescription').type(listingData.title);
    cy.get('textarea#fullDescription').type(listingData.description);
    cy.get('select#category').select(listingData.category);
    cy.get('input#price').type(listingData.price);
    // Location (Coordinates Mode)
    cy.contains('.location-toggle button', 'Coordinates').click();
    cy.get('input#latitude').type(listingData.latitude);
    cy.get('input#longitude').type(listingData.longitude);
    // Submit Form
    cy.get('form.listing-form').submit();
    // Wait and Verify
    cy.wait('@createItem').its('response.statusCode').should('eq', 201);
    cy.url().should('eq', Cypress.config().baseUrl + '/');
  });

  // --- Validation Test ---
  it('should display validation errors or prevent submission if required fields are submitted empty', () => {
    // Ensure form is visible and category is loaded (already checked in beforeEach)
    cy.get('input#briefDescription').should('be.visible');
    cy.get('select#category').should('be.visible');
    // Attempt to submit the form without filling required fields
    cy.get('button.submit-button[type="submit"]').should('be.visible').click();
    // --- Assertions ---
    cy.url().should('include', '/create-listing/start');
    cy.get('input#briefDescription:invalid').should('exist');
  });


  it('should display an error message if the API call fails on submit', () => {
    // --- Arrange ---
    // Intercept the POST request and force a server error
    cy.intercept('POST', '/api/items', {
      statusCode: 500,
      body: { message: 'Internal Server Error' },
      delayMs: 100, // Optional delay
    }).as('createItemError');

    // Fill the form with valid data (similar to the happy path test)
    const listingData = {
      title: `Cypress Test API Fail ${Date.now()}`,
      description: 'Testing backend failure scenario.',
      category: 'Phone',
      price: '199',
      latitude: '63.4300',
      longitude: '10.4000',
    };
    cy.get('input#briefDescription').type(listingData.title);
    cy.get('textarea#fullDescription').type(listingData.description);
    cy.get('select#category').select(listingData.category);
    cy.get('input#price').type(listingData.price);
    cy.contains('.location-toggle button', 'Coordinates').click();
    cy.get('input#latitude').type(listingData.latitude);
    cy.get('input#longitude').type(listingData.longitude);

    // --- Act ---
    // Use cy.stub() to listen for window:alert without failing the test
    const alertStub = cy.stub();
    cy.on('window:alert', alertStub);

    // Submit the form
    cy.get('form.listing-form').submit()
    .then(() => {
      // --- Assert ---
      // Wait for the intercepted error response
      cy.wait('@createItemError');

      // Check if the alert was called with the expected message
      expect(alertStub).to.have.been.calledOnceWith('Submission failed.');

      // Check that the user is still on the creation page
      cy.url().should('include', '/create-listing/start');
    });

    it('should fail validation if title (briefDescription) is too long', () => {
      // --- Arrange ---
      // Intercept the POST request to check the response if it gets sent
      cy.intercept('POST', '/api/items').as('createItemLongTitle');

      // Generate a string longer than 255 characters
      const longTitle = 'a'.repeat(260);

      // Fill the form with valid data otherwise
      const listingData = {
        title: longTitle, // Use the long title
        description: 'Testing max length validation.',
        category: 'Book',
        price: '25.00',
        latitude: '63.4310',
        longitude: '10.4100',
      };
      cy.get('input#briefDescription').type(listingData.title);
      cy.get('textarea#fullDescription').type(listingData.description);
      cy.get('select#category').select(listingData.category);
      cy.get('input#price').type(listingData.price);
      cy.contains('.location-toggle button', 'Coordinates').click();
      cy.get('input#latitude').type(listingData.latitude);
      cy.get('input#longitude').type(listingData.longitude);

      // Use cy.stub() to listen for window:alert
      const alertStub = cy.stub();
      cy.on('window:alert', alertStub);

      // --- Act ---
      cy.get('form.listing-form').submit();

      // --- Assert ---
      cy.wait('@createItemLongTitle').then((interception) => {
        expect(interception.response?.statusCode).to.eq(400);
      });

      // Expect the alert indicating submission failure
      cy.then(() => {
        expect(alertStub).to.have.been.calledWith('Submission failed.');
      });

      // Ensure we are still on the create page
      cy.url().should('include', '/create-listing/start');
    });
  });

});
