package ba.unsa.etf.nwt.bookingservice.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class AppointmentItemResponse {
    private Long id;
    private Long serviceId;
    private String serviceName;
    private BigDecimal price;
    private Integer durationMinutes;
}
