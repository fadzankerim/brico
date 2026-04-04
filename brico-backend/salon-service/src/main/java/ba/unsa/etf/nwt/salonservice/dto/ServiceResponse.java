package ba.unsa.etf.nwt.salonservice.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ServiceResponse {
    private Long id;
    private Long salonId;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer durationMinutes;
    private Boolean isActive;
}
