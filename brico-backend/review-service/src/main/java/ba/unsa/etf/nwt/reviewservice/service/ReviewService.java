package ba.unsa.etf.nwt.reviewservice.service;

import ba.unsa.etf.nwt.reviewservice.dto.*;
import ba.unsa.etf.nwt.reviewservice.exception.DuplicateResourceException;
import ba.unsa.etf.nwt.reviewservice.exception.ResourceNotFoundException;
import ba.unsa.etf.nwt.reviewservice.model.Favorite;
import ba.unsa.etf.nwt.reviewservice.model.Review;
import ba.unsa.etf.nwt.reviewservice.repository.FavoriteRepository;
import ba.unsa.etf.nwt.reviewservice.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final FavoriteRepository favoriteRepository;
    private final ModelMapper modelMapper;

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
        // Prevent duplicate review for same appointment
        if (req.getAppointmentId() != null &&
                reviewRepository.existsByClientIdAndAppointmentId(req.getClientId(), req.getAppointmentId())) {
            throw new DuplicateResourceException("Recenzija za ovaj termin već postoji");
        }
        Review review = modelMapper.map(req, Review.class);
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
                .map(f -> modelMapper.map(f, FavoriteResponse.class)).toList();
    }

    @Transactional
    public FavoriteResponse addFavorite(FavoriteRequest req) {
        if (favoriteRepository.existsByUserIdAndSalonId(req.getUserId(), req.getSalonId())) {
            throw new DuplicateResourceException("Salon je već dodan u omiljene");
        }
        Favorite favorite = Favorite.builder()
                .userId(req.getUserId())
                .salonId(req.getSalonId())
                .build();
        return modelMapper.map(favoriteRepository.save(favorite), FavoriteResponse.class);
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
