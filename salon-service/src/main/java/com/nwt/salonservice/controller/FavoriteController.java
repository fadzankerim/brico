package com.nwt.salonservice.controller;

import com.nwt.salonservice.dto.request.FavoriteRequest;
import com.nwt.salonservice.dto.response.FavoriteResponse;
import com.nwt.salonservice.exception.BusinessException;
import com.nwt.salonservice.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    @GetMapping
    public ResponseEntity<List<FavoriteResponse>> getMyFavorites(
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        if (userId == null) {
            throw new BusinessException("User identification required", HttpStatus.UNAUTHORIZED, "UNAUTHORIZED");
        }
        return ResponseEntity.ok(favoriteService.findByUserId(userId));
    }

    @PostMapping
    public ResponseEntity<FavoriteResponse> addFavorite(
            @RequestBody FavoriteRequest request,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        if (userId == null) {
            throw new BusinessException("User identification required", HttpStatus.UNAUTHORIZED, "UNAUTHORIZED");
        }
        request.setUserId(userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(favoriteService.create(request));
    }

    @DeleteMapping("/{salonId}")
    public ResponseEntity<Void> removeFavorite(
            @PathVariable Long salonId,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        if (userId == null) {
            throw new BusinessException("User identification required", HttpStatus.UNAUTHORIZED, "UNAUTHORIZED");
        }
        favoriteService.deleteByUserIdAndSalonId(userId, salonId);
        return ResponseEntity.noContent().build();
    }
}
