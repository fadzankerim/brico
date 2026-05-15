package ba.unsa.etf.nwt.salonservice;

import ba.unsa.etf.nwt.salonservice.model.*;
import ba.unsa.etf.nwt.salonservice.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalTime;

// Hairdresser ID raspored (po redoslijedu ubacivanja, 2 po salonu):
//  1 → Lejla Mehić      (Elite Cut,      userId=7)
//  2 → Tarik Bašić      (Elite Cut,      userId=8)
//  3 → Edin Kovač       (Urban Barber,   userId=9)
//  4 → Amina Zukić      (Urban Barber,   userId=10)
//  5 → Nina Softić      (Glam Studio,    userId=11)
//  6 → Sara Begić       (Glam Studio,    userId=12)
//  7 → Haris Muratović  (Barber Kingdom, userId=13)
//  8 → Denis Avdić      (Barber Kingdom, userId=14)
//  9 → Jovana Nikolić   (Style Lab,      userId=15)
// 10 → Aleksandar Perić (Style Lab,      userId=16)
//
// Service ID raspored (po redoslijedu ubacivanja):
// Elite Cut:      1=Šišanje, 2=Pranje+Fen, 3=Balayage, 4=Keratin, 5=Brijanje
// Urban Barber:   6=Muško šišanje, 7=Brijanje, 8=Fade, 9=Dječije
// Glam Studio:   10=Bojenje, 11=Pramenovi, 12=Šišanje+fen, 13=Braz.keratin
// Barber Kingdom:14=Klasično, 15=Moderna, 16=Brijanje+maska, 17=Komplet
// Style Lab:     18=Kreativno bojenje, 19=Šišanje, 20=Kompleksna boja, 21=Fen styling

@Component
@RequiredArgsConstructor
@Slf4j
public class DataLoader implements CommandLineRunner {

    private final SalonRepository        salonRepository;
    private final HairdresserRepository  hairdresserRepository;
    private final SalonServiceRepository serviceRepository;
    private final WorkingHoursRepository workingHoursRepository;

