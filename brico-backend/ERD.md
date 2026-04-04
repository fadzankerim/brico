# ERD Dijagrami — Brico Mikroservisi

> Svaki mikroservis posjeduje **vlastitu bazu podataka** (Database per Service pattern).
> Relacije između servisa se ostvaruju logički putem ID-ova, bez stranih ključeva na nivou baze.

---

## 1. USER SERVICE (port 8081 | baza: `brico_users`)

```
┌──────────────────────────────────────────────┐
│                    users                     │
├──────────────────────────────────────────────┤
│ PK  id              BIGINT  AUTO_INCREMENT   │
│     email           VARCHAR(150) UNIQUE NN   │
│     password        VARCHAR     NN           │
│     full_name       VARCHAR(100) NN          │
│     phone           VARCHAR(20)              │
│     profile_photo   VARCHAR                  │
│     role            ENUM(CLIENT, HAIRDRESSER,│
│                     SALON_OWNER, ADMIN)  NN  │
│     email_verified  BOOLEAN  DEFAULT false   │
│     created_at      TIMESTAMP               │
└──────────────────────────────────────────────┘
```

**Seed podaci:** 1 admin, 2 vlasnika, 3 frizera, 3 klijenta = 9 korisnika

---

## 2. SALON SERVICE (port 8082 | baza: `brico_salons`)

```
┌──────────────────────────────────────────────┐
│                    salons                    │
├──────────────────────────────────────────────┤
│ PK  id                  BIGINT              │
│     name                VARCHAR(100) NN      │
│     slug                VARCHAR(120) UNIQUE  │
│     description         VARCHAR(500)         │
│     city                VARCHAR(60)  NN      │
│     address             VARCHAR(200) NN      │
│     latitude            DECIMAL(10,7)        │
│     longitude           DECIMAL(10,7)        │
│     phone               VARCHAR(20)          │
│     website             VARCHAR(200)         │
│     verified            BOOLEAN  DEFAULT F   │
│     is_active           BOOLEAN  DEFAULT T   │
│ FK* owner_id            BIGINT  → users.id   │  *logička veza
│ FK* subscription_plan_id BIGINT              │  *logička veza
│     stripe_customer_id  VARCHAR(60)          │
│     created_at          TIMESTAMP            │
└──────────────────────────────────────────────┘
         │ 1                   │ 1
    ┌────┘ N              ┌────┘ N
    ▼                     ▼
┌────────────────┐   ┌─────────────────────────┐
│  hairdressers  │   │        services          │
├────────────────┤   ├─────────────────────────┤
│ PK id  BIGINT  │   │ PK id          BIGINT   │
│ FK salon_id    │   │ FK salon_id    BIGINT   │
│ FK*user_id     │   │    name        VARCHAR  │
│    full_name   │   │    description VARCHAR  │
│    bio         │   │    price       DECIMAL  │
│    specialties │   │    duration_   INTEGER  │
│    profile_    │   │    minutes              │
│    photo       │   │    is_active   BOOLEAN  │
│    is_active   │   └─────────────────────────┘
│    created_at  │
└────────────────┘

┌───────────────────────────────────────────────┐
│                 working_hours                 │
├───────────────────────────────────────────────┤
│ PK id           BIGINT                        │
│ FK salon_id     BIGINT  → salons.id  NN       │
│    day_of_week  INTEGER (0=Pon, 6=Ned) NN     │
│    start_time   TIME                          │
│    end_time     TIME                          │
│    is_day_off   BOOLEAN  DEFAULT false        │
│ UNIQUE (salon_id, day_of_week)                │
└───────────────────────────────────────────────┘

┌───────────────────────────┐
│       salon_photos        │
├───────────────────────────┤
│ PK id            BIGINT   │
│ FK salon_id      BIGINT   │
│    url           VARCHAR  │
│    is_primary    BOOLEAN  │
│    display_order INTEGER  │
└───────────────────────────┘
```

