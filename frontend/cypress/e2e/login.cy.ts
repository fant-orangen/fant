describe('Login Flow', () => {

  beforeEach(() => {
    // Visit the login page before each test
    cy.visit('/login');
  });

  it('should log in successfully with valid credentials', () => {
    // Use credentials from data.sql
    const username = 'alice@example.com'; // Corresponds to email in your setup
    const password = 'Password123';      // The actual password before hashing

    cy.get('#username').type(username); // ID from LoginForm.vue
    cy.get('#password').type(password); // ID from LoginForm.vue

    // Submit the form
    cy.get('form').submit();

    // Assertions after successful login:
    // 1. Should redirect to the homepage
    cy.url().should('eq', Cypress.config().baseUrl + '/');

    // 2. Check for logged-in state indicator
    cy.get('button').contains('Log Out', { matchCase: false }).should('be.visible');

    // 3. Verify token exists in localStorage
    cy.window().its('localStorage').invoke('getItem', 'token').should('exist');
    cy.window().its('localStorage').invoke('getItem', 'username').should('eq', username);
  });

  it('should display an error with invalid credentials', () => {
    // Use valid username but wrong password
    const username = 'alice@example.com'; //
    const wrongPassword = 'wrongPassword123';

    cy.get('#username').type(username);
    cy.get('#password').type(wrongPassword);

    // Submit the form
    cy.get('form').submit();

    // Assertions for failed login:
    // 1. Should remain on the login page
    cy.url().should('include', '/login');

    // 2. Check for the error message display
    // Adjust selector based on where LoginForm.vue displays the 'error' ref
    cy.get('.form-container .error').should('be.visible')
    // Check for text indicating invalid credentials (depends on backend/store error handling)
    .and('contain', 'Login Failed');

    // 3. Verify token does not exist in localStorage
    cy.window().its('localStorage').invoke('getItem', 'token').should('not.exist');
  });

  it('should disable submit button if username or password is empty', () => {
    // Check initial state
    cy.get('button[type="submit"]').should('be.disabled');

    // Type only username
    cy.get('#username').type('test@example.com');
    cy.get('button[type="submit"]').should('be.disabled');

    // Clear username, type only password
    cy.get('#username').clear();
    cy.get('#password').type('password123');
    cy.get('button[type="submit"]').should('be.disabled');

    // Type both
    cy.get('#username').type('test@example.com');
    cy.get('button[type="submit"]').should('not.be.disabled');
  });

  it('should navigate to registration page when register button is clicked', () => {
    // Find the button likely by its text content
    cy.get('button').contains('Register', { matchCase: false }).click(); //

    // Assert navigation to the registration page
    cy.url().should('include', '/register'); //
  });
});
