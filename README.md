
# fant.
This project was developed as a submisson for IDATT2105 at NTNU Trondheim during spring of 2025. The product is a ‘finn.no´ like e-commerce marketplace – a web application where sellers can list their
items and potential buyers can browse or search the items using various filters, can add items into their
favorite lists, can communicate with sellers, and can also buy items!


## Authors

- [@MrMarHVD](https://github.com/MrMarHVD)
- [@mageik360](https://github.com/mageik360)
- [@nicktuf](https://github.com/nicktuf)
- [@jakobhuuse](https://github.com/jakobhuuse)


## Name
The application name is a play on words from the popular norwegian marketplace "finn.no".
## Overview

The project is divided into two parts; a frontend application using Vue, and a backend application using Spring Boot. For more information about these two parts, like how to run their tests, visit their respective sub-repositories.

- [Frontend](frontend)
- [Backend](backend)
## Run Locally
### Prerequisites
- Docker

Clone the project

```bash
  git clone https://github.com/fant-orangen/fant.git
```

Go to the project directory

```bash
  cd fant
```

If you have Docker installed
```bash
  docker compose up -d
```
This will run two docker-containers on your system, one for the frontend and one for the backend. The application will then be available on http://localhost:80

## CI
This project uses workflows with GitHub actions to do continous integration tests for both frontend and backend.

### Frontend
On any changes to the frontend sub-repository, the Frontend CI workflow will run. It looks like this:
- Set up the environment
- Build
- Unit tests
- Start backend server
- End-to-end tests
This will be done with Node.js versions 18, 20 and 22.

### Backend
On any changes to the backend sub-repository, the Backend CI workflow will run. It looks like this:
- Set up the environment
- Build and test
- Upload the test report as artifact
