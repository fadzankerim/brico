package ba.unsa.etf.nwt.reviewservice;

import ba.unsa.etf.nwt.reviewservice.model.Favorite;
import ba.unsa.etf.nwt.reviewservice.model.Review;
import ba.unsa.etf.nwt.reviewservice.repository.FavoriteRepository;
import ba.unsa.etf.nwt.reviewservice.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

// Referentni ID-ovi:
//  Klijenti: 17=Amina Begić, 18=Emir Zukić
//  Frizeri (hairdresserID u salon-service):
//    1=Lejla(EC), 2=Tarik(EC), 3=Edin(UB), 4=Amina Z.(UB),
//    5=Nina(GS), 6=Sara(GS), 7=Haris(BK), 8=Denis(BK), 9=Jovana(SL), 10=Aleksandar(SL)
//  Saloni: 1=Elite Cut, 2=Urban Barber, 3=Glam Studio, 4=Barber Kingdom, 5=Style Lab
//  Termini (appointmentId): a1–a19 su COMPLETED (1–19), a20–a21 su CANCELLED
//
// Prosječne ocjene po završetku seed-a:
//   Elite Cut     → (5+5+5+5+5+4) / 6 = 4.83
//   Urban Barber  → (4+4+5)       / 3 = 4.33
//   Glam Studio   → (4+5+4)       / 3 = 4.33
//   Barber Kingdom→ (4+3+5+5+4)   / 5 = 4.20
//   Style Lab     → (4+3)         / 2 = 3.50

@Component
@RequiredArgsConstructor
@Slf4j
public class DataLoader implements CommandLineRunner {

    private final ReviewRepository  reviewRepository;
    private final FavoriteRepository favoriteRepository;

