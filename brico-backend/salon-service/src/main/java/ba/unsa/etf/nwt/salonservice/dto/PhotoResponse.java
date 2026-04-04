package ba.unsa.etf.nwt.salonservice.dto;

import lombok.Data;

@Data
public class PhotoResponse {
    private Long id;
    private String url;
    private Boolean isPrimary;
    private Integer displayOrder;
}
