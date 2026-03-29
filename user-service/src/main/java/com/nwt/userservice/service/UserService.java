package com.nwt.userservice.service;

import com.nwt.userservice.dto.request.UpdateUserRequest;
import com.nwt.userservice.dto.response.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {

    Page<UserResponse> getAll(Pageable pageable);

    UserResponse getById(Long id);

    List<UserResponse> getUsersByIds(List<Long> ids);

    UserResponse update(Long id, UpdateUserRequest request);

    void delete(Long id);
}
