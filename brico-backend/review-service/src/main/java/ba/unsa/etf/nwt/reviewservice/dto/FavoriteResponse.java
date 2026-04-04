package ba.unsa.etf.nwt.reviewservice.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class FavoriteResponse {
    private Long id;
    private Long userId;
    private Long salonId;
    private LocalDateTime createdAt;
}
