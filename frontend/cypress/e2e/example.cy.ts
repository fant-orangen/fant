// https://on.cypress.io/api

describe('My First Test', () => {
  it('visits the app preview url', () => {
    cy.visit('/')
    cy.get('section.homepage').should('exist')
  })
})
