# Architecture — TravelExplorer

## High-level architecture

```text
                         Browser
                            |
                     http://localhost:8090
                            |
                     +--------------+
                     |  API Gateway |
                     |     :8080    |
                     +------+-------+
                            |
          +-----------------+------------------+
          |          |            |            |
          v          v            v            v
       Auth       Package      Booking      Payment
      :8084        :8081        :8082        :8083
        |             |            |            |
      H2 DB         H2 DB        H2 DB        H2 DB
                     ^
                     |
              Booking Service
              reads package data
```

## Request flow

### Authentication

Browser → Gateway → Auth Service → H2 user database.

The Auth Service stores BCrypt password hashes and issues a random session token at login. The Gateway validates the token before protected booking/payment operations.

### Package discovery

Browser → Gateway → Package Service → H2.

### Booking

Browser → Gateway → Auth validation → Booking Service → Package Service → H2.

The Booking Service uses the **Factory Pattern** to create a booking according to the selected booking type.

### Payment

Browser → Gateway → Auth validation → Payment Service → H2.

The payment is simulated and returns a transaction ID and SUCCESS status.

## Patterns

### Builder Pattern

`TravelPackage.Builder` allows readable construction of packages with optional values such as hotel, duration, transport and sightseeing.

### Factory Pattern

`BookingFactory.create(...)` centralizes creation of TOUR, HOTEL and FLIGHT booking objects.

## Why microservices?

The system separates authentication, package management, booking and payment into independently maintainable services. This demonstrates service boundaries, REST communication, containerization and independent data ownership for an academic architecture project.
