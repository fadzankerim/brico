package ba.unsa.etf.nwt.bookingservice.dto;

import ba.unsa.etf.nwt.bookingservice.model.AppointmentStatus;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class AppointmentResponse {
    private Long id;
    private Long clientId;
    private String clientName;
    private String clientPhone;
    private Long hairdresserId;
    private String hairdresserName;
    private Long salonId;
    private String salonName;
    private String salonAddress;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private AppointmentStatus status;
    private BigDecimal totalPrice;
    private String notes;
    private List<AppointmentItemResponse> items;
    private LocalDateTime createdAt;
}
