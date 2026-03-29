package com.nwt.userservice.service;

import com.nwt.userservice.dto.request.UpdateUserRequest;
import com.nwt.userservice.dto.response.UserResponse;
import com.nwt.userservice.exception.ResourceNotFoundException;
import com.nwt.userservice.model.User;
import com.nwt.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponse> getAll(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(this::toUserResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
        return toUserResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getUsersByIds(List<Long> ids) {
        return userRepository.findAllByIdIn(ids)
                .stream()
                .map(this::toUserResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserResponse update(Long id, UpdateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));

        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());

        User updatedUser = userRepository.save(user);
        log.info("Updated user with id: {}", updatedUser.getId());

        return toUserResponse(updatedUser);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));

        user.setActive(false);
        userRepository.save(user);
        log.info("Soft-deleted user with id: {}", id);
    }

    private UserResponse toUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .phone(user.getPhone())
                .role(user.getRole())
                .isActive(user.isActive())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
