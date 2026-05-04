package ba.unsa.etf.nwt.reviewservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FavoriteRequest {

    @NotNull(message = "User ID je obavezan")
    private Long userId;

    @NotNull(message = "Salon ID je obavezan")
    private Long salonId;

    private String salonName;
    private String salonSlug;
    private String salonCity;
    private Double salonAvgRating;
    private Boolean salonVerified;
}
