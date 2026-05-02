package ba.unsa.etf.nwt.eventsservice.dto;

import ba.unsa.etf.nwt.eventsservice.model.SystemEvent;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SystemEventResponse {
    private String        id;
    private String        serviceName;
    private String        actionType;
    private String        resourceName;
    private String        responseType;
    private LocalDateTime timestamp;
    private String        userId;
    private String        details;

    public static SystemEventResponse from(SystemEvent e) {
        SystemEventResponse r = new SystemEventResponse();
        r.setId(e.getId());
        r.setServiceName(e.getServiceName());
        r.setActionType(e.getActionType().name());
        r.setResourceName(e.getResourceName());
        r.setResponseType(e.getResponseType().name());
        r.setTimestamp(e.getTimestamp());
        r.setUserId(e.getUserId());
        r.setDetails(e.getDetails());
        return r;
    }
}
