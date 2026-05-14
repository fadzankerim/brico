# Brico — Test Nalozi

Svi nalozi koriste lozinku: **`password123`**

---

## Admin

| Email | Lozinka | Ime | Uloga |
|-------|---------|-----|-------|
| admin@brico.ba | password123 | Super Admin | ADMIN |

---

## Vlasnici salona

| Email | Lozinka | Ime | Salon | Grad | Pretplata |
|-------|---------|-----|-------|------|-----------|
| vlasnik1@brico.ba | password123 | Amir Hodžić | Elite Cut | Sarajevo | PRO |
| vlasnik2@brico.ba | password123 | Selma Kovačević | Urban Barber | Mostar | PRO |
| vlasnik3@brico.ba | password123 | Kenan Avdić | Glam Studio | Sarajevo | BASIC |
| vlasnik4@brico.ba | password123 | Mirza Hadžimuratović | Barber Kingdom | Tuzla | PRO |
| vlasnik5@brico.ba | password123 | Fatima Bašić | Style Lab | Banja Luka | BASIC (trial) |

---

## Frizeri

| Email | Lozinka | Ime | Salon | userId | Hairdresser ID |
|-------|---------|-----|-------|--------|----------------|
| frizer1@brico.ba | password123 | Lejla Mehić | Elite Cut | 7 | 1 |
| frizer2@brico.ba | password123 | Tarik Bašić | Elite Cut | 8 | 2 |
| frizer3@brico.ba | password123 | Edin Kovač | Urban Barber | 9 | 3 |
| frizer4@brico.ba | password123 | Amina Zukić | Urban Barber | 10 | 4 |
| frizer5@brico.ba | password123 | Nina Softić | Glam Studio | 11 | 5 |
| frizer6@brico.ba | password123 | Sara Begić | Glam Studio | 12 | 6 |
| frizer7@brico.ba | password123 | Haris Muratović | Barber Kingdom | 13 | 7 |
| frizer8@brico.ba | password123 | Denis Avdić | Barber Kingdom | 14 | 8 |
| frizer9@brico.ba | password123 | Jovana Nikolić | Style Lab | 15 | 9 |
| frizer10@brico.ba | password123 | Aleksandar Perić | Style Lab | 16 | 10 |

> **userId** = ID u `brico_users` bazi (user-service)  
> **Hairdresser ID** = ID u `brico_salons` bazi (salon-service, tabela `hairdressers`)

---

## Klijenti

| Email | Lozinka | Ime | userId |
|-------|---------|-----|--------|
| klijent1@brico.ba | password123 | Amina Begić | 17 |
| klijent2@brico.ba | password123 | Emir Zukić | 18 |

---

## Saloni i usluge

### 1. Elite Cut — Sarajevo, Titova 1
**Vlasnik:** vlasnik1@brico.ba | **Pretplata:** PRO  
**Frizeri:** Lejla Mehić (frizer1), Tarik Bašić (frizer2)

| Usluga | Cijena | Trajanje |
|--------|--------|---------|
| Šišanje | 15 KM | 30 min |
| Pranje + Fen | 10 KM | 20 min |
| Balayage | 80 KM | 120 min |
| Keratin tretman | 120 KM | 150 min |
| Brijanje britvom | 18 KM | 30 min |

---

### 2. Urban Barber — Mostar, Bulevar 12
**Vlasnik:** vlasnik2@brico.ba | **Pretplata:** PRO  
**Frizeri:** Edin Kovač (frizer3), Amina Zukić (frizer4)

| Usluga | Cijena | Trajanje |
|--------|--------|---------|
| Muško šišanje | 12 KM | 30 min |
| Brijanje britvom | 15 KM | 30 min |
| Fade šišanje | 18 KM | 45 min |
| Dječije šišanje | 10 KM | 20 min |

---

### 3. Glam Studio — Sarajevo, Ferhadija 22
**Vlasnik:** vlasnik3@brico.ba | **Pretplata:** BASIC  
**Frizeri:** Nina Softić (frizer5), Sara Begić (frizer6)

| Usluga | Cijena | Trajanje |
|--------|--------|---------|
| Bojenje | 50 KM | 90 min |
| Pramenovi | 70 KM | 120 min |
| Šišanje + fen | 20 KM | 45 min |
| Brazilski keratin | 100 KM | 180 min |

---

### 4. Barber Kingdom — Tuzla, Armijska bb
**Vlasnik:** vlasnik4@brico.ba | **Pretplata:** PRO  
**Frizeri:** Haris Muratović (frizer7), Denis Avdić (frizer8)

