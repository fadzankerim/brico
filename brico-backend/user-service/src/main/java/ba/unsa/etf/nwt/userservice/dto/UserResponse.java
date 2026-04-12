package ba.unsa.etf.nwt.userservice.dto;

import ba.unsa.etf.nwt.userservice.model.UserRole;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserResponse {
    private Long id;
    private String email;
    private String fullName;
    private String phone;
    private String profilePhoto;
    private UserRole role;
    private Boolean emailVerified;
    private Boolean isActive;
    private LocalDateTime lastLoginAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
