package ba.unsa.etf.nwt.salonservice.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class HairdresserResponse {
    private Long id;
    private Long userId;
    private Long salonId;
    private String fullName;
    private String bio;
    private String specialties;
    private String profilePhoto;
    private Boolean isActive;
    private LocalDateTime createdAt;
}