    @Override
    public void run(String... args) {
        if (reviewRepository.count() > 0) {
            log.info("=== Review Service: podaci već postoje, preskačem seed ===");
            return;
        }
        log.info("=== Seed: review-service ===");

        // ── Recenzije od Amine Begić (clientId=17) ──────────────────────

        List<Review> reviews = List.of(

                // a1: Lejla @ Elite Cut ⭐5
                Review.builder()
                        .clientId(17L).clientName("Amina Begić")
                        .salonId(1L).hairdresserId(1L).hairdresserName("Lejla Mehić")
                        .appointmentId(1L).rating(5)
                        .comment("Lejla je nevjerovatna! Šišanje je savršeno, definitivno dolazim ponovo.")
                        .build(),

                // a3: Nina @ Glam Studio ⭐4
                Review.builder()
                        .clientId(17L).clientName("Amina Begić")
                        .salonId(3L).hairdresserId(5L).hairdresserName("Nina Softić")
                        .appointmentId(3L).rating(4)
                        .comment("Odlično bojenje, boja je tačno onakva kakvu sam tražila. Preporučujem!")
                        .build(),

                // a6: Lejla @ Elite Cut ⭐5
                Review.builder()
                        .clientId(17L).clientName("Amina Begić")
                        .salonId(1L).hairdresserId(1L).hairdresserName("Lejla Mehić")
                        .appointmentId(6L).rating(5)
                        .comment("Balayage je izvanredan! Toliko prirodan i lijep. Lejla je majstor svog zanata.")
                        .build(),

                // a8: Sara @ Glam Studio ⭐5
                Review.builder()
                        .clientId(17L).clientName("Amina Begić")
                        .salonId(3L).hairdresserId(6L).hairdresserName("Sara Begić")
                        .appointmentId(8L).rating(5)
                        .comment("Sara je prava umjetnica. Pramenovi su prekrasni, tačno ono što sam zamišljala!")
                        .build(),

                // a10: Jovana @ Style Lab ⭐4
                Review.builder()
                        .clientId(17L).clientName("Amina Begić")
                        .salonId(5L).hairdresserId(9L).hairdresserName("Jovana Nikolić")
                        .appointmentId(10L).rating(4)
                        .comment("Kreativno bojenje je odlično, Jovana ima dobar osjećaj za boju i stil.")
                        .build(),

                // a12: Lejla @ Elite Cut ⭐5
                Review.builder()
                        .clientId(17L).clientName("Amina Begić")
                        .salonId(1L).hairdresserId(1L).hairdresserName("Lejla Mehić")
                        .appointmentId(12L).rating(5)
                        .comment("Keratin tretman je savršen! Kosa mi je nevjerovatno mekana i sjajna. 10/10.")
                        .build(),

                // a14: Nina @ Glam Studio ⭐4
                Review.builder()
                        .clientId(17L).clientName("Amina Begić")
                        .salonId(3L).hairdresserId(5L).hairdresserName("Nina Softić")
                        .appointmentId(14L).rating(4)
                        .comment("Bojenje i šišanje su super. Jedino malo duže čekanje na početku.")
                        .build(),

                // a18: Lejla @ Elite Cut ⭐5
                Review.builder()
                        .clientId(17L).clientName("Amina Begić")
                        .salonId(1L).hairdresserId(1L).hairdresserName("Lejla Mehić")
                        .appointmentId(18L).rating(5)
                        .comment("Ponovo kod Lejle i ponovo savršeno. Balayage koji traje i izgleda nevjerovatno!")
                        .build(),

                // ── Recenzije od Emira Zukića (clientId=18) ─────────────

                // a2: Tarik @ Elite Cut ⭐5
                Review.builder()
                        .clientId(18L).clientName("Emir Zukić")
                        .salonId(1L).hairdresserId(2L).hairdresserName("Tarik Bašić")
                        .appointmentId(2L).rating(5)
                        .comment("Tarik zna šta radi. Jako sam zadovoljan frizurom i brijanjem. Definitivno se vraćam!")
                        .build(),

                // a4: Haris @ Barber Kingdom ⭐4
                Review.builder()
                        .clientId(18L).clientName("Emir Zukić")
                        .salonId(4L).hairdresserId(7L).hairdresserName("Haris Muratović")
                        .appointmentId(4L).rating(4)
                        .comment("Dobar barbershop, Haris je profesionalac. Komplet usluga vrijedna para.")
                        .build(),

                // a5: Denis @ Barber Kingdom ⭐3
                Review.builder()
                        .clientId(18L).clientName("Emir Zukić")
                        .salonId(4L).hairdresserId(8L).hairdresserName("Denis Avdić")
                        .appointmentId(5L).rating(3)
                        .comment("Solidna frizura, ali nisam bio u potpunosti zadovoljan. Možda sljedeći put bolje.")
                        .build(),

                // a7: Edin @ Urban Barber ⭐4
                Review.builder()
                        .clientId(18L).clientName("Emir Zukić")
                        .salonId(2L).hairdresserId(3L).hairdresserName("Edin Kovač")
                        .appointmentId(7L).rating(4)
                        .comment("Fade je super urađen, Edin je profesionalac. Atmosfera u salonu je odlična.")
                        .build(),

                // a9: Haris @ Barber Kingdom ⭐5
                Review.builder()
                        .clientId(18L).clientName("Emir Zukić")
                        .salonId(4L).hairdresserId(7L).hairdresserName("Haris Muratović")
                        .appointmentId(9L).rating(5)
                        .comment("Ovaj put Haris je nadmašio sva očekivanja. Komplet usluga — šišanje i oblikovanje brade su savršeni!")
                        .build(),

                // a11: Tarik @ Elite Cut ⭐4
                Review.builder()
                        .clientId(18L).clientName("Emir Zukić")
                        .salonId(1L).hairdresserId(2L).hairdresserName("Tarik Bašić")
                        .appointmentId(11L).rating(4)
                        .comment("Opet Tarik, opet kvalitetna usluga. Elite Cut je moj go-to salon u Sarajevu.")
                        .build(),

                // a13: Denis @ Barber Kingdom ⭐5
                Review.builder()
                        .clientId(18L).clientName("Emir Zukić")
                        .salonId(4L).hairdresserId(8L).hairdresserName("Denis Avdić")
                        .appointmentId(13L).rating(5)
                        .comment("Brijanje s hot-towel maskom je odlično iskustvo. Denis je poboljšao tehniku. Preporučujem!")
                        .build(),

                // a15: Edin @ Urban Barber ⭐4
                Review.builder()
                        .clientId(18L).clientName("Emir Zukić")
                        .salonId(2L).hairdresserId(3L).hairdresserName("Edin Kovač")
                        .appointmentId(15L).rating(4)
                        .comment("Brzo i kvalitetno, tačno ono što mi treba. Urban Barber ne razočara.")
                        .build(),

                // a16: Aleksandar @ Style Lab ⭐3
                Review.builder()
                        .clientId(18L).clientName("Emir Zukić")
                        .salonId(5L).hairdresserId(10L).hairdresserName("Aleksandar Perić")
                        .appointmentId(16L).rating(3)
                        .comment("Boja je OK ali mi je izgledala malo drugačije nego što sam zamišljao. Možda nije bio moj stil.")
                        .build(),

                // a17: Haris @ Barber Kingdom ⭐4
                Review.builder()
                        .clientId(18L).clientName("Emir Zukić")
                        .salonId(4L).hairdresserId(7L).hairdresserName("Haris Muratović")
                        .appointmentId(17L).rating(4)
                        .comment("Treći put kod Harisa. Konzistentno dobra usluga, uvijek zadovoljan.")
                        .build(),

                // a19: Amina Z. @ Urban Barber ⭐5
                Review.builder()
                        .clientId(18L).clientName("Emir Zukić")
                        .salonId(2L).hairdresserId(4L).hairdresserName("Amina Zukić")
                        .appointmentId(19L).rating(5)
                        .comment("Amina je odlična! Brijanje je precizno i čisto. Urban Barber ima sjajno osoblje.")
                        .build()
        );

        reviewRepository.saveAll(reviews);
        log.info("Kreirano {} recenzija", reviews.size());

        // ── Omiljeni saloni ───────────────────────────────────────────────
        List<Favorite> favorites = List.of(
                Favorite.builder().userId(17L).salonId(1L).build(), // Amina voli Elite Cut
                Favorite.builder().userId(17L).salonId(3L).build(), // Amina voli Glam Studio
                Favorite.builder().userId(17L).salonId(5L).build(), // Amina voli Style Lab
                Favorite.builder().userId(18L).salonId(1L).build(), // Emir voli Elite Cut
                Favorite.builder().userId(18L).salonId(2L).build(), // Emir voli Urban Barber
                Favorite.builder().userId(18L).salonId(4L).build()  // Emir voli Barber Kingdom
        );

        favoriteRepository.saveAll(favorites);
        log.info("Kreirano {} omiljenih salona", favorites.size());
        log.info("=== Review Service seed završen ===");
    }
}
