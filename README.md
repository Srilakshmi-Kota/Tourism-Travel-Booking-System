# TravelExplorer — Tourism & Travel Booking System

A complete academic Tourism & Travel Booking System demonstrating **Microservices Architecture**, **REST APIs**, **Factory Pattern**, and **Builder Pattern** with a polished multi-step web application.

## Application flow

1. Landing / Home
2. Register / Login
3. Explore Packages
4. Package Details
5. Booking Details
6. Payment (simulated)
7. Booking Confirmation
8. My Bookings

## Services

- **API Gateway** — browser-facing API and static frontend (`8090 -> 8080`)
- **Auth Service** — registration, BCrypt password hashing, login, session token validation (`8084`)
- **Package Service** — travel package catalogue (`8081`)
- **Booking Service** — bookings, user bookings and cancellation (`8082`)
- **Payment Service** — simulated payments and transaction IDs (`8083`)

Each backend service has its own H2 database.

## Design patterns

- **Builder Pattern:** `TravelPackage.Builder` constructs travel packages with optional attributes.
- **Factory Pattern:** `BookingFactory` creates booking objects based on TOUR, HOTEL or FLIGHT type.

## Run with Docker

From the project root:

```bash
docker compose down
docker compose build --no-cache
docker compose up
```

Open:

**http://localhost:8090**

## Demo account

Create your own account through the Register page. Passwords are BCrypt-hashed by the Auth Service.

## Important Docker networking note

Inside Docker, services communicate using Compose service names, not `localhost`:

```text
api-gateway    -> package-service:8081
api-gateway    -> booking-service:8082
api-gateway    -> payment-service:8083
api-gateway    -> auth-service:8084
booking-service -> package-service:8081
```

The browser uses `http://localhost:8090` because Compose maps host port 8090 to the Gateway's internal port 8080.

## Project structure

```text
tourism-travel-booking-system/
├── api-gateway/
├── auth-service/
├── package-service/
├── booking-service/
├── payment-service/
├── postman/
├── docs/
└── docker-compose.yml
```
