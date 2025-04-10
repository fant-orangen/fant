// ***********************************************************
// This example support/index.js is processed and
// loaded automatically before your test files.
//
// This is a great place to put global configuration and
// behavior that modifies Cypress.
//
// You can change the location of this file or turn off
// automatically serving support files with the
// 'supportFile' configuration option.
//
// You can read more here:
// https://on.cypress.io/configuration
// ***********************************************************

// Import commands.js using ES2015 syntax:
import './commands'

// Alternatively you can use CommonJS syntax:
// require('./commands')

import './commands';

// Add this event listener
Cypress.on('uncaught:exception', (err, runnable) => {
  // We expect a specific error message from the application code
  // Prevent Cypress from failing the test if this specific error occurs
  if (err.message.includes("Cannot read properties of null (reading 'document')")) {
    // returning false here prevents Cypress from failing the test
    return false;
  }
  // Allow other uncaught exceptions to fail the test
  return true;
});
