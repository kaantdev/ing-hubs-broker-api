# Broker API

Broker API is a **Spring Boot 3.4.3** microservice application designed to manage financial asset transactions through a REST API. This project was developed as part of the ING Hubs Case Study.
## Getting Started

### Dependencies

This project uses the following technologies:

- **Java 17+**
- **Spring Boot 3.4.3**
- **Spring Web**
- **Spring Data JPA**
- **Spring Security**
- **Lombok**
- **H2 Database**
- **Maven**

### Installation & Running

#### 1️⃣ Clone the Repository

```sh
git clone https://github.com/your_username/broker-api.git
cd broker-api
```

#### 2️⃣ Install Dependencies

```sh
mvn clean install
```

#### 3️⃣ Run the Application

```sh
mvn spring-boot:run
```

Alternatively, you can package it and run it as a `.jar` file:

```sh
mvn package
java -jar target/broker-api-0.0.1-SNAPSHOT.jar
```

## Configuration

The application can be configured via the `application.properties` file. Example:

```properties
spring.application.name=broker-api
server.port=8080
server.servlet.context-path=/broker-app
app.valid-assets=AAPL,TSLA,BTC,ETH,GOLD,SILVER
```

The available **asset names** are listed in the `application.properties` file.

## API Documentation

All available API endpoints are documented using **Swagger UI**.

Once the application is running, you can access the documentation at:

**Swagger UI:** [`http://localhost:8080/broker-app/swagger-ui.html`](http://localhost:8080/broker-app/swagger-ui.html)

## Authentication & Security

- The API uses **JWT Bearer Token** authentication.
- To access protected endpoints, include the token in the `Authorization` header:

```http
Authorization: Bearer <your_token>
```

- **Role-Based Access Control (RBAC)** is implemented.
- The system ensures that the token owner matches the user for whom an operation is requested, preventing unauthorized actions on other accounts.

## Architecture

- **Layered Architecture**: The project follows a layered architecture with Controller, Service, and Repository layers.
- **JWT Authentication**: Secure authentication is implemented using JSON Web Token (JWT).

## Default Users

By default, two users are created using `CommandLineRunner`:

| Username | Password  | Role  |
|----------|----------|-------|
| `user`   | `user123` | Standard User |
| `admin`  | `admin123` | Administrator |

To perform transactions, first **deposit funds** using the `/api/v1/assets/deposit` endpoint.

## H2 Database Console

The application includes an embedded H2 database for development and testing purposes.

Once the application is running, you can access the H2 database console at:

**H2 Console:** [`http://localhost:8080/broker-app/h2-console/`](http://localhost:8080/broker-app/h2-console/)

Use the following credentials to log in:

- **Username:** `sa`
- **Password:** `passing`

Ensure that the JDBC URL is set to: `jdbc:h2:mem:brokerapi`