| Usluga | Cijena | Trajanje |
|--------|--------|---------|
| Klasično šišanje | 14 KM | 30 min |
| Moderna frizura | 16 KM | 35 min |
| Brijanje + maska | 22 KM | 45 min |
| Komplet usluga | 35 KM | 75 min |

---

### 5. Style Lab — Banja Luka, Krajiška 5
**Vlasnik:** vlasnik5@brico.ba | **Pretplata:** BASIC (trial)  
**Frizeri:** Jovana Nikolić (frizer9), Aleksandar Perić (frizer10)

| Usluga | Cijena | Trajanje |
|--------|--------|---------|
| Kreativno bojenje | 60 KM | 120 min |
| Šišanje | 15 KM | 30 min |
| Kompleksna boja | 90 KM | 150 min |
| Fen styling | 25 KM | 30 min |

---

## Mock podaci — statistika

### Termini (booking-service) — ukupno 25

| Status | Broj |
|--------|------|
| COMPLETED | 19 |
| CANCELLED | 2 |
| CONFIRMED | 2 |
| PENDING | 2 |

### Zarada po salonu (iz COMPLETED termina)

| Salon | Zarada | Br. termina |
|-------|--------|-------------|
| Elite Cut | 371 KM | 6 |
| Glam Studio | 190 KM | 3 |
| Style Lab | 150 KM | 2 |
| Barber Kingdom | 143 KM | 5 |
| Urban Barber | 69 KM | 3 |

### Recenzije (review-service) — ukupno 19

| Salon | Prosj. ocjena | Br. recenzija |
|-------|---------------|---------------|
| Elite Cut | ⭐ 4.83 | 6 |
| Urban Barber | ⭐ 4.33 | 3 |
| Glam Studio | ⭐ 4.33 | 3 |
| Barber Kingdom | ⭐ 4.20 | 5 |
| Style Lab | ⭐ 3.50 | 2 |

### Omiljeni saloni

| Klijent | Omiljeni saloni |
|---------|----------------|
| Amina Begić | Elite Cut, Glam Studio, Style Lab |
| Emir Zukić | Elite Cut, Urban Barber, Barber Kingdom |

---

## ID referenca (cross-service)

```
user-service (brico_users):
  ID 1  → admin@brico.ba
  ID 2  → vlasnik1@brico.ba  (Elite Cut)
  ID 3  → vlasnik2@brico.ba  (Urban Barber)
  ID 4  → vlasnik3@brico.ba  (Glam Studio)
  ID 5  → vlasnik4@brico.ba  (Barber Kingdom)
  ID 6  → vlasnik5@brico.ba  (Style Lab)
  ID 7  → frizer1@brico.ba   (Lejla Mehić)
  ID 8  → frizer2@brico.ba   (Tarik Bašić)
  ID 9  → frizer3@brico.ba   (Edin Kovač)
  ID 10 → frizer4@brico.ba   (Amina Zukić)
  ID 11 → frizer5@brico.ba   (Nina Softić)
  ID 12 → frizer6@brico.ba   (Sara Begić)
  ID 13 → frizer7@brico.ba   (Haris Muratović)
  ID 14 → frizer8@brico.ba   (Denis Avdić)
  ID 15 → frizer9@brico.ba   (Jovana Nikolić)
  ID 16 → frizer10@brico.ba  (Aleksandar Perić)
  ID 17 → klijent1@brico.ba  (Amina Begić)
  ID 18 → klijent2@brico.ba  (Emir Zukić)

salon-service (brico_salons) — saloni:
  ID 1 → Elite Cut      (ownerId=2)
  ID 2 → Urban Barber   (ownerId=3)
  ID 3 → Glam Studio    (ownerId=4)
  ID 4 → Barber Kingdom (ownerId=5)
  ID 5 → Style Lab      (ownerId=6)

salon-service (brico_salons) — frizeri:
  ID 1  → Lejla Mehić      (userId=7,  Elite Cut)
  ID 2  → Tarik Bašić      (userId=8,  Elite Cut)
  ID 3  → Edin Kovač       (userId=9,  Urban Barber)
  ID 4  → Amina Zukić      (userId=10, Urban Barber)
  ID 5  → Nina Softić      (userId=11, Glam Studio)
  ID 6  → Sara Begić       (userId=12, Glam Studio)
  ID 7  → Haris Muratović  (userId=13, Barber Kingdom)
  ID 8  → Denis Avdić      (userId=14, Barber Kingdom)
  ID 9  → Jovana Nikolić   (userId=15, Style Lab)
  ID 10 → Aleksandar Perić (userId=16, Style Lab)
```
