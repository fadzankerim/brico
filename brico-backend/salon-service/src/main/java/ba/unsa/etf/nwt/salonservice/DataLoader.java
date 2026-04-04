package ba.unsa.etf.nwt.salonservice;

import ba.unsa.etf.nwt.salonservice.model.*;
import ba.unsa.etf.nwt.salonservice.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataLoader implements CommandLineRunner {

    private final SalonRepository salonRepository;
    private final HairdresserRepository hairdresserRepository;
    private final SalonServiceRepository salonServiceRepository;
    private final WorkingHoursRepository workingHoursRepository;

    @Override
    public void run(String... args) {
        log.info("=== Učitavanje početnih podataka za Salon Service ===");

        // ── Salon 1 ──────────────────────────────────────────────
        Salon salon1 = salonRepository.save(
                Salon.builder()
                        .name("Frizerski Studio Lejla")
                        .slug("frizerski-studio-lejla")
                        .description("Moderan frizerski salon u srcu Sarajeva. Specijalizovani za bojenje i stilizaciju.")
                        .city("Sarajevo")
                        .address("Ferhadija 12, Sarajevo")
                        .latitude(43.8563)
                        .longitude(18.4131)
                        .phone("+38733111001")
                        .verified(true)
                        .isActive(true)
                        .ownerId(2L) // Amir Hodžić iz user-service
                        .subscriptionPlanId(2L) // PRO plan
                        .build()
        );

        // ── Salon 2 ──────────────────────────────────────────────
        Salon salon2 = salonRepository.save(
                Salon.builder()
                        .name("Barbershop King")
                        .slug("barbershop-king")
                        .description("Muški barbershop sa tradicionalnim tehnikama i modernim stilovima.")
                        .city("Mostar")
                        .address("Bulevar 33, Mostar")
                        .latitude(43.3439)
                        .longitude(17.8078)
                        .phone("+38736222002")
                        .verified(true)
                        .isActive(true)
                        .ownerId(3L) // Selma Kovačević iz user-service
                        .subscriptionPlanId(1L) // BASIC plan
                        .build()
        );

        // ── Salon 3 ──────────────────────────────────────────────
        Salon salon3 = salonRepository.save(
                Salon.builder()
                        .name("Beauty Zone")
                        .slug("beauty-zone-tuzla")
                        .description("Kompletan beauty tretman - kosa, nokti, koža.")
                        .city("Tuzla")
                        .address("Turalibegova 5, Tuzla")
                        .latitude(44.5382)
                        .longitude(18.6734)
                        .phone("+38735333003")
                        .verified(false)
                        .isActive(true)
                        .ownerId(2L)
                        .subscriptionPlanId(1L) // BASIC plan
                        .build()
        );

        log.info("Kreirano {} salona", salonRepository.count());

        // ── Frizeri za Salon 1 ───────────────────────────────────
        Hairdresser h1 = hairdresserRepository.save(
                Hairdresser.builder()
                        .userId(4L) // Lejla Mehić iz user-service
                        .fullName("Lejla Mehić")
                        .bio("10 godina iskustva u bojenju i stilizaciji kose.")
                        .specialties("Bojenje,Pramenovi,Stilizacija")
                        .isActive(true)
                        .salon(salon1)
                        .build()
        );

        Hairdresser h2 = hairdresserRepository.save(
                Hairdresser.builder()
                        .userId(5L) // Tarik Bašić
                        .fullName("Tarik Bašić")
                        .bio("Specijalist za muške frizure i bradu.")
                        .specialties("Šišanje,Brada,Fade")
                        .isActive(true)
                        .salon(salon1)
                        .build()
        );

        // ── Frizeri za Salon 2 ───────────────────────────────────
        Hairdresser h3 = hairdresserRepository.save(
                Hairdresser.builder()
                        .userId(6L) // Nina Softić
                        .fullName("Nina Softić")
                        .bio("Certificirani barber sa međunarodnim iskustvom.")
                        .specialties("Šišanje,Brada,Klasični rezovi")
                        .isActive(true)
                        .salon(salon2)
                        .build()
        );

        // ── Frizeri za Salon 3 ───────────────────────────────────
        Hairdresser h4 = hairdresserRepository.save(
                Hairdresser.builder()
                        .fullName("Amra Husić")
                        .bio("Specijalist za tretmane kose i keratinski tretman.")
                        .specialties("Keratin,Tretmani,Bojenje")
                        .isActive(true)
                        .salon(salon3)
                        .build()
        );

        log.info("Kreirano {} frizera", hairdresserRepository.count());

        // ── Usluge za Salon 1 ────────────────────────────────────
        salonServiceRepository.save(SalonService.builder()
                .name("Šišanje i stilizacija")
                .description("Profesionalno šišanje sa pranjem i stilizacijom")
                .price(new BigDecimal("35.00"))
                .durationMinutes(60)
                .salon(salon1).build());

        salonServiceRepository.save(SalonService.builder()
                .name("Bojenje kose (puna boja)")
                .description("Profesionalno bojenje sa premium bojama")
                .price(new BigDecimal("80.00"))
                .durationMinutes(120)
                .salon(salon1).build());

        salonServiceRepository.save(SalonService.builder()
                .name("Pramenovi")
                .description("Klasični ili balayage pramenovi")
                .price(new BigDecimal("100.00"))
                .durationMinutes(150)
                .salon(salon1).build());

        salonServiceRepository.save(SalonService.builder()
                .name("Tretman keratinom")
                .description("Brazilski keratin tretman za ravnu i sjajnu kosu")
                .price(new BigDecimal("120.00"))
                .durationMinutes(180)
                .salon(salon1).build());

        // ── Usluge za Salon 2 ────────────────────────────────────
        salonServiceRepository.save(SalonService.builder()
                .name("Muško šišanje")
                .description("Klasično ili moderno muško šišanje")
                .price(new BigDecimal("20.00"))
                .durationMinutes(30)
                .salon(salon2).build());

        salonServiceRepository.save(SalonService.builder()
                .name("Šišanje + brada")
                .description("Komplet usluga šišanja i oblikovanja brade")
                .price(new BigDecimal("30.00"))
                .durationMinutes(45)
                .salon(salon2).build());

        salonServiceRepository.save(SalonService.builder()
                .name("Fade šišanje")
                .description("Moderni fade stil - visoki, srednji ili niski fade")
                .price(new BigDecimal("25.00"))
                .durationMinutes(40)
                .salon(salon2).build());

        // ── Usluge za Salon 3 ────────────────────────────────────
        salonServiceRepository.save(SalonService.builder()
                .name("Pranje i feniranje")
                .description("Dubinsko pranje sa premium šamponimaima i feniranje")
                .price(new BigDecimal("25.00"))
                .durationMinutes(45)
                .salon(salon3).build());

        salonServiceRepository.save(SalonService.builder()
                .name("Keratin tretman")
                .description("Profesionalni keratin za glatku i sjajnu kosu")
                .price(new BigDecimal("110.00"))
                .durationMinutes(180)
                .salon(salon3).build());

        log.info("Kreirano {} usluga", salonServiceRepository.count());

        // ── Radno vrijeme za Salon 1 ─────────────────────────────
        // Pon - Pet: 09:00 - 20:00, Sub: 09:00 - 17:00, Ned: slobodan
        for (int day = 0; day <= 4; day++) {
            workingHoursRepository.save(WorkingHours.builder()
                    .dayOfWeek(day)
                    .startTime(LocalTime.of(9, 0))
                    .endTime(LocalTime.of(20, 0))
                    .isDayOff(false)
                    .salon(salon1).build());
        }
        workingHoursRepository.save(WorkingHours.builder()
                .dayOfWeek(5)
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(17, 0))
                .isDayOff(false)
                .salon(salon1).build());
        workingHoursRepository.save(WorkingHours.builder()
                .dayOfWeek(6)
                .isDayOff(true)
                .salon(salon1).build());

        // ── Radno vrijeme za Salon 2 ─────────────────────────────
        // Pon - Sub: 08:00 - 18:00, Ned: slobodan
        for (int day = 0; day <= 5; day++) {
            workingHoursRepository.save(WorkingHours.builder()
                    .dayOfWeek(day)
                    .startTime(LocalTime.of(8, 0))
                    .endTime(LocalTime.of(18, 0))
                    .isDayOff(false)
                    .salon(salon2).build());
        }
        workingHoursRepository.save(WorkingHours.builder()
                .dayOfWeek(6)
                .isDayOff(true)
                .salon(salon2).build());

        log.info("Kreirano {} radnih vremena", workingHoursRepository.count());
        log.info("=== Salon Service inicijalizacija završena ===");
    }
}
