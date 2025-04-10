// cypress/support/commands.ts

/// <reference types="cypress" />

// --- Add the login command definition ---
Cypress.Commands.add('login', (email, password) => {
  cy.session([email, password], () => {
    cy.visit('/login');
    cy.get('#username').type(email); // ID from LoginForm.vue
    cy.get('#password').type(password); // ID from LoginForm.vue
    cy.get('form').submit();

    // Assertions after successful login (ensures login completed before moving on)
    cy.url().should('eq', Cypress.config().baseUrl + '/'); // Check redirect
    cy.contains('button', 'Log Out', { matchCase: false }).should('be.visible'); // Check logged-in state
  }, {
    // Cache the session based on email to speed up tests
    cacheAcrossSpecs: true
  });
  // After cy.session restores or creates the session, visit the root again
  // or the page you intend to test next. Often the beforeEach hook handles the visit.
  // cy.visit('/'); // You might not need this visit here if beforeEach handles it
});
// --- End of login command definition ---


// --- Add TypeScript definitions for custom commands ---
declare global {
  namespace Cypress {
    interface Chainable {
      /**
       * Custom command to log in a user via the UI.
       * Caches the session for efficiency.
       * @param email The user's email address.
       * @param password The user's password.
       * @example cy.login('test@example.com', 'password123')
       */
      login(email: string, password: string): Chainable<void>
      // Add other custom command definitions here if needed
      // drag(subject: string, options?: Partial<TypeOptions>): Chainable<Element>
      // dismiss(subject: string, options?: Partial<TypeOptions>): Chainable<Element>
      // visit(originalFn: CommandOriginalFn, url: string, options: Partial<VisitOptions>): Chainable<Element>
    }
  }
}

// Keep this export to make it a module
export {}
