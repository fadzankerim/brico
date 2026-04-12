package ba.unsa.etf.nwt.reviewservice.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ReviewResponse {
    private Long id;
    private Long clientId;
    private String clientName;
    private String clientPhoto;
    private Long salonId;
    private Long hairdresserId;
    private String hairdresserName;
    private Long appointmentId;
    private Integer rating;
    private String comment;
    private String ownerReply;
    private LocalDateTime ownerReplyAt;
    private LocalDateTime createdAt;
}
