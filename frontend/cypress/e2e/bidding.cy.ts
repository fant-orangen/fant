describe('Bidding Functionality', () => {
  // --- Test Users & Items ---
  const bidderUser = { // User who will place/update/delete bids
    email: 'alice@example.com',
    password: 'Password123'
  };
  const sellerUser = { // User who owns item 9
    email: 'jakob@mail.com',
    password: 'Password123'
  };
  const itemForBidding = { // Item listed by sellerUser (Jakob)
    id: '9',
    title: 'Diskotek',
    initialPrice: 9000000.00
  };
  const itemOwnedByBidder = { // Item listed by bidderUser (Alice)
    id: '1', // Assuming Item ID 1 is owned by User ID 1 (Alice)
    title: 'Test'
  };

  // --- Helper Function for Login ---
  const login = (email, password) => {
    cy.visit('/login');
    cy.get('#username').type(email);
    cy.get('#password').type(password);
    cy.get('form').submit();
    cy.url().should('eq', Cypress.config().baseUrl + '/');
    cy.contains('button', 'Log Out', { matchCase: false }).should('be.visible');
  };

  // --- Helper to Place a Bid via UI (used for setup in update/delete tests) ---
  const placeInitialBid = (itemId, amount, comment = 'Initial bid') => {
    cy.visit(`/item-detail/${itemId}`);
    cy.contains('.action-buttons button.edit-button', 'Place Bid').should('not.be.disabled').click();
    cy.get('.modal-content').should('be.visible');
    cy.get('#bid-amount').clear().type(amount.toString());
    if (comment) {
      cy.get('#bid-comment').clear().type(comment);
    }
    cy.get('.modal-content .submit-button').click();
    cy.get('.bid-success').should('be.visible'); // Wait for success state
    cy.get('.close-success-button').click();     // Close the modal
    cy.get('.modal-backdrop').should('not.exist');
    cy.log(`Placed initial bid of ${amount} on item ${itemId}`);
  }

  // --- Tests ---
  context('When logged in as a bidder', () => {
    beforeEach(() => {
      login(bidderUser.email, bidderUser.password);
    });

    it('should allow placing a bid on another user\'s item', () => {
      // Visit the item detail page
      cy.visit(`/item-detail/${itemForBidding.id}`);
      cy.contains('h1.item-title', itemForBidding.title).should('be.visible'); // Wait for page load

      // Find and click the "Place Bid" button
      cy.contains('.action-buttons button.edit-button', 'Place Bid', { matchCase: false })
      .should('be.visible')
      .and('not.be.disabled')
      .click();

      // --- Bid Modal Interaction ---
      cy.get('.modal-content').should('be.visible');

      // Find amount input, clear, and type
      const bidAmount = itemForBidding.initialPrice + 1000; // 9001000
      cy.get('#bid-amount')
      .should('be.visible')
      .clear()
      .type(bidAmount.toString());

      // Find comment textarea, clear, and type
      cy.get('#bid-comment')
      .should('be.visible')
      .clear()
      .type('Test comment from Cypress');

      // Submit the bid
      cy.get('.modal-content .submit-button')
      .should('not.be.disabled')
      .click();

      // --- Verification ---
      // Assert success message and close modal
      cy.get('.bid-success').should('be.visible');
      cy.contains('.bid-success p', 'Your bid has been placed successfully!').should('be.visible');
      cy.get('.close-success-button').should('be.visible').click();
      cy.get('.modal-backdrop').should('not.exist');

      // Navigate to "My Bids" page
      cy.visit('/profile/my-bids');

      // Find the bid card, assert details
      cy.contains('.bid-card-my', itemForBidding.title)
      .should('be.visible')
      .within(() => {
        // ** Use Robust Assertion **
        cy.contains('p', 'Your Bid Amount:')
        .should('be.visible')
        .invoke('text')
        .then((text) => {
          expect(text).to.include('kr');
          expect(text).to.include('9');
          expect(text).to.include('001');
          expect(text).to.include('000');
        });

        cy.contains('p', 'Your Comment:')
        .should('contain.text', 'Test comment from Cypress');

        cy.contains('.bid-status-badge', 'PENDING').should('be.visible');
      });
    });

    it('should allow updating a pending bid', () => {
      const initialBidAmount = itemForBidding.initialPrice + 500;
      const updatedBidAmount = initialBidAmount + 500;
      const updatedComment = 'Updated my offer!';

      // Setup: Place an initial bid first
      placeInitialBid(itemForBidding.id, initialBidAmount, 'Placing initial bid for update test');

      // Go to My Bids page
      cy.visit('/profile/my-bids');

      // Find the PENDING bid and click Update
      cy.contains('.bid-card-my', itemForBidding.title)
      .contains('.bid-status-badge', 'PENDING') // Ensure it's the pending one
      .parents('.bid-card-my') // Go up to the card container
      .within(() => {
        cy.contains('button.edit-button', 'Update Bid').click();
      });

      // --- Bid Modal Interaction (Update Mode) ---
      cy.get('.modal-content').should('be.visible');
      cy.contains('.modal-header h3', 'Update Your Bid').should('be.visible');

      // Verify initial amount is pre-filled
      cy.get('#bid-amount').should('have.value', initialBidAmount);

      // Update amount and comment
      cy.get('#bid-amount').clear().type(updatedBidAmount.toString());
      cy.get('#bid-comment').clear().type(updatedComment);

      // Submit update
      cy.get('.modal-content .submit-button').contains('Update Bid').click();

      // --- Verification ---
      // Wait for the modal to potentially disappear
      cy.get('.modal-backdrop').should('not.exist');

      // Verify on My Bids page (re-visit to ensure data refresh)
      cy.visit('/profile/my-bids');
      cy.contains('.bid-card-my', itemForBidding.title)
      .should('be.visible')
      .within(() => {
        // Assert Updated Amount (using robust check)
        cy.contains('p', 'Your Bid Amount:')
        .should('be.visible')
        .invoke('text')
        .then((text) => {
          expect(text).to.include('kr');
          expect(text).to.include('9');
          expect(text).to.include('001');
          expect(text).to.include('000');
        });
        // Assert Updated Comment
        cy.contains('p', 'Your Comment:')
        .should('contain.text', updatedComment);
        // Assert Status is still PENDING
        cy.contains('.bid-status-badge', 'PENDING').should('be.visible');
        // Assert Updated timestamp is visible
        cy.contains('p.bid-timestamp', 'Updated:').should('be.visible');
      });
    });

    it('should allow deleting a pending bid', () => {
      const bidToDeleteAmount = itemForBidding.initialPrice + 200;

      // Setup: Place an initial bid first
      placeInitialBid(itemForBidding.id, bidToDeleteAmount, 'Bid to be deleted');

      // Go to My Bids page
      cy.visit('/profile/my-bids');

      // Find the PENDING bid and click Delete
      cy.contains('.bid-card-my', itemForBidding.title)
      .contains('.bid-status-badge', 'PENDING')
      .parents('.bid-card-my')
      .within(() => {
        cy.contains('button.delete-button', 'Delete').click();
      });


      cy.log('>>> Waiting for ConfirmDeleteModal <<<');
      cy.get('.modal-content').should('be.visible');
      cy.contains('.modal-body p', 'Are you sure').should('be.visible');
      cy.get('.confirm-btn').contains('Delete Bid').click();

      // --- Verification ---
      // Assert modal closes
      cy.get('.modal-backdrop').should('not.exist');

      // Assert the bid card is gone from My Bids page
      cy.visit('/profile/my-bids'); // Re-visit for refresh
      // Check that the specific bid comment (or amount) no longer exists within any bid card
      cy.contains('.bid-card-my', 'Bid to be deleted').should('not.exist');
    });
  });


  // --- Test: Seller Cannot Bid ---
  context('When logged in as the seller', () => {
    beforeEach(() => {
      // Log in as the user who OWNS itemOwnedByBidder (Alice)
      login(bidderUser.email, bidderUser.password);
    });

    it('should disable the Place Bid button on their own item', () => {
      cy.visit(`/item-detail/${itemOwnedByBidder.id}`); // Visit item owned by Alice
      cy.contains('h1.item-title', itemOwnedByBidder.title).should('be.visible');

      // Assert Place Bid button is disabled
      cy.contains('.action-buttons button.edit-button', 'Place Bid', { matchCase: false })
      .should('be.visible')
      .and('be.disabled');

      // Assert seller notice is visible
      cy.contains('.seller-notice', 'You cannot bid on or contact the seller for your own item.', { matchCase: false }) // Check i18n key ERROR_BID_OWN_ITEM
      .should('be.visible');
    });
  });

  // --- Test: Non-Logged-In User Cannot Bid ---
  context('When not logged in', () => {
    it('should disable the Place Bid button and show login notice', () => {
      cy.visit(`/item-detail/${itemForBidding.id}`); // Visit any item
      cy.contains('h1.item-title', itemForBidding.title).should('be.visible');

      // Assert Place Bid button is disabled
      cy.contains('.action-buttons button.edit-button', 'Place Bid', { matchCase: false })
      .should('be.visible')
      .and('be.disabled');

      // Assert login notice is visible
      cy.contains('.login-notice', 'Please log in to place a bid or contact the seller.', { matchCase: false }) // Check i18n key ERROR_LOG_IN_TO_BID
    });
  });

});
