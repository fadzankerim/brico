package ba.unsa.etf.nwt.userservice.service;

import ba.unsa.etf.nwt.userservice.dto.UpdateUserRequest;
import ba.unsa.etf.nwt.userservice.dto.UserRequest;
import ba.unsa.etf.nwt.userservice.dto.UserResponse;
import ba.unsa.etf.nwt.userservice.exception.DuplicateResourceException;
import ba.unsa.etf.nwt.userservice.exception.ResourceNotFoundException;
import ba.unsa.etf.nwt.userservice.model.User;
import ba.unsa.etf.nwt.userservice.model.UserRole;
import ba.unsa.etf.nwt.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public List<UserResponse> findAll() {
        return userRepository.findAll().stream()
                .map(u -> modelMapper.map(u, UserResponse.class))
                .toList();
    }

    public List<UserResponse> findByRole(UserRole role) {
        return userRepository.findByRole(role).stream()
                .map(u -> modelMapper.map(u, UserResponse.class))
                .toList();
    }

    // ── Paginacija + sortiranje ────────────────────────────────────────

    public Page<UserResponse> findAllPaged(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(u -> modelMapper.map(u, UserResponse.class));
    }

    public Page<UserResponse> findByRolePaged(UserRole role, Pageable pageable) {
        return userRepository.findByRole(role, pageable)
                .map(u -> modelMapper.map(u, UserResponse.class));
    }

    // ── Custom JPQL pretraga ──────────────────────────────────────────

    public Page<UserResponse> search(String q, Pageable pageable) {
        return userRepository.searchByNameOrEmail(q, pageable)
                .map(u -> modelMapper.map(u, UserResponse.class));
    }

    // ── Statistika ────────────────────────────────────────────────────

    public Map<String, Long> getStats() {
        Map<String, Long> stats = new LinkedHashMap<>();
        userRepository.countByRole().forEach(row ->
                stats.put(row[0].toString(), (Long) row[1]));
        return stats;
    }

    public UserResponse findById(Long id) {
        return modelMapper.map(getOrThrow(id), UserResponse.class);
    }

    /**
     * Validacija za inter-service komunikaciju.
     * Vraća mapu sa active:true/false, koristi booking-service Feign klijent.
     * Baca ResourceNotFoundException ako korisnik ne postoji (→ 404).
     */
    public Map<String, Object> validate(Long id) {
        User user = getOrThrow(id);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("id", user.getId());
        result.put("active", user.getIsActive());
        result.put("role", user.getRole().name());
        return result;
    }

    public UserResponse findByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Korisnik sa emailom '" + email + "' nije pronađen"));
        return modelMapper.map(user, UserResponse.class);
    }

    @Transactional
    public UserResponse create(UserRequest req) {
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new DuplicateResourceException("Korisnik sa emailom '" + req.getEmail() + "' već postoji");
        }
        User user = modelMapper.map(req, User.class);
        if (req.getRole() == null) user.setRole(UserRole.CLIENT);
        // NOTE: In production, password would be BCrypt-hashed here
        return modelMapper.map(userRepository.save(user), UserResponse.class);
    }

    @Transactional
    public UserResponse update(Long id, UpdateUserRequest req) {
        User user = getOrThrow(id);
        if (req.getFullName()     != null) user.setFullName(req.getFullName());
        if (req.getPhone()        != null) user.setPhone(req.getPhone());
        if (req.getProfilePhoto() != null) user.setProfilePhoto(req.getProfilePhoto());
        return modelMapper.map(userRepository.save(user), UserResponse.class);
    }

    @Transactional
    public void delete(Long id) {
        getOrThrow(id);
        userRepository.deleteById(id);
    }

    private User getOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Korisnik sa ID=" + id + " nije pronađen"));
    }
}
