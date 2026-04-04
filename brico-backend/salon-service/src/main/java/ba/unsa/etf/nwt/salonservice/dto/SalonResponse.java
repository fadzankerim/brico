package ba.unsa.etf.nwt.salonservice.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class SalonResponse {
    private Long id;
    private String name;
    private String slug;
    private String description;
    private String city;
    private String address;
    private Double latitude;
    private Double longitude;
    private String phone;
    private String website;
    private Boolean verified;
    private Boolean isActive;
    private Long ownerId;
    private List<HairdresserResponse> hairdressers;
    private List<ServiceResponse> services;
    private List<PhotoResponse> photos;
    private LocalDateTime createdAt;
}
