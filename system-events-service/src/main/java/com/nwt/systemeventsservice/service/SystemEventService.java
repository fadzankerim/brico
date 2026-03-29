package com.nwt.systemeventsservice.service;

import com.nwt.systemeventsservice.model.SystemEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface SystemEventService {

    SystemEvent save(SystemEvent event);

    Optional<SystemEvent> findById(String id);

    Page<SystemEvent> findByFilters(String microservice, String userId, String actionType, Pageable pageable);

    void deleteById(String id);
}
