package com.nwt.systemeventsservice.service;

import com.nwt.systemeventsservice.model.SystemEvent;
import com.nwt.systemeventsservice.repository.SystemEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SystemEventServiceImpl implements SystemEventService {

    private final SystemEventRepository systemEventRepository;

    @Override
    public SystemEvent save(SystemEvent event) {
        log.debug("Saving system event: microservice={}, action={}", event.getMicroservice(), event.getActionType());
        return systemEventRepository.save(event);
    }

    @Override
    public Optional<SystemEvent> findById(String id) {
        return systemEventRepository.findById(id);
    }

    @Override
    public Page<SystemEvent> findByFilters(String microservice, String userId, String actionType, Pageable pageable) {
        boolean hasMicroservice = microservice != null && !microservice.isBlank();
        boolean hasUserId = userId != null && !userId.isBlank();
        boolean hasActionType = actionType != null && !actionType.isBlank();

        if (hasMicroservice && hasUserId && hasActionType) {
            return systemEventRepository.findByMicroserviceAndUserIdAndActionType(microservice, userId, actionType, pageable);
        } else if (hasMicroservice && hasUserId) {
            return systemEventRepository.findByMicroserviceAndUserId(microservice, userId, pageable);
        } else if (hasMicroservice && hasActionType) {
            return systemEventRepository.findByMicroserviceAndActionType(microservice, actionType, pageable);
        } else if (hasUserId && hasActionType) {
            return systemEventRepository.findByUserIdAndActionType(userId, actionType, pageable);
        } else if (hasMicroservice) {
            return systemEventRepository.findByMicroservice(microservice, pageable);
        } else if (hasUserId) {
            return systemEventRepository.findByUserId(userId, pageable);
        } else if (hasActionType) {
            return systemEventRepository.findByActionType(actionType, pageable);
        } else {
            return systemEventRepository.findAll(pageable);
        }
    }

    @Override
    public void deleteById(String id) {
        log.debug("Deleting system event with id={}", id);
        systemEventRepository.deleteById(id);
    }
}
