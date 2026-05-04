package ba.unsa.etf.nwt.userservice;

import ba.unsa.etf.nwt.userservice.model.User;
import ba.unsa.etf.nwt.userservice.model.UserRole;
import ba.unsa.etf.nwt.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        log.info("=== Učitavanje početnih podataka za User Service ===");

        // Svi test korisnici imaju lozinku: password123
        String pw = passwordEncoder.encode("password123");

        List<User> users = List.of(
                User.builder()
                        .email("admin@brico.ba")
                        .password(pw)
                        .fullName("Super Admin")
                        .phone("+38761000001")
                        .role(UserRole.ADMIN)
                        .emailVerified(true)
                        .build(),

                User.builder()
                        .email("vlasnik1@brico.ba")
                        .password(pw)
                        .fullName("Amir Hodžić")
                        .phone("+38761111001")
                        .role(UserRole.SALON_OWNER)
                        .emailVerified(true)
                        .build(),

                User.builder()
                        .email("vlasnik2@brico.ba")
                        .password(pw)
                        .fullName("Selma Kovačević")
                        .phone("+38761111002")
                        .role(UserRole.SALON_OWNER)
                        .emailVerified(true)
                        .build(),

                User.builder()
                        .email("frizer1@brico.ba")
                        .password(pw)
                        .fullName("Lejla Mehić")
                        .phone("+38762222001")
                        .role(UserRole.HAIRDRESSER)
                        .emailVerified(true)
                        .build(),

                User.builder()
                        .email("frizer2@brico.ba")
                        .password(pw)
                        .fullName("Tarik Bašić")
                        .phone("+38762222002")
                        .role(UserRole.HAIRDRESSER)
                        .emailVerified(true)
                        .build(),

                User.builder()
                        .email("frizer3@brico.ba")
                        .password(pw)
                        .fullName("Nina Softić")
                        .phone("+38762222003")
                        .role(UserRole.HAIRDRESSER)
                        .emailVerified(false)
                        .build(),

                User.builder()
                        .email("klijent1@example.com")
                        .password(pw)
                        .fullName("Amina Begić")
                        .phone("+38763333001")
                        .role(UserRole.CLIENT)
                        .emailVerified(true)
                        .build(),

                User.builder()
                        .email("klijent2@example.com")
                        .password(pw)
                        .fullName("Emir Zukić")
                        .phone("+38763333002")
                        .role(UserRole.CLIENT)
                        .emailVerified(true)
                        .build(),

                User.builder()
                        .email("klijent3@example.com")
                        .password(pw)
                        .fullName("Maja Đozić")
                        .phone("+38763333003")
                        .role(UserRole.CLIENT)
                        .emailVerified(false)
                        .build()
        );

        long inserted = users.stream()
                .filter(u -> !userRepository.existsByEmail(u.getEmail()))
                .map(userRepository::save)
                .count();

        log.info("=== Kreirano {} novih korisnika (preskočeno: {}) ===", inserted, users.size() - inserted);
        log.info("Admin nalog: admin@brico.ba");
        log.info("Vlasnici salona: {}", users.stream()
                .filter(u -> u.getRole() == UserRole.SALON_OWNER)
                .map(User::getEmail)
                .toList());
    }
}
