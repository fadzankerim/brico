# Changelog

## 1.0.0 (2026-06-01)


### Features

* added a owner dashboard with its first pannels(owerview, calendar view)- next up analytics and add appointments modal ([47d1274](https://github.com/fadzankerim/brico/commit/47d127487e623da5cd646555665091861f47679f))
* **backend:** migrate H2 → PostgreSQL, improve models (NWT Zadatak 4 prep) ([11cd91f](https://github.com/fadzankerim/brico/commit/11cd91f1ef62458456e4c39fa758fbad7b234ce8))
* **backend:** REST controllers, DTOs, service layer, error handling … ([fa13623](https://github.com/fadzankerim/brico/commit/fa13623e0ee3d04b4ff807bb33fe16e103125e9d))
* **backend:** REST controllers, DTOs, service layer, error handling + tests (NWT Zadatak 3) ([3df698f](https://github.com/fadzankerim/brico/commit/3df698fab41dcbc4c05575f3734d4230d40d5096))
* dynamic owner dashboard, photo management, multi-step registration + CI release workflow ([7edd3b5](https://github.com/fadzankerim/brico/commit/7edd3b51a8723fed6659733a53b954166fabb7b2))
* initial commit ([7bb1d68](https://github.com/fadzankerim/brico/commit/7bb1d689fd23f8fb385d037b79213b3856cc482b))
* microservices backend + super admin dashboard + plan selection ([71b28d4](https://github.com/fadzankerim/brico/commit/71b28d4cd4a708d55caa36ee119fc4a06333d0ea))
* zadatak 7 ([32db3cb](https://github.com/fadzankerim/brico/commit/32db3cbef36fff10d2da2fdf4343c70c5db8156a))
* zadatak 7 ([bc9f60b](https://github.com/fadzankerim/brico/commit/bc9f60bfc3302107ee5bcfed366984b44084b301))
* zadatak4 implementiran u potpunosti i popravljene određenegreške. Modeli podešeni bolje i umjesto H2 sad je postgresql ([e99f4ea](https://github.com/fadzankerim/brico/commit/e99f4ea4cdb0fb06390898e5a1c8270a4f4052b6))


### Bug Fixes

* database seed(prvo provjeri da li ima podataka u bazi pa onda seed radi) ([21fcba6](https://github.com/fadzankerim/brico/commit/21fcba63055705ab58272cfb9eeb15e62bcbfe99))
* Dodan salonId prop — bez njega useAvailability nije mogao pozivati API, Nova sekcija Usluge salona u settings tabu — frizer može dodavati naziv, cijena, trajanje i brisati usluge ([7ad9849](https://github.com/fadzankerim/brico/commit/7ad984967c9bfad999c1c36cb76e4f9850b8e3f1))
* fixed the broken salon-service ([d5c5b8b](https://github.com/fadzankerim/brico/commit/d5c5b8bacd7a42df6f83c190f5a5a7ecb9f8f254))
* ispravka prikaza cijena i usluga u kalendar viewu ([d2913b6](https://github.com/fadzankerim/brico/commit/d2913b6ad5ae220aca449876c96e4eb8455d42e5))
* ispravka prikaza cijena i usluga u kalendar viewu ([15b8619](https://github.com/fadzankerim/brico/commit/15b861979f5808cf9a5b4c5122dad51a6bd91adf))
* Ispravljen bug u getSalonLimits(SubscriptionService): kada je pretplata CANCELLED, metoda sada vraća BASIC limite (max 3 frizera) umjesto PRO limite, dodana mogucnost otkazivanja pretplate od strane vlasnika salonadodana slozenija analitika za vlasnike salona koji posjeduju pro paket ([b9e5eb7](https://github.com/fadzankerim/brico/commit/b9e5eb77897c959757378994c2ba80c5799bcbfc))
* popravljena logika reiranja termina na strani vlasnika salona i … ([0846026](https://github.com/fadzankerim/brico/commit/084602699c0c5d0a96d26c210f777952cb5c4b29))
* popravljena logika reiranja termina na strani vlasnika salona i frizera AppointmentRequest.java — uklonjen @NotNull sa clientId. Gateway i dalje uvijek postavlja pravu vrijednost iz JWT tokena via X-User-Id header ([f26e8ff](https://github.com/fadzankerim/brico/commit/f26e8ff2a113c5cbae175d2155d1b5dde3637a75))
* spojen kompletan email servis sa svim fajlovima i riješeni konflikti ([87b9e4a](https://github.com/fadzankerim/brico/commit/87b9e4a2d55f16e5bb1a1ab0ec55fb34b148ef39))
* ukoliko nema aktivnih notifikacija(uklonjen indikator), notifikacije sada vode na instancu ispravan event ([35b4c7c](https://github.com/fadzankerim/brico/commit/35b4c7c1a5fad4570b6f281b01d5bb675f4488f0))
* ukoliko nema aktivnih notifikacija(uklonjen indikator), notifikacije sada vode na instancu ispravan event, izmjena seed podataka(5 salona (5 vlasnik account-a, svaki po dva frizera), 2 klijenta, jedan adminski) ([3616750](https://github.com/fadzankerim/brico/commit/36167505ee7eab2a2ed97cdec875afd3402f2c84))
