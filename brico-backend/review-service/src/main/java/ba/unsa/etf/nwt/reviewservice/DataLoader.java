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

@Component
@RequiredArgsConstructor
@Slf4j
public class DataLoader implements CommandLineRunner {

    private final ReviewRepository reviewRepository;
    private final FavoriteRepository favoriteRepository;

    @Override
    public void run(String... args) {
        if (reviewRepository.count() > 0) {
            log.info("=== Review Service: podaci već postoje, preskačem seed ===");
            return;
        }
        log.info("=== Učitavanje početnih podataka za Review Service ===");

        // ── Recenzije ─────────────────────────────────────────────
        List<Review> reviews = List.of(
                Review.builder()
                        .clientId(7L)
                        .clientName("Amina Begić")
                        .salonId(1L)
                        .hairdresserId(1L)
                        .hairdresserName("Lejla Mehić")
                        .appointmentId(1L)
                        .rating(5)
                        .comment("Odlična usluga! Lejla je stvarno profesionalac, jako sam zadovoljna rezultatom.")
                        .build(),

                Review.builder()
                        .clientId(8L)
                        .clientName("Emir Zukić")
                        .salonId(1L)
                        .hairdresserId(2L)
                        .hairdresserName("Tarik Bašić")
                        .appointmentId(2L)
                        .rating(4)
                        .comment("Veoma dobar salon, ljubazno osoblje. Preporučujem Tarika za muške frizure.")
                        .build(),

                Review.builder()
                        .clientId(8L)
                        .clientName("Emir Zukić")
                        .salonId(2L)
                        .hairdresserId(3L)
                        .hairdresserName("Nina Softić")
                        .rating(3)
                        .comment("Solidna usluga, ali čekanje je bilo dugo.")
                        .build(),

                Review.builder()
                        .clientId(7L)
                        .clientName("Amina Begić")
                        .salonId(1L)
                        .rating(5)
                        .comment("Najljepši salon u Sarajevu! Uvijek se vraćam.")
                        .build(),

                Review.builder()
                        .clientId(9L)
                        .clientName("Maja Đozić")
                        .salonId(2L)
                        .rating(4)
                        .comment("Pristupačne cijene i kvalitetna usluga. Preporučujem Barbershop King!")
                        .build()
        );

        reviewRepository.saveAll(reviews);
        log.info("Kreirano {} recenzija", reviewRepository.count());

        // ── Omiljeni saloni ───────────────────────────────────────
        List<Favorite> favorites = List.of(
                Favorite.builder().userId(7L).salonId(1L).build(),
                Favorite.builder().userId(7L).salonId(2L).build(),
                Favorite.builder().userId(8L).salonId(1L).build(),
                Favorite.builder().userId(9L).salonId(2L).build(),
                Favorite.builder().userId(9L).salonId(3L).build()
        );

        favoriteRepository.saveAll(favorites);
        log.info("Kreirano {} omiljenih salona", favoriteRepository.count());

        // Log prosječnih ocjena
        Double avgSalon1 = reviewRepository.calculateAverageRatingBySalonId(1L);
        Double avgSalon2 = reviewRepository.calculateAverageRatingBySalonId(2L);
        log.info("Prosječna ocjena Salon 1: {}", avgSalon1);
        log.info("Prosječna ocjena Salon 2: {}", avgSalon2);
        log.info("=== Review Service inicijalizacija završena ===");
    }
}
