package ba.unsa.etf.nwt.portfolioservice.dto;

import ba.unsa.etf.nwt.portfolioservice.model.SubscriptionStatus;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class SubscriptionResponse {
    private Long id;
    private Long salonId;
    private PlanResponse plan;
    private SubscriptionStatus status;
    private LocalDate startDate;
    private LocalDate endDate;
    private String stripeSubscriptionId;
    private String stripeCustomerId;
    private LocalDateTime createdAt;
}
