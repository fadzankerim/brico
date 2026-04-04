package ba.unsa.etf.nwt.userservice;

import ba.unsa.etf.nwt.userservice.model.User;
import ba.unsa.etf.nwt.userservice.model.UserRole;
import ba.unsa.etf.nwt.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;

    @Override
    public void run(String... args) {
        log.info("=== Učitavanje početnih podataka za User Service ===");

        List<User> users = List.of(
                User.builder()
                        .email("admin@brico.ba")
                        .password("$2a$10$adminHashedPassword123") // BCrypt hash
                        .fullName("Super Admin")
                        .phone("+38761000001")
                        .role(UserRole.ADMIN)
                        .emailVerified(true)
                        .build(),

                User.builder()
                        .email("vlasnik1@brico.ba")
                        .password("$2a$10$ownerHashedPassword456")
                        .fullName("Amir Hodžić")
                        .phone("+38761111001")
                        .role(UserRole.SALON_OWNER)
                        .emailVerified(true)
                        .build(),

                User.builder()
                        .email("vlasnik2@brico.ba")
                        .password("$2a$10$ownerHashedPassword789")
                        .fullName("Selma Kovačević")
                        .phone("+38761111002")
                        .role(UserRole.SALON_OWNER)
                        .emailVerified(true)
                        .build(),

                User.builder()
                        .email("frizer1@brico.ba")
                        .password("$2a$10$hairdresserHash001")
                        .fullName("Lejla Mehić")
                        .phone("+38762222001")
                        .role(UserRole.HAIRDRESSER)
                        .emailVerified(true)
                        .build(),

                User.builder()
                        .email("frizer2@brico.ba")
                        .password("$2a$10$hairdresserHash002")
                        .fullName("Tarik Bašić")
                        .phone("+38762222002")
                        .role(UserRole.HAIRDRESSER)
                        .emailVerified(true)
                        .build(),

                User.builder()
                        .email("frizer3@brico.ba")
                        .password("$2a$10$hairdresserHash003")
                        .fullName("Nina Softić")
                        .phone("+38762222003")
                        .role(UserRole.HAIRDRESSER)
                        .emailVerified(false)
                        .build(),

                User.builder()
                        .email("klijent1@example.com")
                        .password("$2a$10$clientHash001")
                        .fullName("Amina Begić")
                        .phone("+38763333001")
                        .role(UserRole.CLIENT)
                        .emailVerified(true)
                        .build(),

                User.builder()
                        .email("klijent2@example.com")
                        .password("$2a$10$clientHash002")
                        .fullName("Emir Zukić")
                        .phone("+38763333002")
                        .role(UserRole.CLIENT)
                        .emailVerified(true)
                        .build(),

                User.builder()
                        .email("klijent3@example.com")
                        .password("$2a$10$clientHash003")
                        .fullName("Maja Đozić")
                        .phone("+38763333003")
                        .role(UserRole.CLIENT)
                        .emailVerified(false)
                        .build()
        );

        userRepository.saveAll(users);

        log.info("=== Kreirano {} korisnika ===", users.size());
        log.info("Admin nalog: admin@brico.ba");
        log.info("Vlasnici salona: {}", users.stream()
                .filter(u -> u.getRole() == UserRole.SALON_OWNER)
                .map(User::getEmail)
                .toList());
    }
}