**Seed podaci:** 3 salona, 4 frizera, 9 usluga, 21 radnih vremena

---

## 3. BOOKING SERVICE (port 8083 | baza: `brico_bookings`)

```
┌──────────────────────────────────────────────────┐
│                   appointments                   │
├──────────────────────────────────────────────────┤
│ PK  id               BIGINT  AUTO_INCREMENT      │
│ FK* client_id        BIGINT → users.id  NN       │  *logička veza
│     client_name      VARCHAR(100) NN             │
│     client_phone     VARCHAR(20)                 │
│ FK* hairdresser_id   BIGINT → hairdressers.id NN │
│     hairdresser_name VARCHAR(100) NN             │
│ FK* salon_id         BIGINT → salons.id  NN      │
│     salon_name       VARCHAR(100) NN             │
│     salon_address    VARCHAR(200)                │
│     start_time       TIMESTAMP  NN               │
│     end_time         TIMESTAMP  NN               │
│     status           ENUM(PENDING, CONFIRMED,    │
│                      COMPLETED, CANCELLED,       │
│                      NO_SHOW)  DEFAULT PENDING   │
│     total_price      DECIMAL(10,2) NN            │
│     notes            VARCHAR(300)                │
│     created_at       TIMESTAMP                   │
└──────────────────────────────────────────────────┘
         │ 1
         │ N
         ▼
┌──────────────────────────────────────────────┐
│              appointment_items               │
├──────────────────────────────────────────────┤
│ PK  id               BIGINT                 │
│ FK  appointment_id   BIGINT → appointments  │
│ FK* service_id       BIGINT → services.id   │  *logička
│     service_name     VARCHAR(100) NN        │
│     price            DECIMAL(10,2) NN       │
│     duration_minutes INTEGER NN             │
└──────────────────────────────────────────────┘
```

> **Napomena:** `client_name`, `hairdresser_name`, `salon_name` i `service_name` su
> denormalizovani (snapshot) podaci — čuvaju stanje u trenutku rezervacije,
> neovisno od eventualnih promjena u drugim servisima.

**Seed podaci:** 5 termina (COMPLETED ×2, CONFIRMED, PENDING, CANCELLED)

---

## 4. REVIEW SERVICE (port 8084 | baza: `brico_reviews`)

```
┌─────────────────────────────────────────────────┐
│                    reviews                      │
├─────────────────────────────────────────────────┤
│ PK  id               BIGINT  AUTO_INCREMENT     │
│ FK* client_id        BIGINT → users.id  NN      │
│     client_name      VARCHAR(100) NN            │
│     client_photo     VARCHAR                    │
│ FK* salon_id         BIGINT → salons.id  NN     │
│ FK* hairdresser_id   BIGINT → hairdressers.id   │  nullable
│     hairdresser_name VARCHAR(100)               │
│ FK* appointment_id   BIGINT → appointments.id   │  nullable
│     rating           INTEGER CHECK(1..5) NN     │
│     comment          VARCHAR(500)               │
│     created_at       TIMESTAMP                  │
└─────────────────────────────────────────────────┘

┌──────────────────────────────────────────────┐
│                  favorites                   │
├──────────────────────────────────────────────┤
│ PK  id         BIGINT  AUTO_INCREMENT        │
│ FK* user_id    BIGINT → users.id  NN         │
│ FK* salon_id   BIGINT → salons.id NN         │
│     created_at TIMESTAMP                     │
│ UNIQUE (user_id, salon_id)                   │
└──────────────────────────────────────────────┘
```

**Seed podaci:** 5 recenzija, 5 omiljenih salona

---

## 5. PORTFOLIO SERVICE (port 8085 | baza: `brico_portfolio`)

