# Jeevadaana — Blood Donation Management System

A full-stack **Blood Donation Management System** built as a college DBMS project.
Donors can register, discover donation camps by district and sign up for them;
organizers can create camps, manage registrations and record donations.

## Tech Stack

| Layer | Technology |
|-------|------------|
| Backend | Java 17, Spring Boot 3.3 (Spring MVC, Spring Data JPA) |
| Frontend | Thymeleaf, HTML, CSS, Bootstrap 5, JavaScript |
| Database | MySQL 8 |
| Build | Maven |
| Auth | Session-based, BCrypt password hashing |

## Features / Modules

1. **Donor registration & login**
2. **Organizer registration & login**
3. **Blood donation camp organization** (create / edit / cancel camps)
4. **Camp registration** for donors (with capacity & duplicate checks)
5. **Donor dashboard** — donation history + nearby camps (by district)
6. **Organizer dashboard** — camp stats & management
7. **Post-camp management** — record donations for registered donors
8. **District-wise camp search** (public and donor-facing)
9. **Donation history**
10. **MySQL database integration**

## Architecture (clean MVC)

```
com.jeevadaana
├── config       # SecurityConfig, WebConfig, AuthInterceptor, DataSeeder
├── controller   # Home, Donor, Organizer controllers + exception handler
├── dto          # Form-backing / validation objects
├── model        # JPA entities + enums
├── repository   # Spring Data JPA repositories
└── service      # Business logic (transactional)
```

Database design and ER diagram: [`docs/ER_DIAGRAM.md`](docs/ER_DIAGRAM.md).
Canonical SQL DDL: [`db/schema.sql`](db/schema.sql).

## Getting Started

### Prerequisites
- Java 17+
- Maven 3.6+
- MySQL 8 running locally

### 1. Create the database

```sql
CREATE DATABASE jeevadaana;
CREATE USER 'jeevadaana'@'localhost' IDENTIFIED BY 'jeevadaana';
GRANT ALL PRIVILEGES ON jeevadaana.* TO 'jeevadaana'@'localhost';
FLUSH PRIVILEGES;
```

Hibernate creates the tables automatically on first run (`ddl-auto=update`).
Alternatively, run [`db/schema.sql`](db/schema.sql) manually.

### 2. Configure (optional)

Connection settings are read from environment variables with sensible defaults:

| Variable | Default |
|----------|---------|
| `DB_HOST` | `localhost` |
| `DB_PORT` | `3306` |
| `DB_NAME` | `jeevadaana` |
| `DB_USERNAME` | `jeevadaana` |
| `DB_PASSWORD` | `jeevadaana` |
| `PORT` | `8080` |

### 3. Run

```bash
mvn spring-boot:run
```

Open <http://localhost:8080>.

### Demo accounts (seeded on first run)

| Role | Email | Password |
|------|-------|----------|
| Donor | `donor@jeevadaana.org` | `password` |
| Organizer | `organizer@jeevadaana.org` | `password` |

## Build & Test

```bash
mvn clean package      # builds target/jeevadaana.jar
mvn test               # runs tests against in-memory H2
```
