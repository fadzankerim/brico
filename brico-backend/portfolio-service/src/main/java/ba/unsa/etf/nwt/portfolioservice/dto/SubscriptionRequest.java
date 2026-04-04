package ba.unsa.etf.nwt.portfolioservice.dto;

import ba.unsa.etf.nwt.portfolioservice.model.PlanType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SubscriptionRequest {

    @NotNull(message = "Salon ID je obavezan")
    private Long salonId;

    @NotNull(message = "Tip plana je obavezan")
    private PlanType planType;

    private String stripeSubscriptionId;
    private String stripeCustomerId;
}
