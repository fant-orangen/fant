
# fant.

A marketplace for selling and buying second-hand items.




## Authors

- [@MrMarHVD](https://github.com/MrMarHVD)
- [@mageik360](https://github.com/mageik360)
- [@nicktuf](https://github.com/nicktuf)
- [@jakobhuuse](https://github.com/jakobhuuse)


## Name
The application name is a play on words from the popular norwegian marketplace "finn.no".
## Overview

The project is divided into two parts; a frontend application using Vue and a backend application using Spring Boot. For more information about these two parts, like how to run their tests, visit their respective sub-repositories.

- [Frontend](https://github.com/fant-orangen/fant/tree/dev/frontend)
- [Backend](https://github.com/fant-orangen/fant/tree/dev/backend)
## Run Locally
### Prerequisites
- Docker

Clone the project

```bash
  git clone https://github.com/fant-orangen/fant
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
