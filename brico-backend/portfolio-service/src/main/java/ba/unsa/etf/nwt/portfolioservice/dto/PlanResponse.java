package ba.unsa.etf.nwt.portfolioservice.dto;

import ba.unsa.etf.nwt.portfolioservice.model.PlanType;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PlanResponse {
    private Long id;
    private PlanType planType;
    private String name;
    private String description;
    private BigDecimal priceKm;
    private Integer maxHairdressers;
    private Boolean hasAdvancedAnalytics;
    private Boolean hasFeaturedStatus;
    private Boolean hasPrioritySupport;
    private Boolean isActive;
    private LocalDateTime createdAt;
}
