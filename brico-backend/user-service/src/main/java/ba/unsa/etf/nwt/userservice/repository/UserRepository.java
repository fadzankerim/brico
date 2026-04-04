package ba.unsa.etf.nwt.userservice.repository;

import ba.unsa.etf.nwt.userservice.model.User;
import ba.unsa.etf.nwt.userservice.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    List<User> findByRole(UserRole role);

    List<User> findByEmailVerified(Boolean emailVerified);
}