    @Override
    public void run(String... args) {
        if (salonRepository.count() > 0) return;
        log.info("=== Seed: salon-service ===");

        // ── 1. Elite Cut ─────────────────────────────────────────────────
        Salon eliteCut = createSalon("Elite Cut", "elite-cut",
                "Moderan frizerski salon u centru Sarajeva — specijalnost balayage i keratin tretmani",
                "Sarajevo", "Titova 1", "+38761100011", 2L, true, "4200111000001");

        addHairdresser(eliteCut, 7L,  "Lejla Mehić",  "Specijalnost: balayage, keratin i žensko šišanje",  5.0, 4);
        addHairdresser(eliteCut, 8L,  "Tarik Bašić",  "Muško šišanje, brijanje i oblikovanje brade",        4.5, 2);
        addService(eliteCut, "Šišanje",          15, 30);
        addService(eliteCut, "Pranje + Fen",     10, 20);
        addService(eliteCut, "Balayage",         80, 120);
        addService(eliteCut, "Keratin tretman", 120, 150);
        addService(eliteCut, "Brijanje britvom", 18, 30);
        addWorkingHours(eliteCut);

        // ── 2. Urban Barber ───────────────────────────────────────────────
        Salon urbanBarber = createSalon("Urban Barber", "urban-barber",
                "Premium barbershop iskustvo u Mostaru — fade, skin fade i klasično brijanje",
                "Mostar", "Bulevar 12", "+38762200012", 3L, true, "4200222000002");

        addHairdresser(urbanBarber, 9L,  "Edin Kovač",   "Specijalnost: fade i skin fade šišanje",   4.0, 2);
        addHairdresser(urbanBarber, 10L, "Amina Zukić",  "Klasično brijanje i muška njega kose",      5.0, 1);
        addService(urbanBarber, "Muško šišanje",    12, 30);
        addService(urbanBarber, "Brijanje britvom", 15, 30);
        addService(urbanBarber, "Fade šišanje",     18, 45);
        addService(urbanBarber, "Dječije šišanje",  10, 20);
        addWorkingHours(urbanBarber);

        // ── 3. Glam Studio ────────────────────────────────────────────────
        Salon glamStudio = createSalon("Glam Studio", "glam-studio",
                "Ženski frizerski salon specijalizovan za boju, pramenove i stilizaciju",
                "Sarajevo", "Ferhadija 22", "+38761300013", 4L, true, "4200333000003");

        addHairdresser(glamStudio, 11L, "Nina Softić",  "Specijalnost: bojenje, pramenovi i balayage",   4.0, 2);
        addHairdresser(glamStudio, 12L, "Sara Begić",   "Fen styling, nadogradnja i posebne frizure",    5.0, 1);
        addService(glamStudio, "Bojenje",           50,  90);
        addService(glamStudio, "Pramenovi",         70, 120);
        addService(glamStudio, "Šišanje + fen",     20,  45);
        addService(glamStudio, "Brazilski keratin", 100, 180);
        addWorkingHours(glamStudio);

        // ── 4. Barber Kingdom ─────────────────────────────────────────────
        Salon barberKingdom = createSalon("Barber Kingdom", "barber-kingdom",
                "Tradicionalni barbershop s modernim pristupom — brijanje, fade i oblikovanje brade",
                "Tuzla", "Armijska bb", "+38765400014", 5L, true, "4200444000004");

        addHairdresser(barberKingdom, 13L, "Haris Muratović", "Klasično i moderno šišanje, komplet usluge",   4.3, 3);
        addHairdresser(barberKingdom, 14L, "Denis Avdić",     "Brijanje britvom i hot-towel tretman",         4.0, 2);
        addService(barberKingdom, "Klasično šišanje",   14, 30);
        addService(barberKingdom, "Moderna frizura",    16, 35);
        addService(barberKingdom, "Brijanje + maska",   22, 45);
        addService(barberKingdom, "Komplet usluga",     35, 75);
        addWorkingHours(barberKingdom);

        // ── 5. Style Lab ──────────────────────────────────────────────────
        Salon styleLab = createSalon("Style Lab", "style-lab",
                "Kreativni salon za izražavanje individualnosti — nestandardne boje i avangardni stilovi",
                "Banja Luka", "Krajiška 5", "+38765500015", 6L, true, "4200555000005");

        addHairdresser(styleLab, 15L, "Jovana Nikolić",    "Kreativne boje, fantasy i pastele",              4.0, 1);
        addHairdresser(styleLab, 16L, "Aleksandar Perić",  "Muške frizure, undercut i kreativni stilovi",    3.0, 1);
        addService(styleLab, "Kreativno bojenje", 60, 120);
        addService(styleLab, "Šišanje",           15,  30);
        addService(styleLab, "Kompleksna boja",   90, 150);
        addService(styleLab, "Fen styling",       25,  30);
        addWorkingHours(styleLab);

        log.info("=== Seed završen: {} salona, {} frizera ===",
                salonRepository.count(), hairdresserRepository.count());
    }

    private Salon createSalon(String name, String slug, String desc,
                               String city, String address, String phone,
                               Long ownerId, boolean verified, String idBroj) {
        return salonRepository.save(Salon.builder()
                .name(name).slug(slug).description(desc)
                .city(city).address(address).phone(phone)
                .ownerId(ownerId).verified(verified).isActive(true)
                .idBroj(idBroj)
                .build());
    }

    private void addHairdresser(Salon salon, Long userId, String name, String bio,
                                 double avgRating, int totalAppointments) {
        hairdresserRepository.save(Hairdresser.builder()
                .salon(salon)
                .userId(userId)
                .fullName(name)
                .bio(bio)
                .isActive(true)
                .avgRating(avgRating)
                .totalAppointments(totalAppointments)
                .build());
    }

    private void addService(Salon salon, String name, double price, int durationMin) {
        serviceRepository.save(SalonService.builder()
                .salon(salon)
                .name(name)
                .price(BigDecimal.valueOf(price))
                .durationMinutes(durationMin)
                .isActive(true)
                .build());
    }

    private void addWorkingHours(Salon salon) {
        for (DayOfWeek day : DayOfWeek.values()) {
            boolean isDayOff = (day == DayOfWeek.SUNDAY);
            int dayIndex = day.getValue() % 7;
            workingHoursRepository.save(WorkingHours.builder()
                    .salon(salon).dayOfWeek(dayIndex)
                    .startTime(isDayOff ? null : LocalTime.of(8, 0))
                    .endTime(isDayOff ? null : LocalTime.of(20, 0))
                    .isDayOff(isDayOff).build());
        }
    }
}
