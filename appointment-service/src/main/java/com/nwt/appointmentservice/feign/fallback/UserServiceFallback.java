package com.nwt.appointmentservice.feign.fallback;

import com.nwt.appointmentservice.feign.UserServiceClient;
import com.nwt.appointmentservice.feign.dto.UserDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Slf4j
@Component
public class UserServiceFallback implements UserServiceClient {

    @Override
    public UserDto getUserById(Long id) {
        log.warn("UserService fallback triggered for getUserById({})", id);
        return UserDto.builder()
                .id(id)
                .fullName("Unknown User")
                .build();
    }

    @Override
    public List<UserDto> getUsersByIds(List<Long> ids) {
        log.warn("UserService fallback triggered for getUsersByIds({})", ids);
        return Collections.emptyList();
    }
}
