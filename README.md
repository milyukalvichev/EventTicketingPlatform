# 🎟️ EventPass - Distributed Event Ticketing Platform

A full-stack, distributed event ticketing platform built with **Spring Boot**, **Spring Security (BCrypt / RBAC)**, **Spring Data JPA**, **Thymeleaf**, **Bootstrap 5**, and a dedicated **Pricing Microservice** communicating via synchronous REST (`RestClient`).

---

## 🏛️ System Architecture

The solution uses a decoupled two-service microservice pattern communicating over HTTP:

```
+------------------------------------------------------------------------+
|                            CLIENT BROWSER                              |
+-----------------------------------+------------------------------------+
                                    |
                                    | HTTP (Port 8080)
                                    v
+------------------------------------------------------------------------+
|                      MAIN APPLICATION (Port 8080)                      |
|  - Role-Based Access Control (ROLE_USER, ROLE_ADMIN)                   |
|  - Event, Venue, User & Ticket Domain Management                       |
|  - Multi-ticket checkout & dynamic venue capacity engine               |
|  - Thymeleaf UI (15 responsive views with custom CSS)                  |
|  - Ticket refund & return lifecycle                                    |
+-----------------------------------+------------------------------------+
                                    |
                                    | Synchronous REST (RestClient)
                                    | http://localhost:8081/api/promos
                                    v
+------------------------------------------------------------------------+
|                 PRICING MICROSERVICE (Port 8081)                       |
|  - Dedicated promotional code validation engine                        |
|  - Discount percentages & expiration date auditing                     |
|  - REST CRUD API for promo management (create, update, toggle, delete) |
+------------------------------------------------------------------------+

```

---

## 🛠️ Tech Stack & Requirements

* **Language:** Java 21 / 22
* **Framework:** Spring Boot 3+ / 4+
* **Security:** Spring Security (Form Login, Session Invalidation, BCrypt Hashing, Route Authorization)
* **Persistence & ORM:** Spring Data JPA, Hibernate, HikariCP
* **Databases:** MySQL 8+ / 9+ (Separate schemas: `ticketing_main_db`, `ticketing_pricing_db`)
* **Inter-Service Communication:** Spring `RestClient` (Synchronous HTTP)
* **Frontend:** Thymeleaf, Bootstrap 5.3, Custom CSS
* **Testing:** JUnit 5, Mockito

---

## ✨ Core Features

### 1. Catalog & Real-Time Capacity Tracking

* **Keyword Search:** Instant filtering across event titles, venue names, and hosting cities.
* **Live Seat Counters:** Remaining ticket availability (`X left`) updates dynamically on each purchase or return.
* **Sold-Out Guard:** Automatically disables checkout buttons and blocks transactions when venue capacity is exhausted.

### 2. User Experience & Orders

* **Multi-Ticket Checkout:** Select from 1 to 10 tickets per transaction (capped by remaining capacity).
* **Live Promo Code Evaluation:** Connected to the pricing microservice for instant feedback on expired/invalid codes, displaying strikethrough original prices, discount totals, and final EUR amounts.
* **Self-Service Returns:** Users can cancel and refund individual tickets from their order dashboard, immediately releasing that capacity back to the public catalog.
* **Automatic Sign-In:** Instant authentication and session establishment right upon registration.

### 3. Comprehensive Admin Control

* **Event Management:** Full CRUD operations for concerts, summits, and festivals.
* **Venue Directory:** Manage venues independently or create custom venues on the fly when creating an event.
* **Promo Code Administration:** Add, edit, toggle active/inactive status, or delete discount codes directly through the microservice REST integration.

---

## 🔑 Pre-Seeded Accounts & Test Data

The application auto-seeds sample data on first boot via a `CommandLineRunner`:

| Username | Password | Role | Permissions |
| --- | --- | --- | --- |
| `admin` | `password123` | `ROLE_ADMIN` | Full CRUD over Events, Venues, and Promo Codes |
| `john_doe` | `password123` | `ROLE_USER` | Browse, Search, Book, and Return Tickets |

### Pre-Configured Promo Codes (Microservice):

* `SUMMER20` - **20% OFF** (Active)
* `EARLYBIRD` - **15% OFF** (Active)
* `EXPIRED50` - **50% OFF** (Expired — tests validation error feedback)

---

## 🚀 Setup & Execution Guide

### 1. Prerequisites

* **Java 21+** installed (`java -version`)
* **Maven 3.9+** installed (`mvn -version`)
* **MySQL** running locally on port `3306`

### 2. Database Configuration

Ensure your local MySQL credentials match the `application.properties` in both applications:

* **`pricing-microservice/src/main/resources/application.properties`**:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/ticketing_pricing_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=your_mysql_password

```


* **`main-app/src/main/resources/application.properties`**:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/ticketing_main_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=your_mysql_password
pricing.service.url=http://localhost:8081

```



### 3. Starting the Applications

#### Terminal 1 — Launch Pricing Microservice (Port 8081):

```bash
cd pricing-microservice
mvn clean spring-boot:run

```

#### Terminal 2 — Launch Main Application (Port 8080):

```bash
cd main-app
mvn clean spring-boot:run

```

Access the web interface at **`http://localhost:8080`**.

---

## 🧪 Running Automated Unit Tests

Run test suites for both modules using Maven:

```bash
# Test Pricing Microservice
cd pricing-microservice && mvn test

# Test Main App
cd ../main-app && mvn test

```

### Unit Test Coverage:

* `TicketServiceTest` — Multi-ticket purchase validation, sold-out capacity exceptions, authorized user refund processing, and unauthorized access rejections.
* `UserServiceTest` — Password matching validation, duplicate username/email verification, and BCrypt encryption.
* `PromoServiceTest` — Active promo calculation, expired promo handling, and edge-case validation.

