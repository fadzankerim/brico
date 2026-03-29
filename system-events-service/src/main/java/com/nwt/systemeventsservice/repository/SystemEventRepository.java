package com.nwt.systemeventsservice.repository;

import com.nwt.systemeventsservice.model.SystemEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SystemEventRepository extends MongoRepository<SystemEvent, String> {

    Page<SystemEvent> findByMicroservice(String microservice, Pageable pageable);

    Page<SystemEvent> findByUserId(String userId, Pageable pageable);

    Page<SystemEvent> findByMicroserviceAndUserId(String microservice, String userId, Pageable pageable);

    Page<SystemEvent> findByActionType(String actionType, Pageable pageable);

    Page<SystemEvent> findByMicroserviceAndActionType(String microservice, String actionType, Pageable pageable);

    Page<SystemEvent> findByUserIdAndActionType(String userId, String actionType, Pageable pageable);

    Page<SystemEvent> findByMicroserviceAndUserIdAndActionType(
            String microservice, String userId, String actionType, Pageable pageable);

    @Query(value = "{ $and: [ " +
            "{ $or: [ { 'microservice': { $exists: false } }, { 'microservice': ?0 } ] }, " +
            "{ $or: [ { 'userId': { $exists: false } }, { 'userId': ?1 } ] } " +
            "] }")
    Page<SystemEvent> findByFilters(String microservice, String userId, Pageable pageable);
}
