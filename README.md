# BRICO — Platforma za upravljanje frizerskim salonima

Mikroservisna web platforma koja digitalizuje poslovanje frizerskih salona i unapređuje iskustvo klijenata pri pronalasku i rezervaciji frizerskih usluga.

## Tim

| Ime i prezime | Uloga |
|---|---|
| Kerim Fadzan | Backend developer |
| Lejla Ahmethodzic | Backend developer |
| Berin Mujkic | Backend developer |
| Nermin Djulepa | Frontend developer |

## Arhitektura

```
                        ┌─────────────┐
                        │  Frontend   │
                        │  (React/TS) │
                        └──────┬──────┘
                               │
                        ┌──────▼──────┐
                        │ API Gateway │ :8080
                        └──────┬──────┘
                               │
          ┌────────────────────┼────────────────────┐
          │                    │                    │
   ┌──────▼──────┐    ┌────────▼──────┐    ┌───────▼──────┐
   │ user-service│    │salon-service  │    │appointment-  │
   │   :8081     │    │   :8082       │    │service :8083 │
   └─────────────┘    └───────────────┘    └──────┬───────┘
                                                  │ RabbitMQ
                                           ┌──────▼───────┐
                                           │notification- │
                                           │service :8084 │
                                           └──────────────┘
   ┌─────────────┐    ┌───────────────┐
   │system-events│◄───│ (gRPC :9090)  │
   │service :8085│    └───────────────┘
   └─────────────┘

   Infrastructure: Eureka :8761 | Config Server :8888
   Databases: PostgreSQL :5432 | MongoDB :27017 | RabbitMQ :5672
```

## Mikroservisi

| Servis | Port | Baza podataka | Opis |
|---|---|---|---|
| eureka-server | 8761 | — | Service discovery |
| config-server | 8888 | — | Centralizovana konfiguracija |
| api-gateway | 8080 | — | Gateway, JWT autentikacija, routing |
| user-service | 8081 | PostgreSQL (brico_users) | Korisnici, autentikacija, JWT |
| salon-service | 8082 | PostgreSQL (brico_salons) | Saloni, frizeri, usluge, radno vrijeme |
| appointment-service | 8083 | PostgreSQL (brico_appointments) | Rezervacije, recenzije |
| notification-service | 8084 | PostgreSQL (brico_notifications) | Email notifikacije, saga events |
| system-events-service | 8085 | MongoDB (brico_events) | Audit log via gRPC |

## Tehnologije

**Backend:** Java 17, Spring Boot 3.3.5, Spring Cloud 2023.0.3
**Komunikacija:** REST (OpenFeign), RabbitMQ (saga choreography), gRPC
**Sigurnost:** Spring Security, JWT (JJWT 0.12.6)
**Baze:** PostgreSQL, MongoDB
**Build:** Maven multi-module
**Frontend:** React 19, TypeScript, Vite, Tailwind CSS, shadcn/ui

## Pokretanje putem Dockera

### Preduvjeti
- [Docker Desktop](https://www.docker.com/products/docker-desktop/) instaliran i pokrenut
- Git

### Koraci

```bash
# 1. Klonirajte repozitorij
git clone <repo-url>
cd nwt

# 2. Pokrenite sve servise
docker compose up --build

# 3. Sačekajte da se svi servisi podignu (~3-5 minuta pri prvom pokretanju)
#    Možete pratiti status na Eureka dashboardu:
#    http://localhost:8761

# 4. Aplikacija je dostupna na:
#    Frontend:      http://localhost:3000
#    API Gateway:   http://localhost:8080
#    Eureka:        http://localhost:8761
#    RabbitMQ UI:   http://localhost:15672  (guest/guest)
```

### Zaustavljanje

```bash
docker compose down

# Za brisanje i podataka (baze):
docker compose down -v
```

### Redoslijed pokretanja (automatski putem docker-compose)

1. PostgreSQL, MongoDB, RabbitMQ
2. Eureka Server
3. Config Server
4. Microservisi (user, salon, system-events)
5. Appointment Service, Notification Service
6. API Gateway
7. Frontend

## Lokalni razvoj (bez Dockera)

### Preduvjeti
- Java 17 JDK
- Maven 3.8+
- PostgreSQL 16 (lokalno ili Docker)
- MongoDB 7 (lokalno ili Docker)
- RabbitMQ 3 (lokalno ili Docker)

### Baze podataka

```bash
# PostgreSQL — kreirajte baze i korisnika:
psql -U postgres -c "CREATE USER brico WITH PASSWORD 'brico_pass';"
psql -U postgres -c "CREATE DATABASE brico_users OWNER brico;"
psql -U postgres -c "CREATE DATABASE brico_salons OWNER brico;"
psql -U postgres -c "CREATE DATABASE brico_appointments OWNER brico;"
psql -U postgres -c "CREATE DATABASE brico_notifications OWNER brico;"
```

### Pokretanje servisa (redoslijed je bitan)

```bash
# 1. Eureka Server
cd eureka-server && mvn spring-boot:run

# 2. Config Server
cd config-server && mvn spring-boot:run

# 3. Mikroservisi (svaki u zasebnom terminalu)
cd user-service && mvn spring-boot:run
cd salon-service && mvn spring-boot:run
cd system-events-service && mvn spring-boot:run
cd appointment-service && mvn spring-boot:run
cd notification-service && mvn spring-boot:run
cd api-gateway && mvn spring-boot:run

# 4. Frontend
cd frontend/brico && npm install && npm run dev
# Frontend dostupan na http://localhost:5173
```

## Testovi

```bash
# Svi testovi
mvn test

# Testovi za specifičan servis
cd user-service && mvn test
cd salon-service && mvn test
cd appointment-service && mvn test
cd notification-service && mvn test
```

## Asinhorna komunikacija (Saga Choreography)

Rezervacija termina (`POST /api/appointments`) koristi RabbitMQ saga pattern:

1. **appointment-service** sprema rezervaciju (status: `PENDING`) i šalje `AppointmentBookedEvent`
2. **notification-service** prima event, kreira notifikaciju, šalje email, odgovara `NotificationSuccessEvent`
3. **appointment-service** prima potvrdu i mijenja status u `CONFIRMED`
4. U slučaju greške: notification-service šalje `NotificationFailedEvent` koji pokreće rollback

API odmah vraća `202 ACCEPTED` sa porukom "Booking initiated. You will be notified once confirmed."

## API Endpoints (putem API Gatewaya na :8080)

| Method | Endpoint | Opis |
|---|---|---|
| POST | `/api/auth/register` | Registracija |
| POST | `/api/auth/login` | Login (vraća JWT) |
| GET | `/api/salons?city=Sarajevo` | Pretraga salona |
| GET | `/api/salons/{id}` | Detalji salona |
| POST | `/api/appointments` | Rezervacija termina |
| GET | `/api/appointments/client/{id}` | Rezervacije klijenta |
| GET | `/api/appointments/available-slots?hairdresserId=1&date=2026-04-07` | Slobodni termini |
| POST | `/api/reviews` | Ostavi recenziju |
| GET | `/api/reviews/salon/{id}` | Recenzije salona |
