package ba.unsa.etf.nwt.reviewservice.service;

import ba.unsa.etf.nwt.reviewservice.client.BookingClient;
import ba.unsa.etf.nwt.reviewservice.dto.*;
import ba.unsa.etf.nwt.reviewservice.exception.DuplicateResourceException;
import ba.unsa.etf.nwt.reviewservice.exception.ResourceNotFoundException;
import ba.unsa.etf.nwt.reviewservice.model.Favorite;
import ba.unsa.etf.nwt.reviewservice.model.Review;
import ba.unsa.etf.nwt.reviewservice.repository.FavoriteRepository;
import ba.unsa.etf.nwt.reviewservice.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final FavoriteRepository favoriteRepository;
    private final ModelMapper modelMapper;
    private final BookingClient bookingClient;

    // ── Reviews ────────────────────────────────────────────────────────

    public List<ReviewResponse> findBySalonId(Long salonId) {
        return reviewRepository.findBySalonId(salonId).stream()
                .map(r -> modelMapper.map(r, ReviewResponse.class)).toList();
    }

    public List<ReviewResponse> findByClientId(Long clientId) {
        return reviewRepository.findByClientId(clientId).stream()
                .map(r -> modelMapper.map(r, ReviewResponse.class)).toList();
    }

    public ReviewResponse findById(Long id) {
        return modelMapper.map(getReviewOrThrow(id), ReviewResponse.class);
    }

    // ── Paginacija ────────────────────────────────────────────────────

    public Page<ReviewResponse> findBySalonIdPaged(Long salonId, Pageable pageable) {
        return reviewRepository.findBySalonId(salonId, pageable)
                .map(r -> modelMapper.map(r, ReviewResponse.class));
    }

    public Page<ReviewResponse> findByClientIdPaged(Long clientId, Pageable pageable) {
        return reviewRepository.findByClientId(clientId, pageable)
                .map(r -> modelMapper.map(r, ReviewResponse.class));
    }

    // ── Statistika distribucije ocjena ────────────────────────────────

    public Map<String, Object> getSalonReviewStats(Long salonId) {
        Double avg = reviewRepository.calculateAverageRatingBySalonId(salonId);
        long total = reviewRepository.countBySalonId(salonId);
        Map<Integer, Long> distribution = new LinkedHashMap<>();
        for (int i = 1; i <= 5; i++) distribution.put(i, 0L);
        reviewRepository.countBySalonIdGroupByRating(salonId)
                .forEach(row -> distribution.put(((Number) row[0]).intValue(), (Long) row[1]));
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("salonId", salonId);
        stats.put("totalReviews", total);
        stats.put("averageRating", avg != null ? Math.round(avg * 10.0) / 10.0 : 0.0);
        stats.put("distribution", distribution);
        return stats;
    }

    public Map<String, Object> getSalonRating(Long salonId) {
        Double avg = reviewRepository.calculateAverageRatingBySalonId(salonId);
        long count = reviewRepository.countBySalonId(salonId);
        return Map.of(
                "salonId", salonId,
                "averageRating", avg != null ? Math.round(avg * 10.0) / 10.0 : 0.0,
                "reviewCount", count
        );
    }

    @Transactional
    public ReviewResponse create(ReviewRequest req) {
        // ── Inter-service validacija (sinhrona komunikacija putem Feign + Eureka) ──
        // Provjeri da termin postoji i da je COMPLETED u booking-service
        if (req.getAppointmentId() != null) {
            Map<String, Object> apptInfo = bookingClient.validateAppointment(req.getAppointmentId());
            boolean completed = Boolean.TRUE.equals(apptInfo.get("completed"));
            if (!completed) {
                String status = (String) apptInfo.get("status");
                throw new IllegalStateException(
                    "Recenzija se može ostaviti samo za završene termine. Status termina: " + status);
            }
        }

        // Prevent duplicate review for same appointment
        if (req.getAppointmentId() != null &&
                reviewRepository.existsByClientIdAndAppointmentId(req.getClientId(), req.getAppointmentId())) {
            throw new DuplicateResourceException("Recenzija za ovaj termin već postoji");
        }
        Review review = Review.builder()
                .salonId(req.getSalonId())
                .hairdresserId(req.getHairdresserId())
                .clientId(req.getClientId())
                .clientName(req.getClientName())
                .appointmentId(req.getAppointmentId())
                .rating(req.getRating())
                .comment(req.getComment())
                .build();
        return modelMapper.map(reviewRepository.save(review), ReviewResponse.class);
    }

    // ── PATCH — odgovor vlasnika ──────────────────────────────────────

    @Transactional
    public ReviewResponse addOwnerReply(Long id, OwnerReplyRequest req) {
        Review review = getReviewOrThrow(id);
        review.setOwnerReply(req.getReply());
        review.setOwnerReplyAt(LocalDateTime.now());
        return modelMapper.map(reviewRepository.save(review), ReviewResponse.class);
    }

    @Transactional
    public void delete(Long id) {
        getReviewOrThrow(id);
        reviewRepository.deleteById(id);
    }

    // ── Favorites ──────────────────────────────────────────────────────

    public List<FavoriteResponse> findFavoritesByUser(Long userId) {
        return favoriteRepository.findByUserId(userId).stream()
                .map(FavoriteResponse::from).toList();
    }

    @Transactional
    public FavoriteResponse addFavorite(FavoriteRequest req) {
        if (favoriteRepository.existsByUserIdAndSalonId(req.getUserId(), req.getSalonId())) {
            throw new DuplicateResourceException("Salon je već dodan u omiljene");
        }
        Favorite favorite = Favorite.builder()
                .userId(req.getUserId())
                .salonId(req.getSalonId())
                .salonName(req.getSalonName())
                .salonSlug(req.getSalonSlug())
                .salonCity(req.getSalonCity())
                .salonAvgRating(req.getSalonAvgRating())
                .salonVerified(req.getSalonVerified())
                .build();
        return FavoriteResponse.from(favoriteRepository.save(favorite));
    }

    @Transactional
    public void removeFavorite(Long userId, Long salonId) {
        if (!favoriteRepository.existsByUserIdAndSalonId(userId, salonId)) {
            throw new ResourceNotFoundException("Salon nije pronađen u omiljenim");
        }
        favoriteRepository.deleteByUserIdAndSalonId(userId, salonId);
    }

    private Review getReviewOrThrow(Long id) {
        return reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recenzija sa ID=" + id + " nije pronađena"));
    }
}
