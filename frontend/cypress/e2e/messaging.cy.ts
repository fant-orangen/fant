describe('Messaging E2E Tests', () => {
  // --- Test Users & Items (Adjust based on your data.sql) ---
  const userA = {
    email: 'alice@example.com', // User who will initiate contact
    password: 'Password123',
  };
  const userB = {
    email: 'jakob@mail.com', // User who listed the item (seller) - Assuming ID 2 from data.sql
    displayName: 'jakob'     // Display name from data.sql
  };
  // *** IMPORTANT: Ensure item 10 is listed by user ID 1 (alice@example.com) ***
  // *** Choose an item listed by a DIFFERENT user (e.g., jakob, ID 2) ***
  // Let's assume item ID 27 ('Powerful gaming laptop') is listed by user 8 ('oliverb@example.com')
  // We'll need oliverb's details if we want to assert the name, or use jakob (user 2) as the seller and pick one of his items (e.g., item 9, Diskotek)
  // Let's use Item 9 listed by Jakob (User ID 2) for the test
  const itemListedByDifferentUser = {
    id: '9', // Item ID 9 ('Diskotek') listed by user 2 (jakob)
    title: 'Diskotek' // brief_description for item 9
  };
  const sellerOfItem9 = { // Details for Jakob (seller of item 9)
    email: 'jakob@mail.com',
    displayName: 'jakob'
  };


  // --- Helper Function for Login ---
  const login = (email, password) => {
    cy.visit('/login');
    cy.get('#username').type(email);
    cy.get('#password').type(password);
    cy.get('form').submit();
    cy.url().should('eq', Cypress.config().baseUrl + '/'); // Wait for redirect
    cy.contains('button', 'Log Out', { matchCase: false }).should('be.visible'); // Confirm login
  };

  // --- Test Suite ---

  beforeEach(() => {
    // Log in as User A before each test
    login(userA.email, userA.password);

    // Intercept relevant API calls
    cy.intercept('POST', '/api/messaging/conversations/initiate*').as('initiateConversation');
    cy.intercept('GET', '/api/messaging/conversations').as('getConversations');
    cy.intercept('GET', '/api/messaging/messages*').as('getMessages');
    cy.intercept('GET', '/api/items/details/*').as('getItemDetails');
    cy.intercept('GET', '/api/users/profile').as('getUserProfile'); // Keep intercept for debugging if needed, but don't wait below
  });

  it('should start a new conversation from an item detail page', () => {
    // Visit the item detail page listed by a different user
    cy.visit(`/item-detail/${itemListedByDifferentUser.id}`);
    cy.wait('@getItemDetails');
    // cy.wait('@getUserProfile'); // REMOVED - This was causing the timeout

    // Find and click the "Contact Seller" button
    // Need to wait for the button to potentially become enabled after checks
    cy.contains('.action-buttons button.edit-button', 'Contact Seller', { matchCase: false })
    .should('be.visible') // Ensure it's rendered first
    .and('not.be.disabled') // Then check if it's enabled
    .click({ force: true }); // Use force: true if visibility/disable checks are flaky

    // Wait for the backend to initiate/find the conversation
    cy.wait('@initiateConversation').its('response.statusCode').should('eq', 200);

    // Assert redirection to the conversation view
    cy.url().should('match', /\/messages\/\d+$/); // Asserts URL looks like /messages/123

    // Verify elements on the ConversationView load
    cy.wait('@getMessages'); // Wait for messages to be fetched
    cy.contains('h2.chat-partner-name', sellerOfItem9.displayName).should('be.visible'); // Check chat partner name
    cy.contains('.item-title-link h3', itemListedByDifferentUser.title).should('be.visible'); // Check item title
    cy.get('textarea[placeholder*="Type your message"]').should('be.visible');
  });

  it('should display conversations in the inbox and navigate to one', () => {
    // Pre-condition: Ensure at least one conversation exists for userA
    // We can initiate one here to be sure
    cy.request({
      method: 'POST',
      url: `/api/messaging/conversations/initiate?itemId=${itemListedByDifferentUser.id}`,
      headers: { 'Authorization': `Bearer ${localStorage.getItem('token')}` }
    }).its('status').should('eq', 200);


    cy.visit('/messages'); // Navigate to the inbox
    cy.wait('@getConversations'); // Wait for conversations to load

    // Assert the inbox title is visible
    cy.contains('h1', 'Inbox').should('be.visible'); // Adjust text based on i18n

    // Check that at least one conversation item exists
    cy.get('.conversation-list .conversation-item')
    .should('have.length.greaterThan', 0);

    // Find the conversation related to the specific item/user if possible
    cy.contains('.conversation-item .other-user', sellerOfItem9.displayName) // Find by seller name
    .parents('.conversation-item') // Go up to the item container
    .should('contain.text', itemListedByDifferentUser.title) // Check item title within the same container
    .click();

    // Assert navigation to the conversation view
    cy.url().should('match', /\/messages\/\d+$/);

    // Verify elements on the ConversationView load again
    cy.wait('@getMessages');
    cy.get('.conversation-view').should('be.visible');
    cy.get('textarea[placeholder*="Type your message"]').should('be.visible');
  });

  it('should send a message within a conversation', () => {
    // Pre-condition: A conversation must exist. Navigate to it.
    // Initiate conversation first to ensure it exists and we land on the right page
    cy.visit(`/item-detail/${itemListedByDifferentUser.id}`);
    cy.wait('@getItemDetails');
    // cy.wait('@getUserProfile'); // REMOVED - This was causing the timeout
    cy.contains('.action-buttons button.edit-button', 'Contact Seller', { matchCase: false })
    .should('be.visible')
    .and('not.be.disabled')
    .click({ force: true }); // Use force: true if needed
    cy.wait('@initiateConversation');
    cy.url().should('match', /\/messages\/\d+$/);
    cy.wait('@getMessages'); // Wait for initial messages to load

    // Type a message
    const testMessage = `Hello from Cypress! ${Date.now()}`;
    cy.get('.message-input-area textarea')
    .should('be.visible') // Ensure textarea is ready
    .type(testMessage);

    // Click send
    cy.get('.message-input-area button.edit-button')
    .contains('Send', { matchCase: false }) // Adjust text based on i18n
    .should('not.be.disabled')
    .click();

    // Assert the message appears in the sent messages list
    // Wait a short moment for potential optimistic update or websocket round trip
    cy.wait(500); // Adjust timing if needed
    cy.get('.messages-container')
    .should('contain.text', testMessage);

    // Optionally, assert it's in a 'sent' bubble
    cy.contains('.message-bubble-wrapper.sent .message-content', testMessage)
    .should('be.visible');

    // Assert the input area is cleared
    cy.get('.message-input-area textarea').should('have.value', '');
  });

});
