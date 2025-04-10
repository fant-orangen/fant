# frontend

This guide will help you start developing on the backend of this project.
The frontend is built with Vue 3 in Vite.

## Recommended IDE Setup

[VSCode](https://code.visualstudio.com/) + [Volar](https://marketplace.visualstudio.com/items?itemName=Vue.volar) (and disable Vetur).

## Prerequisites

- Node.js v18+


## Installation
After cloning into the repository
```bash
  git clone https://github.com/fant-orangen/fant.git
```
Navigate to the frontend folder
```bash
  cd fant/frontend
```

Then install the required node modules

```bash
  npm install
```

You can then run your own development-server (default is http://localhost:5173)
```bash
  npm run dev
```
## Running Tests

To run the unit tests using Vitest, run the following command

```bash
  npm test:unit
```

For the end-to-end tests using cypress, run the following command. Note that you need to have the backend of this project running on http://localhost:8080 for the tests to work.
```bash
  npm test:e2e
```
