describe('Registration Flow', () => {
  // Use a unique email for each successful registration test run
  const uniqueEmail = `testuser_${Date.now()}@example.com`;
  const validPassword = 'Password123';

  beforeEach(() => {
    // Visit the registration page before each test
    cy.visit('/register'); // Assuming '/register' is the correct route
  });

  it('should register a new user successfully', () => {
    cy.get('#displayName').type('Test User Reg');
    cy.get('#email').type(uniqueEmail);
    cy.get('#password').type(validPassword);
    cy.get('#firstName').type('Test');
    cy.get('#lastName').type('User');
    cy.get('#phoneNumber').type('+4712345678');

    // Submit the form
    cy.get('form').submit();

    // Assertions after successful registration:
    // 1. Should redirect to the homepage (or dashboard)
    cy.url().should('eq', Cypress.config().baseUrl + '/');

    // 2. Check for an element indicating logged-in state (e.g., logout button)
    // Adjust selector based on your AppHeader or layout component
    cy.get('button').contains('Log Out', { matchCase: false }).should('be.visible');

    // 3. Verify token exists in localStorage (optional but good)
    cy.window().its('localStorage').invoke('getItem', 'token').should('exist');
  });

  it('should display an error for invalid password complexity', () => {
    cy.get('#displayName').type('Test User InvalidPass');
    cy.get('#email').type(`invalidpass_${Date.now()}@example.com`);
    cy.get('#password').type('password'); // Does not meet complexity
    cy.get('#password').closest('.form-group').find('.error-message').should('be.visible');
    cy.get('button[type="submit"]').should('be.disabled');
  });

  it('should display an error for invalid phone number format', () => {
    cy.get('#phoneNumber').type('12345'); // Invalid format

    // Check for the error message related to phone number format
    cy.get('#phoneNumber').closest('.form-group').find('.error-message').should('be.visible');
    // cy.get('#phoneNumber').siblings('.error-message').should('contain', 'Phone number must be in international format');

    cy.get('#password').type(validPassword);
    cy.get('button[type="submit"]').should('not.be.disabled');
  });


  it('should display an error when trying to register with an existing email', () => {
    // Use an email known to exist from data.sql
    const existingEmail = 'alice@example.com';

    cy.get('#displayName').type('Existing Alice');
    cy.get('#email').type(existingEmail);
    cy.get('#password').type(validPassword);

    // Submit the form
    cy.get('form').submit();

    // Assertions for existing email error:
    // 1. Should remain on the registration page
    cy.url().should('include', '/register');

    // 2. An error message should be displayed in the form's error area
    // Adjust the selector based on where RegistrationForm.vue displays the general 'error' ref
    cy.get('.form-container .error').should('be.visible')
    // The exact text depends on the backend response, check for a likely substring
    .and('contain', 'Registration Failed');
  });
});

