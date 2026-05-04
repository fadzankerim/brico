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

        createSalon("Elite Cut", "elite-cut", "Moderan frizerski salon u centru Sarajeva",
            "Sarajevo", "Titova 1", "+38761100001", 2L, true,
            new String[]{"Lejla Mehić", "Tarik Bašić", "Amina Hodžić"},
            new String[]{"Specijalnost: balayage i keratin", "Muško šišanje i brijanje", "Bojanje i pramenovi"},
            new String[]{"Šišanje", "Pranje + Fen", "Balayage", "Keratin tretman", "Brijanje britvom"},
            new double[]{15, 10, 80, 120, 18},
            new int[]{30, 20, 120, 150, 30});

        createSalon("Urban Barber", "urban-barber", "Premium barbershop iskustvo u Mostaru",
            "Mostar", "Bulevar 12", "+38762200002", 3L, true,
            new String[]{"Edin Kovač", "Mirza Hadžić"},
            new String[]{"Specijalnost: fade i skin fade", "Klasično brijanje"},
            new String[]{"Muško šišanje", "Brijanje britvom", "Fade šišanje", "Dječije šišanje"},
            new double[]{12, 15, 18, 10},
            new int[]{30, 30, 45, 20});

        createSalon("Glam Studio", "glam-studio", "Ženski frizerski salon — boja i stilizacija",
            "Sarajevo", "Ferhadija 22", "+38761300003", 2L, false,
            new String[]{"Nina Softić", "Sara Begić"},
            new String[]{"Specijalnost: boja i pramenovi", "Fen i stilizacija"},
            new String[]{"Bojenje", "Pramenovi", "Šišanje + fen", "Brazilski keratin"},
            new double[]{50, 70, 20, 100},
            new int[]{90, 120, 45, 180});

        createSalon("Barber Kingdom", "barber-kingdom", "Tradicionalni barbershop s modernim pristupom",
            "Tuzla", "Armijska bb", "+38765400004", 3L, true,
            new String[]{"Haris Muratović", "Denis Avdić"},
            new String[]{"Klasično i moderno šišanje", "Brijanje i oblikovanje"},
            new String[]{"Klasično šišanje", "Moderna frizura", "Brijanje + maska", "Komplet usluga"},
            new double[]{14, 16, 22, 35},
            new int[]{30, 35, 45, 75});

        createSalon("Style Lab", "style-lab", "Kreativni salon za izražavanje individualnosti",
            "Banja Luka", "Krajiška 5", "+38765500005", 2L, true,
            new String[]{"Jovana Nikolić", "Aleksandar Perić"},
            new String[]{"Kreativne boje i stilovi", "Muške frizure"},
            new String[]{"Kreativno bojenje", "Šišanje", "Kompleksna boja", "Fen styling"},
            new double[]{60, 15, 90, 25},
            new int[]{120, 30, 150, 30});

        createSalon("Lux Hair", "lux-hair", "Luksuzni salon za najzahtjevnije klijente",
            "Sarajevo", "Skenderija 8", "+38761600006", 3L, true,
            new String[]{"Valentina Čović", "Marko Ilić"},
            new String[]{"VIP tretmani i ekskluzivne usluge", "Muški grooming"},
            new String[]{"VIP paket", "Balayage premium", "Šišanje + styling", "Tretman njege"},
            new double[]{150, 120, 35, 60},
            new int[]{180, 150, 60, 90});

        createSalon("Fresh Cut", "fresh-cut", "Brzo i kvalitetno šišanje po pristupačnoj cijeni",
            "Zenica", "Zmaja od Bosne 3", "+38762700007", 2L, false,
            new String[]{"Adnan Mehić"},
            new String[]{"Brzo i precizno šišanje"},
            new String[]{"Šišanje", "Šišanje + brijanje", "Dječije šišanje"},
            new double[]{10, 18, 8},
            new int[]{20, 35, 15});

        createSalon("Color Me Beautiful", "color-me-beautiful", "Salon specijalizovan za bojanje kose",
            "Sarajevo", "Grbavica 14", "+38761800008", 3L, true,
            new String[]{"Emina Hadžimuratović", "Lejla Begović"},
            new String[]{"Specijalista za nijanse i tonove", "Balayage ekspert"},
            new String[]{"Puno bojenje", "Highlights", "Balayage", "Toning", "Farbanje obrva"},
            new double[]{55, 65, 85, 35, 15},
            new int[]{90, 100, 120, 45, 20});

        createSalon("The Gentleman", "the-gentleman", "Ekskluzivni muški grooming salon",
            "Mostar", "Rondo 7", "+38763900009", 2L, true,
            new String[]{"Stefan Marković", "Ivan Pavlović"},
            new String[]{"Muški grooming specijalista", "Brade i brkovi ekspert"},
            new String[]{"Šišanje", "Brijanje britvom", "Oblikovanje brade", "Paket premium"},
            new double[]{18, 22, 15, 50},
            new int[]{30, 40, 25, 80});

        createSalon("Scissor Sisters", "scissor-sisters", "Ženski salon s opuštenom atmosferom",
            "Sarajevo", "Ilidža bb", "+38761000010", 3L, false,
            new String[]{"Ana Kovačević", "Maja Đukić"},
            new String[]{"Fen i styling specijalista", "Kratke frizure ekspert"},
            new String[]{"Fen styling", "Kratke frizure", "Updo frizure", "Nadogradnja noktiju"},
            new double[]{20, 25, 30, 35},
            new int[]{30, 45, 60, 90});

        log.info("=== Seed završen: {} salona ===", salonRepository.count());
    }

    private void createSalon(String name, String slug, String desc,
                              String city, String address, String phone,
                              Long ownerId, boolean verified,
                              String[] hairdressers, String[] bios,
                              String[] services, double[] prices, int[] durations) {
        Salon salon = salonRepository.save(Salon.builder()
                .name(name).slug(slug).description(desc)
                .city(city).address(address).phone(phone)
                .ownerId(ownerId).verified(verified).isActive(true).build());

        for (int i = 0; i < hairdressers.length; i++) {
            hairdresserRepository.save(Hairdresser.builder()
                    .salon(salon).fullName(hairdressers[i])
                    .bio(bios[i]).isActive(true).avgRating(0.0).build());
        }

        for (int i = 0; i < services.length; i++) {
            serviceRepository.save(SalonService.builder()
                    .salon(salon).name(services[i])
                    .price(BigDecimal.valueOf(prices[i]))
                    .durationMinutes(durations[i])
                    .isActive(true).build());
        }

        addWorkingHours(salon);
    }

    private void addWorkingHours(Salon salon) {
        for (DayOfWeek day : DayOfWeek.values()) {
            boolean isDayOff = (day == DayOfWeek.SUNDAY);
            workingHoursRepository.save(WorkingHours.builder()
                    .salon(salon).dayOfWeek(day.getValue())
                    .startTime(isDayOff ? null : LocalTime.of(8, 0))
                    .endTime(isDayOff ? null : LocalTime.of(20, 0))
                    .isDayOff(isDayOff).build());
        }
    }
}
