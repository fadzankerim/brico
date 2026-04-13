package ba.unsa.etf.nwt.reviewservice.service;

import ba.unsa.etf.nwt.reviewservice.dto.*;
import ba.unsa.etf.nwt.reviewservice.exception.DuplicateResourceException;
import ba.unsa.etf.nwt.reviewservice.exception.ResourceNotFoundException;
import ba.unsa.etf.nwt.reviewservice.model.Favorite;
import ba.unsa.etf.nwt.reviewservice.model.Review;
import ba.unsa.etf.nwt.reviewservice.repository.FavoriteRepository;
import ba.unsa.etf.nwt.reviewservice.repository.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock ReviewRepository reviewRepository;
    @Mock FavoriteRepository favoriteRepository;
    @InjectMocks ReviewService reviewService;

    private final ModelMapper modelMapper = new ModelMapper();

    @BeforeEach
    void setup() throws Exception {
        var field = ReviewService.class.getDeclaredField("modelMapper");
        field.setAccessible(true);
        field.set(reviewService, modelMapper);
    }

    private Review sampleReview() {
        return Review.builder()
                .id(1L).clientId(1L).clientName("Ana Anić")
                .salonId(1L).rating(5).comment("Odlično!")
                .build();
    }

    @Test
    void findBySalonId_returnsReviews() {
        when(reviewRepository.findBySalonId(1L)).thenReturn(List.of(sampleReview()));
        List<ReviewResponse> result = reviewService.findBySalonId(1L);
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getRating()).isEqualTo(5);
    }

    @Test
    void findById_existing_returnsReview() {
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(sampleReview()));
        ReviewResponse resp = reviewService.findById(1L);
        assertThat(resp.getComment()).isEqualTo("Odlično!");
    }

    @Test
    void findById_unknown_throwsNotFound() {
        when(reviewRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> reviewService.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void create_newReview_saves() {
        when(reviewRepository.save(any())).thenReturn(sampleReview());

        ReviewRequest req = new ReviewRequest();
        req.setClientId(1L); req.setClientName("Ana");
        req.setSalonId(1L); req.setRating(5);

        ReviewResponse resp = reviewService.create(req);
        assertThat(resp.getRating()).isEqualTo(5);
    }

    @Test
    void create_duplicateAppointmentReview_throwsConflict() {
        when(reviewRepository.existsByClientIdAndAppointmentId(1L, 1L)).thenReturn(true);

        ReviewRequest req = new ReviewRequest();
        req.setClientId(1L); req.setClientName("Ana");
        req.setSalonId(1L); req.setRating(5);
        req.setAppointmentId(1L);

        assertThatThrownBy(() -> reviewService.create(req))
                .isInstanceOf(DuplicateResourceException.class);
    }

    @Test
    void getSalonRating_returnsAverageAndCount() {
        when(reviewRepository.calculateAverageRatingBySalonId(1L)).thenReturn(4.5);
        when(reviewRepository.countBySalonId(1L)).thenReturn(10L);

        Map<String, Object> result = reviewService.getSalonRating(1L);
        assertThat(result.get("averageRating")).isEqualTo(4.5);
        assertThat(result.get("reviewCount")).isEqualTo(10L);
    }

    @Test
    void addFavorite_new_saves() {
        when(favoriteRepository.existsByUserIdAndSalonId(1L, 2L)).thenReturn(false);
        Favorite saved = Favorite.builder().id(1L).userId(1L).salonId(2L).build();
        when(favoriteRepository.save(any())).thenReturn(saved);

        FavoriteRequest req = new FavoriteRequest();
        req.setUserId(1L); req.setSalonId(2L);

        FavoriteResponse resp = reviewService.addFavorite(req);
        assertThat(resp.getSalonId()).isEqualTo(2L);
    }

    @Test
    void addFavorite_duplicate_throwsConflict() {
        when(favoriteRepository.existsByUserIdAndSalonId(1L, 2L)).thenReturn(true);

        FavoriteRequest req = new FavoriteRequest();
        req.setUserId(1L); req.setSalonId(2L);

        assertThatThrownBy(() -> reviewService.addFavorite(req))
                .isInstanceOf(DuplicateResourceException.class);
    }

    // ── Novi testovi za Zadatak 4 ─────────────────────────────────────

    @Test
    void findBySalonIdPaged_returnsPaginatedReviews() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Review> page = new PageImpl<>(List.of(sampleReview()), pageable, 1);
        when(reviewRepository.findBySalonId(1L, pageable)).thenReturn(page);

        Page<ReviewResponse> result = reviewService.findBySalonIdPaged(1L, pageable);
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getRating()).isEqualTo(5);
    }

    @Test
    void addOwnerReply_savesReplyAndTimestamp() {
        Review review = sampleReview();
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));
        when(reviewRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        OwnerReplyRequest req = new OwnerReplyRequest();
        req.setReply("Hvala na recenziji!");

        ReviewResponse resp = reviewService.addOwnerReply(1L, req);
        assertThat(resp.getOwnerReply()).isEqualTo("Hvala na recenziji!");
        assertThat(resp.getOwnerReplyAt()).isNotNull();
    }

    @Test
    void getSalonReviewStats_returnsDistributionAndAverage() {
        when(reviewRepository.countBySalonId(1L)).thenReturn(3L);
        when(reviewRepository.calculateAverageRatingBySalonId(1L)).thenReturn(4.0);
        when(reviewRepository.countBySalonIdGroupByRating(1L)).thenReturn(
                List.of(new Object[]{4, 2L}, new Object[]{5, 1L})
        );

        Map<String, Object> stats = reviewService.getSalonReviewStats(1L);
        assertThat(stats.get("totalReviews")).isEqualTo(3L);
        assertThat(stats.get("averageRating")).isEqualTo(4.0);

        @SuppressWarnings("unchecked")
        Map<Integer, Long> dist = (Map<Integer, Long>) stats.get("distribution");
        assertThat(dist.get(4)).isEqualTo(2L);
        assertThat(dist.get(5)).isEqualTo(1L);
        assertThat(dist.get(1)).isEqualTo(0L); // nezastupljene ocjene su 0
    }
}