```
┌──────────────────────────────────────────────────┐
│               subscription_plans                 │
├──────────────────────────────────────────────────┤
│ PK  id                    BIGINT                 │
│     plan_type             ENUM(BASIC, PRO) UNIQUE│
│     name                  VARCHAR(50) NN         │
│     description           VARCHAR(300)           │
│     price_km              DECIMAL(10,2) NN       │
│     max_hairdressers      INTEGER (null=∞)       │
│     has_advanced_analytics BOOLEAN DEFAULT false │
│     has_featured_status   BOOLEAN DEFAULT false  │
│     has_priority_support  BOOLEAN DEFAULT false  │
│     stripe_price_id       VARCHAR(60)            │
│     is_active             BOOLEAN DEFAULT true   │
│     created_at            TIMESTAMP              │
│     updated_at            TIMESTAMP              │
└──────────────────────────────────────────────────┘
         │ 1
         │ N
         ▼
┌──────────────────────────────────────────────────┐
│              salon_subscriptions                 │
├──────────────────────────────────────────────────┤
│ PK  id                    BIGINT                 │
│ FK* salon_id              BIGINT → salons.id UNIQUE│
│ FK  plan_id               BIGINT → subscription_plans│
│     status                ENUM(ACTIVE, INACTIVE, │
│                            TRIAL, CANCELLED,     │
│                            PAST_DUE)             │
│     start_date            DATE                   │
│     end_date              DATE                   │
│     stripe_subscription_id VARCHAR(60)           │
│     stripe_customer_id    VARCHAR(60)            │
│     created_at            TIMESTAMP              │
└──────────────────────────────────────────────────┘
```

**Seed podaci:** 2 plana (BASIC: 0 KM, PRO: 50 KM), 3 pretplate salona

---

## Pregled međuservisnih veza (logičke reference)

```
user-service          salon-service         booking-service
─────────────         ─────────────         ───────────────
users.id  ──────────► salons.owner_id
users.id  ──────────► hairdressers.user_id
users.id  ──────────────────────────────► appointments.client_id

salon-service         booking-service       review-service
─────────────         ───────────────       ──────────────
salons.id ──────────────────────────────► appointments.salon_id
salons.id ──────────────────────────────────────────────────► reviews.salon_id
salons.id ──────────────────────────────────────────────────► favorites.salon_id
hairdressers.id ────────────────────────► appointments.hairdresser_id
hairdressers.id ────────────────────────────────────────────► reviews.hairdresser_id
services.id ────────────────────────────► appointment_items.service_id

portfolio-service     salon-service
─────────────────     ─────────────
subscription_plans.id ──────────► salons.subscription_plan_id
```

---

## Pokretanje servisa

Svaki servis se pokreće nezavisno:

```bash
# user-service
cd user-service && mvn spring-boot:run

# salon-service
cd salon-service && mvn spring-boot:run

# booking-service
cd booking-service && mvn spring-boot:run

# review-service
cd review-service && mvn spring-boot:run

# portfolio-service
cd portfolio-service && mvn spring-boot:run
```

H2 konzola dostupna na:

- http://localhost:8081/h2-console (user-service)
- http://localhost:8082/h2-console (salon-service)
- http://localhost:8083/h2-console (booking-service)
- http://localhost:8084/h2-console (review-service)
- http://localhost:8085/h2-console (portfolio-service)

url - jdbc:h2:mem:userdb

Servis Port Baza Entiteti Seed podataka
user-service 8081 userdb User 9 korisnika (admin, vlasnici, frizeri, klijenti)
salon-service 8082 salondb Salon, Hairdresser, SalonService, WorkingHours, SalonPhoto 3 salona, 4 frizera, 9 usluga, 21 radno vrijeme
booking-service 8083 bookingdb Appointment, AppointmentItem 5 termina sa multi-servis stavkama
review-service 8084 reviewdb Review, Favorite 5 recenzija, 5 omiljenih salona
portfolio-service 8085 portfoliodb SubscriptionPlan, SalonSubscription 2 plana (BASIC/PRO), 3 pretplate
