package com.nwt.userservice;

import com.nwt.userservice.model.User;
import com.nwt.userservice.model.UserRole;
import com.nwt.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@Profile("!test")
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            log.info("Database already seeded. Skipping data seeder.");
            return;
        }

        log.info("Seeding database with initial data...");

        String defaultPassword = passwordEncoder.encode("Password123!");

        // ADMIN
        User admin = User.builder()
                .email("admin@brico.ba")
                .passwordHash(defaultPassword)
                .fullName("Admir Hodžić")
                .phone("+38761100001")
                .role(UserRole.ADMIN)
                .isActive(true)
                .build();

        // SALON_OWNERs
        User owner1 = User.builder()
                .email("emir.basic@brico.ba")
                .passwordHash(defaultPassword)
                .fullName("Emir Bašić")
                .phone("+38761200001")
                .role(UserRole.SALON_OWNER)
                .isActive(true)
                .build();

        User owner2 = User.builder()
                .email("lejla.kovacevic@brico.ba")
                .passwordHash(defaultPassword)
                .fullName("Lejla Kovačević")
                .phone("+38762200002")
                .role(UserRole.SALON_OWNER)
                .isActive(true)
                .build();

        // CLIENTs
        User client1 = User.builder()
                .email("amela.begovic@gmail.com")
                .passwordHash(defaultPassword)
                .fullName("Amela Begović")
                .phone("+38763300001")
                .role(UserRole.CLIENT)
                .isActive(true)
                .build();

        User client2 = User.builder()
                .email("nermin.muratovic@gmail.com")
                .passwordHash(defaultPassword)
                .fullName("Nermin Muratović")
                .phone("+38761300002")
                .role(UserRole.CLIENT)
                .isActive(true)
                .build();

        User client3 = User.builder()
                .email("selma.ibrahimovic@gmail.com")
                .passwordHash(defaultPassword)
                .fullName("Selma Ibrahimović")
                .phone("+38762300003")
                .role(UserRole.CLIENT)
                .isActive(true)
                .build();

        User client4 = User.builder()
                .email("damir.hadzic@gmail.com")
                .passwordHash(defaultPassword)
                .fullName("Damir Hadžić")
                .phone("+38763300004")
                .role(UserRole.CLIENT)
                .isActive(true)
                .build();

        User client5 = User.builder()
                .email("belma.causevic@gmail.com")
                .passwordHash(defaultPassword)
                .fullName("Belma Čaušević")
                .phone("+38761300005")
                .role(UserRole.CLIENT)
                .isActive(true)
                .build();

        userRepository.saveAll(List.of(admin, owner1, owner2, client1, client2, client3, client4, client5));

        log.info("Data seeding complete. Created {} users.", userRepository.count());
    }
}
