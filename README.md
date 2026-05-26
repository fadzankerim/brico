<div align="center">

# ✂️ Brico

**Platforma za online rezervaciju termina u frizerskim salonima**

![React](https://img.shields.io/badge/React_19-20232A?style=flat&logo=react&logoColor=61DAFB)
![TypeScript](https://img.shields.io/badge/TypeScript-3178C6?style=flat&logo=typescript&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot_3-6DB33F?style=flat&logo=spring-boot&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL_16-4169E1?style=flat&logo=postgresql&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED?style=flat&logo=docker&logoColor=white)
![RabbitMQ](https://img.shields.io/badge/RabbitMQ-FF6600?style=flat&logo=rabbitmq&logoColor=white)

</div>

---

##  O projektu

**Brico** je web aplikacija za upravljanje frizerskim salonima i rezervaciju termina. Sistem povezuje vlasnike salona, frizere i klijente na jednoj platformi.

**Ključne funkcionalnosti:**

- 🔐 **Autentifikacija** — registracija i prijava s ulogama (Admin / Vlasnik salona / Frizer / Klijent)
- 📅 **Rezervacije** — klijenti biraju salon, frizera, usluge i termin; frizeri upravljaju kalendarom
- 💼 **Upravljanje salonom** — vlasnici dodaju frizere, usluge i prate statistiku prihoda
- ⭐ **Recenzije** — klijenti ocjenjuju salone nakon završenog termina
- 📸 **Portfolio** — frizeri prikazuju svoje radove
- 📧 **Email notifikacije** — potvrda, podsjetnik (24h / 1h prije) i otkazivanje termina
- 📊 **Analitika** — statistika po salonu, prihodi, popunjenost termina
- 👑 **Pretplate** — BASIC (besplatno) i PRO (50 KM/mj.) plan

---

## 🏗️ Arhitektura

Aplikacija je izgrađena kao **mikroservisna arhitektura** s React frontendom.

```
┌─────────────────────────────────────────────┐
│               React Frontend                │  :3000
└──────────────────────┬──────────────────────┘
                       │
┌──────────────────────▼──────────────────────┐
│              API Gateway (JWT)              │  :8080
└──────┬───────┬───────┬───────┬──────┬───────┘
       │       │       │       │      │
   ┌───▼──┐ ┌──▼──┐ ┌──▼──┐ ┌─▼──┐ ┌─▼────┐
   │Users │ │Salon│ │Book │ │Rev.│ │Port. │
   │:8081 │ │:8082│ │:8083│ │:8084│ │:8085 │
   └──────┘ └─────┘ └─────┘ └────┘ └──────┘

   ┌──────────────────┐   ┌──────────────────┐
   │  system-events   │   │  notification    │
   │  :8086 / gRPC    │   │  :8087 / AMQP    │
   └──────────────────┘   └──────────────────┘

   ┌────────────┐   ┌──────────┐   ┌────────────┐
   │  Eureka    │   │  Config  │   │  RabbitMQ  │
   │  :8761     │   │  :8888   │   │  :5672     │
   └────────────┘   └──────────┘   └────────────┘
```

| Servis | Port | Opis |
|--------|------|------|
| `frontend` | 3000 | React + Vite SPA |
| `api-gateway` | 8080 | Spring Cloud Gateway + JWT filter |
| `user-service` | 8081 | Autentifikacija, korisnici, uloge |
| `salon-service` | 8082 | Saloni, frizeri, usluge, rasporedi |
| `booking-service` | 8083 | Termini, rezervacije, statistika |
| `review-service` | 8084 | Recenzije i ocjene |
| `portfolio-service` | 8085 | Slike i radovi frizera |
| `system-events-service` | 8086 / 9090 | Audit log putem gRPC |
| `notification-service` | 8087 | Email notifikacije putem RabbitMQ |
| `eureka-server` | 8761 | Service discovery |
| `config-server` | 8888 | Centralizovana konfiguracija |
| `postgres` | 5432 | PostgreSQL 16 (9 baza) |
| `rabbitmq` | 5672 / 15672 | Message broker + management UI |

---

## 🛠️ Tech Stack

**Frontend**
- React 19 · TypeScript · Vite
- TailwindCSS 4 · shadcn/ui · Motion (Framer)
- TanStack Query · React Hook Form + Zod · Zustand
- Recharts · FullCalendar · Lucide Icons

**Backend**
- Java 21 · Spring Boot 3 · Spring Cloud (Gateway, Eureka, Config)
- Spring Security + JWT · Spring Data JPA · Hibernate
- gRPC (system-events) · RabbitMQ (async notifikacije) · Feign Client
- PostgreSQL 16 · Lombok · MapStruct

---

## 👥 Tim

| GitHub | Ime |
|--------|-----|
| [@fadzankerim](https://github.com/fadzankerim) | Kerim Fadzan |
| [@sveti-berin](https://github.com/sveti-berin) | Berin Mujkić |
| [@ALejla1](https://github.com/ALejla1) | Lejla Ahmethodzic |
| [@nermindju](https://github.com/nermindju) | Nermin Dulepa |

---

## 🐳 Pokretanje putem Dockera

### Preduvjeti

- [Docker](https://docs.docker.com/get-docker/) ≥ 24
- [Docker Compose](https://docs.docker.com/compose/install/) ≥ 2.20
- Minimalno **6 GB slobodne RAM memorije**

### 1. Kloniraj repozitorij

```bash
git clone https://github.com/fadzankerim/brico.git
cd brico/brico-frontend
```

### 2. Pokreni cijeli sistem

```bash
cd brico-backend
docker compose up --build -d
```

> ⏱️ **Prvo pokretanje traje 10–15 minuta** jer se builda svih 10 Spring Boot servisa.
> Svaki servis čeka da mu zavisnosti budu zdrave (healthcheck).

### 3. Provjeri status kontejnera

```bash
docker compose ps
```

Svi servisi trebaju biti u statusu `running (healthy)` ili `running`.

### 4. Otvori aplikaciju

| URL | Opis |
|-----|------|
| [http://localhost:3000](http://localhost:3000) | 🌐 Frontend aplikacija |
| [http://localhost:8761](http://localhost:8761) | 🔍 Eureka — Service registry |
| [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html) | 📄 API dokumentacija |
| [http://localhost:15672](http://localhost:15672) | 🐰 RabbitMQ Management UI |

> **RabbitMQ login:** `brico` / `brico123`

---

### Korisne Docker naredbe

```bash
# Zaustavi sve servise (čuva podatke)
docker compose down

# Zaustavi i obriši sve volumene (reset baze)
docker compose down -v

# Pokreni samo jedan servis
docker compose up booking-service -d

# Prati logove servisa u realnom vremenu
docker compose logs -f booking-service

# Rebuild jednog servisa bez rušenja ostalih
docker compose up --build --no-deps salon-service -d
```

---

## 🔑 Test nalozi

Svi nalozi koriste lozinku: **`password123`**

| Uloga | Email |
|-------|-------|
| Admin | `admin@brico.ba` |
| Vlasnik salona | `vlasnik1@brico.ba` |
| Frizer | `frizer1@brico.ba` |
| Klijent | `klijent1@brico.ba` |

> Kompletna lista naloga, salona i usluga dostupna je u [`brico-backend/ACCOUNTS.md`](brico-backend/ACCOUNTS.md).

---

## 📁 Struktura projekta

```
brico-frontend/
├── src/
│   ├── components/      # UI komponente
│   ├── pages/           # Stranice po ulozi (auth, client, owner, hairdresser, admin)
│   ├── hooks/           # React Query hooks
│   ├── services/        # Axios API pozivi
│   ├── store/           # Zustand globalni state
│   ├── types/           # TypeScript tipovi
│   └── utils/           # Validatori (Zod), helpers
├── brico-backend/
│   ├── api-gateway/
│   ├── user-service/
│   ├── salon-service/
│   ├── booking-service/
│   ├── review-service/
│   ├── portfolio-service/
│   ├── system-events-service/
│   ├── notification-service/
│   ├── eureka-server/
│   ├── config-server/
│   └── docker-compose.yml
└── README.md
```

---

<div align="center">


</div>
