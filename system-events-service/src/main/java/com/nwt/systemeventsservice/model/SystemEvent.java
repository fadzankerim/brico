package com.nwt.systemeventsservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "system_events")
public class SystemEvent {

    @Id
    private String id;

    private String microservice;

    private String userId;

    /**
     * Stored as string: CREATE / DELETE / GET / UPDATE
     */
    private String actionType;

    private String resourceName;

    /**
     * Stored as string: SUCCESS / ERROR
     */
    private String responseType;

    private Instant timestamp;

    private String detail;
}
