
# backend

This guide will help you start developing on the backend of this project.
The backend of this project is build using Spring Boot with Maven.


## Prerequisites

- Java JDK 21+


## Installation
After cloning into the repository
```bash
  git clone https://github.com/fant-orangen/fant.git
```
Navigate to the backend folder
```bash
  cd fant/backend
```

Then install the required Maven dependencies

```bash
  ./mvnw clean
```

You can then run your own development-server (default is http://localhost:8080)
```bash
  ./mvnw spring-boot:run
```
## Running Tests

To run tests, run the following command

```bash
  ./mvnw clean test
```
This will create a jacoco.exec file that can be found at target/jacoco.exec folder, which you can then import into your preferred IDE that supports it to view the code coverage. Alternatively, you can use the index.html file found at target/site/jacoco/index.html.

