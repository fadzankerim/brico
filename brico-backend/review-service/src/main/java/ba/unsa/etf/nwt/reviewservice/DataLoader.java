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

// ═══════════════════════════════════════════════════════════════════════
//  Referentni ID-ovi:
//  Klijenti: 17=Amina Begić  18=Emir Zukić  36=Marko Petrović
//            37=Ivana Horvat  38=Dino Bajrić
//  Frizeri:
//   1=Lejla(EC)  2=Tarik(EC)  3=Edin(UB)  4=Amina Z.(UB)
//   5=Nina(GS)   6=Sara(GS)   7=Haris(BK) 8=Denis(BK)
//   9=Jovana(SL) 10=Aleksandar(SL)
//  11=Naida(EC)  12=Muhamed(UB)  13=Lejla Č.(GS)  14=Nermin(BK)  15=Tijana(SL)
//  16=Viktor(TGC)  17=Dario(TGC)  18=Bojan(TGC)
//  19=Belma(LBL)   20=Dina(LBL)   21=Amra(LBL)
//  22=Stefan(BS)   23=Lana(BS)    24=Niko(BS)
//  Saloni: 1=EC  2=UB  3=GS  4=BK  5=SL  6=TGC  7=LBL  8=BS
//  Termini (appointmentId → COMPLETED):
//   a1–a19 originalni; a26–a30 Marko; a33–a38 Ivana; a41–a45 Dino;
//   a54 Amina/Naida; a56 Emir/Muhamed; a58 Marko/Nermin; a60 Marko/Bojan
//
//  Prosječne ocjene po završetku seed-a:
//   Elite Cut (1):           (5+5+5+5+5+4+5)/7  = 4.86 ≈ 4.9
//   Urban Barber (2):        (4+4+5+5+4+4)/6    = 4.33 ≈ 4.3
//   Glam Studio (3):         (4+5+4+4+5)/5      = 4.40 ≈ 4.4
//   Barber Kingdom (4):      (4+3+5+5+4+4+4)/7  = 4.14 ≈ 4.1
//   Style Lab (5):           (4+3+4+5)/4        = 4.00
//   The Gentlemen's Club (6):(5+5+4+5)/4        = 4.75 ≈ 4.8
//   Luxe Beauty Lounge (7):  (5+5+4+5)/4        = 4.75 ≈ 4.8
//   Brico Studio (8):        (4+4+4)/3          = 4.00
// ═══════════════════════════════════════════════════════════════════════

@Component
@RequiredArgsConstructor
@Slf4j
public class DataLoader implements CommandLineRunner {

    private final ReviewRepository   reviewRepository;
    private final FavoriteRepository favoriteRepository;

