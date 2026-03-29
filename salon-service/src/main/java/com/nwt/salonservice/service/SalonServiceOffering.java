package com.nwt.salonservice.service;

import com.nwt.salonservice.dto.request.ServiceRequest;
import com.nwt.salonservice.dto.response.ServiceResponse;

import java.util.List;

public interface SalonServiceOffering {

    List<ServiceResponse> findBySalonId(Long salonId);

    ServiceResponse findById(Long id);

    ServiceResponse create(ServiceRequest request);

    ServiceResponse update(Long id, ServiceRequest request);

    void delete(Long id);

    List<ServiceResponse> findAllByIds(List<Long> ids);
}
