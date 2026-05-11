package ba.unsa.etf.nwt.reviewservice.dto;

import ba.unsa.etf.nwt.reviewservice.model.Favorite;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
public class FavoriteResponse {
    private Long              id;
    private Long              userId;
    private Long              salonId;
    private Map<String,Object> salon;
    private LocalDateTime     createdAt;

    public static FavoriteResponse from(Favorite f) {
        FavoriteResponse r = new FavoriteResponse();
        r.setId(f.getId());
        r.setUserId(f.getUserId());
        r.setSalonId(f.getSalonId());
        r.setCreatedAt(f.getCreatedAt());
        r.setSalon(Map.of(
            "id",        f.getSalonId(),
            "name",      f.getSalonName()     != null ? f.getSalonName()     : "",
            "slug",      f.getSalonSlug()     != null ? f.getSalonSlug()     : "",
            "city",      f.getSalonCity()     != null ? f.getSalonCity()     : "",
            "avgRating", f.getSalonAvgRating()!= null ? f.getSalonAvgRating(): 0.0,
            "verified",  f.getSalonVerified() != null ? f.getSalonVerified() : false
        ));
        return r;
    }
}