    @Override
    public void run(String... args) {
        if (reviewRepository.count() > 0) {
            log.info("=== Review Service: podaci već postoje, preskačem seed ===");
            return;
        }
        log.info("=== Seed: review-service ===");

        // ── Recenzije od Amine Begić (clientId=17) ───────────────────────────

        List<Review> aminaReviews = List.of(

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

                // a54: Naida @ Elite Cut ⭐5
                Review.builder()
                        .clientId(17L).clientName("Amina Begić")
                        .salonId(1L).hairdresserId(11L).hairdresserName("Naida Halilović")
                        .appointmentId(54L).rating(5)
                        .comment("Naida je odlična addicija timu Elite Cuta. Šišanje je precizno i detaljna pažnja prema željama klijenta.")
                        .build()
        );

        // ── Recenzije od Emira Zukića (clientId=18) ─────────────────────────

        List<Review> emirReviews = List.of(

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
                        .build(),

                // a56: Muhamed @ Urban Barber ⭐4
                Review.builder()
                        .clientId(18L).clientName("Emir Zukić")
                        .salonId(2L).hairdresserId(12L).hairdresserName("Muhamed Husić")
                        .appointmentId(56L).rating(4)
                        .comment("Muhamed je solidan barber, profesionalan i uredan. Dobra nadopuna timu Urban Barbera.")
                        .build()
        );

        // ── Recenzije od Marka Petrovića (clientId=36) ──────────────────────

        List<Review> markoReviews = List.of(

                // a26: Haris @ Barber Kingdom ⭐4
                Review.builder()
                        .clientId(36L).clientName("Marko Petrović")
                        .salonId(4L).hairdresserId(7L).hairdresserName("Haris Muratović")
                        .appointmentId(26L).rating(4)
                        .comment("Komplet usluga za 35 KM — vrijedi svaki dinar. Haris je profesionalac, barber s klasom.")
                        .build(),

                // a27: Viktor @ The Gentlemen's Club ⭐5
                Review.builder()
                        .clientId(36L).clientName("Marko Petrović")
                        .salonId(6L).hairdresserId(16L).hairdresserName("Viktor Blažević")
                        .appointmentId(27L).rating(5)
                        .comment("The Gentlemen's Club je nevjerovatan doživljaj. Viktor je pravi majstor — pažljiv, precizan i stručan!")
                        .build(),

                // a28: Dario @ The Gentlemen's Club ⭐5
                Review.builder()
                        .clientId(36L).clientName("Marko Petrović")
                        .salonId(6L).hairdresserId(17L).hairdresserName("Dario Šimić")
                        .appointmentId(28L).rating(5)
                        .comment("Royal shave kod Darija je iskustvo koje se mora doživjeti. Pravi luksuz za razumnu cijenu!")
                        .build(),

                // a29: Aleksandar @ Style Lab ⭐4
                Review.builder()
                        .clientId(36L).clientName("Marko Petrović")
                        .salonId(5L).hairdresserId(10L).hairdresserName("Aleksandar Perić")
                        .appointmentId(29L).rating(4)
                        .comment("Šišanje kod Aleksandra — solidno i čisto. Style Lab ima jedinstven pristup, preporučujem onima koji vole eksperimentisati.")
                        .build(),

                // a30: Bojan @ The Gentlemen's Club ⭐4
                Review.builder()
                        .clientId(36L).clientName("Marko Petrović")
                        .salonId(6L).hairdresserId(18L).hairdresserName("Bojan Krsmanović")
                        .appointmentId(30L).rating(4)
                        .comment("Skin fade + konture — Bojan radi precizno i polako. Atmosfera u salonu je fantastična.")
                        .build(),

                // a58: Nermin @ Barber Kingdom ⭐4
                Review.builder()
                        .clientId(36L).clientName("Marko Petrović")
                        .salonId(4L).hairdresserId(14L).hairdresserName("Nermin Salihović")
                        .appointmentId(58L).rating(4)
                        .comment("Nermin je profesionalan i temeljit. Klasično šišanje bez iznenađenja — baš kako treba.")
                        .build(),

                // a60: Bojan @ The Gentlemen's Club ⭐5
                Review.builder()
                        .clientId(36L).clientName("Marko Petrović")
                        .salonId(6L).hairdresserId(18L).hairdresserName("Bojan Krsmanović")
                        .appointmentId(60L).rating(5)
                        .comment("Drugi put kod Bojana i svaki put odlično. Skin fade koji traje i izgleda besprijekorno!")
                        .build()
        );

        // ── Recenzije od Ivane Horvat (clientId=37) ──────────────────────────

        List<Review> ivanaReviews = List.of(

                // a33: Belma @ Luxe Beauty Lounge ⭐5
                Review.builder()
                        .clientId(37L).clientName("Ivana Horvat")
                        .salonId(7L).hairdresserId(19L).hairdresserName("Belma Selimović")
                        .appointmentId(33L).rating(5)
                        .comment("Luxe Beauty Lounge je raj za žene! Belma je napravila savršen balayage — prirodan, sjajan i dugotrajni. Jedino mjesto gdje ću dolaziti!")
                        .build(),

                // a34: Nina @ Glam Studio ⭐4
                Review.builder()
                        .clientId(37L).clientName("Ivana Horvat")
                        .salonId(3L).hairdresserId(5L).hairdresserName("Nina Softić")
                        .appointmentId(34L).rating(4)
                        .comment("Nina je odlična frizerka, pramenovi su dobro urađeni. Glam Studio je prijatan salon.")
                        .build(),

                // a35: Dina @ Luxe Beauty Lounge ⭐5
                Review.builder()
                        .clientId(37L).clientName("Ivana Horvat")
                        .salonId(7L).hairdresserId(20L).hairdresserName("Dina Terzić")
                        .appointmentId(35L).rating(5)
                        .comment("Keratin tretman kod Dine je promijenio moju kosu! Nevjerovatno gladka i sjajna kosa tjednima. Apsolutno preporučujem!")
                        .build(),

                // a36: Lejla Č. @ Glam Studio ⭐5
                Review.builder()
                        .clientId(37L).clientName("Ivana Horvat")
                        .salonId(3L).hairdresserId(13L).hairdresserName("Lejla Čaušević")
                        .appointmentId(36L).rating(5)
                        .comment("Lejla Čaušević je nevjerovatna za ombre tehniku — precizna, strastvena i ima sjajan osjećaj za boje. Salonska zvijezda!")
                        .build(),

                // a37: Amra @ Luxe Beauty Lounge ⭐4
                Review.builder()
                        .clientId(37L).clientName("Ivana Horvat")
                        .salonId(7L).hairdresserId(21L).hairdresserName("Amra Husejnović")
                        .appointmentId(37L).rating(4)
                        .comment("Olaplex tretman je bio odličan. Amra je pažljiva i stručna, kosa mi je primjetno zdravija.")
                        .build(),

                // a38: Belma @ Luxe Beauty Lounge ⭐5
                Review.builder()
                        .clientId(37L).clientName("Ivana Horvat")
                        .salonId(7L).hairdresserId(19L).hairdresserName("Belma Selimović")
                        .appointmentId(38L).rating(5)
                        .comment("Svečana frizura za posebnu prigodu — Belma je bila apsolutno savršena. Frizura je izdržala cijeli dan. Hvala puno!")
                        .build()
        );

        // ── Recenzije od Dine Bajrića (clientId=38) ──────────────────────────

        List<Review> dinoReviews = List.of(

                // a41: Stefan @ Brico Studio ⭐4
                Review.builder()
                        .clientId(38L).clientName("Dino Bajrić")
                        .salonId(8L).hairdresserId(22L).hairdresserName("Stefan Đorđević")
                        .appointmentId(41L).rating(4)
                        .comment("Stefan u Brico Studiju radi zaista kvalitetno šišanje po razumnoj cijeni. Moderna atmosfera i prijatno osoblje.")
                        .build(),

                // a42: Edin @ Urban Barber ⭐5
                Review.builder()
                        .clientId(38L).clientName("Dino Bajrić")
                        .salonId(2L).hairdresserId(3L).hairdresserName("Edin Kovač")
                        .appointmentId(42L).rating(5)
                        .comment("Edin je odličan! Fade je bio savršen — čiste linije i precizno urađeno. Urban Barber je top!")
                        .build(),

                // a43: Lana @ Brico Studio ⭐4
                Review.builder()
                        .clientId(38L).clientName("Dino Bajrić")
                        .salonId(8L).hairdresserId(23L).hairdresserName("Lana Begović")
                        .appointmentId(43L).rating(4)
                        .comment("Lana je talentovana za bojenje. Boja je ispala lijepo, taman onako kako sam tražio. Brico Studio ima dobar tim.")
                        .build(),

                // a44: Tijana @ Style Lab ⭐5
                Review.builder()
                        .clientId(38L).clientName("Dino Bajrić")
                        .salonId(5L).hairdresserId(15L).hairdresserName("Tijana Marković")
                        .appointmentId(44L).rating(5)
                        .comment("Tijana iz Style Laba je kreativna i precizna. Kreativno bojenje koje sam dobio je nevjerovatno — komplimenti sa svake strane!")
                        .build(),

                // a45: Niko @ Brico Studio ⭐4
                Review.builder()
                        .clientId(38L).clientName("Dino Bajrić")
                        .salonId(8L).hairdresserId(24L).hairdresserName("Niko Perišić")
                        .appointmentId(45L).rating(4)
                        .comment("Niko je odradio lijep ombre efekt. Tehnika je solidna, rezultat je prirodan. Definitivno se vraćam u Brico Studio.")
                        .build()
        );

        reviewRepository.saveAll(aminaReviews);
        reviewRepository.saveAll(emirReviews);
        reviewRepository.saveAll(markoReviews);
        reviewRepository.saveAll(ivanaReviews);
        reviewRepository.saveAll(dinoReviews);

        log.info("Kreirano {} recenzija", reviewRepository.count());

        // ── Omiljeni saloni ──────────────────────────────────────────────────

        List<Favorite> favorites = List.of(
                // Amina Begić (userId=17)
                Favorite.builder().userId(17L).salonId(1L)
                        .salonName("Elite Cut").salonSlug("elite-cut")
                        .salonCity("Sarajevo").salonAvgRating(4.9).salonVerified(true).build(),
                Favorite.builder().userId(17L).salonId(3L)
                        .salonName("Glam Studio").salonSlug("glam-studio")
                        .salonCity("Sarajevo").salonAvgRating(4.4).salonVerified(true).build(),
                Favorite.builder().userId(17L).salonId(5L)
                        .salonName("Style Lab").salonSlug("style-lab")
                        .salonCity("Banja Luka").salonAvgRating(4.0).salonVerified(true).build(),

                // Emir Zukić (userId=18)
                Favorite.builder().userId(18L).salonId(1L)
                        .salonName("Elite Cut").salonSlug("elite-cut")
                        .salonCity("Sarajevo").salonAvgRating(4.9).salonVerified(true).build(),
                Favorite.builder().userId(18L).salonId(2L)
                        .salonName("Urban Barber").salonSlug("urban-barber")
                        .salonCity("Mostar").salonAvgRating(4.3).salonVerified(true).build(),
                Favorite.builder().userId(18L).salonId(4L)
                        .salonName("Barber Kingdom").salonSlug("barber-kingdom")
                        .salonCity("Tuzla").salonAvgRating(4.1).salonVerified(true).build(),

                // Marko Petrović (userId=36)
                Favorite.builder().userId(36L).salonId(4L)
                        .salonName("Barber Kingdom").salonSlug("barber-kingdom")
                        .salonCity("Tuzla").salonAvgRating(4.1).salonVerified(true).build(),
                Favorite.builder().userId(36L).salonId(6L)
                        .salonName("The Gentlemen's Club").salonSlug("gentlemens-club")
                        .salonCity("Sarajevo").salonAvgRating(4.8).salonVerified(true).build(),

                // Ivana Horvat (userId=37)
                Favorite.builder().userId(37L).salonId(3L)
                        .salonName("Glam Studio").salonSlug("glam-studio")
                        .salonCity("Sarajevo").salonAvgRating(4.4).salonVerified(true).build(),
                Favorite.builder().userId(37L).salonId(7L)
                        .salonName("Luxe Beauty Lounge").salonSlug("luxe-beauty-lounge")
                        .salonCity("Mostar").salonAvgRating(4.8).salonVerified(true).build(),

                // Dino Bajrić (userId=38)
                Favorite.builder().userId(38L).salonId(5L)
                        .salonName("Style Lab").salonSlug("style-lab")
                        .salonCity("Banja Luka").salonAvgRating(4.0).salonVerified(true).build(),
                Favorite.builder().userId(38L).salonId(8L)
                        .salonName("Brico Studio").salonSlug("brico-studio")
                        .salonCity("Banja Luka").salonAvgRating(4.0).salonVerified(true).build()
        );

        favoriteRepository.saveAll(favorites);
        log.info("Kreirano {} omiljenih salona", favorites.size());
        log.info("=== Review Service seed završen ===");
    }
}
