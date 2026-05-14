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

// ID raspored (po redoslijedu ubacivanja):
//  1 → admin@brico.ba
//  2 → vlasnik1@brico.ba  (Elite Cut)
//  3 → vlasnik2@brico.ba  (Urban Barber)
//  4 → vlasnik3@brico.ba  (Glam Studio)
//  5 → vlasnik4@brico.ba  (Barber Kingdom)
//  6 → vlasnik5@brico.ba  (Style Lab)
//  7 → frizer1@brico.ba   (Lejla Mehić   – Elite Cut)
//  8 → frizer2@brico.ba   (Tarik Bašić   – Elite Cut)
//  9 → frizer3@brico.ba   (Edin Kovač    – Urban Barber)
// 10 → frizer4@brico.ba   (Amina Zukić   – Urban Barber)
// 11 → frizer5@brico.ba   (Nina Softić   – Glam Studio)
// 12 → frizer6@brico.ba   (Sara Begić    – Glam Studio)
// 13 → frizer7@brico.ba   (Haris Muratović – Barber Kingdom)
// 14 → frizer8@brico.ba   (Denis Avdić   – Barber Kingdom)
// 15 → frizer9@brico.ba   (Jovana Nikolić – Style Lab)
// 16 → frizer10@brico.ba  (Aleksandar Perić – Style Lab)
// 17 → klijent1@brico.ba  (Amina Begić)
// 18 → klijent2@brico.ba  (Emir Zukić)

@Component
@RequiredArgsConstructor
@Slf4j
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        log.info("=== Učitavanje početnih podataka za User Service ===");

        String pw = passwordEncoder.encode("password123");

        List<User> users = List.of(

                // ── Admin ────────────────────────────────────────────────
                User.builder()
                        .email("admin@brico.ba")
                        .password(pw)
                        .fullName("Super Admin")
                        .phone("+38761000001")
                        .role(UserRole.ADMIN)
                        .emailVerified(true)
                        .build(),

                // ── Vlasnici salona ──────────────────────────────────────
                User.builder()
                        .email("vlasnik1@brico.ba")
                        .password(pw)
                        .fullName("Amir Hodžić")
                        .phone("+38761100001")
                        .role(UserRole.SALON_OWNER)
                        .emailVerified(true)
                        .build(),

                User.builder()
                        .email("vlasnik2@brico.ba")
                        .password(pw)
                        .fullName("Selma Kovačević")
                        .phone("+38761100002")
                        .role(UserRole.SALON_OWNER)
                        .emailVerified(true)
                        .build(),

                User.builder()
                        .email("vlasnik3@brico.ba")
                        .password(pw)
                        .fullName("Kenan Avdić")
                        .phone("+38761100003")
                        .role(UserRole.SALON_OWNER)
                        .emailVerified(true)
                        .build(),

                User.builder()
                        .email("vlasnik4@brico.ba")
                        .password(pw)
                        .fullName("Mirza Hadžimuratović")
                        .phone("+38761100004")
                        .role(UserRole.SALON_OWNER)
                        .emailVerified(true)
                        .build(),

                User.builder()
                        .email("vlasnik5@brico.ba")
                        .password(pw)
                        .fullName("Fatima Bašić")
                        .phone("+38761100005")
                        .role(UserRole.SALON_OWNER)
                        .emailVerified(true)
                        .build(),

                // ── Frizeri – Elite Cut (salonId=1) ─────────────────────
                User.builder()
                        .email("frizer1@brico.ba")
                        .password(pw)
                        .fullName("Lejla Mehić")
                        .phone("+38762100001")
                        .role(UserRole.HAIRDRESSER)
                        .emailVerified(true)
                        .build(),

                User.builder()
                        .email("frizer2@brico.ba")
                        .password(pw)
                        .fullName("Tarik Bašić")
                        .phone("+38762100002")
                        .role(UserRole.HAIRDRESSER)
                        .emailVerified(true)
                        .build(),

                // ── Frizeri – Urban Barber (salonId=2) ──────────────────
                User.builder()
                        .email("frizer3@brico.ba")
                        .password(pw)
                        .fullName("Edin Kovač")
                        .phone("+38762100003")
                        .role(UserRole.HAIRDRESSER)
                        .emailVerified(true)
                        .build(),

                User.builder()
                        .email("frizer4@brico.ba")
                        .password(pw)
                        .fullName("Amina Zukić")
                        .phone("+38762100004")
                        .role(UserRole.HAIRDRESSER)
                        .emailVerified(true)
                        .build(),

                // ── Frizeri – Glam Studio (salonId=3) ───────────────────
                User.builder()
                        .email("frizer5@brico.ba")
                        .password(pw)
                        .fullName("Nina Softić")
                        .phone("+38762100005")
                        .role(UserRole.HAIRDRESSER)
                        .emailVerified(true)
                        .build(),

                User.builder()
                        .email("frizer6@brico.ba")
                        .password(pw)
                        .fullName("Sara Begić")
                        .phone("+38762100006")
                        .role(UserRole.HAIRDRESSER)
                        .emailVerified(true)
                        .build(),

                // ── Frizeri – Barber Kingdom (salonId=4) ────────────────
                User.builder()
                        .email("frizer7@brico.ba")
                        .password(pw)
                        .fullName("Haris Muratović")
                        .phone("+38762100007")
                        .role(UserRole.HAIRDRESSER)
                        .emailVerified(true)
                        .build(),

                User.builder()
                        .email("frizer8@brico.ba")
                        .password(pw)
                        .fullName("Denis Avdić")
                        .phone("+38762100008")
                        .role(UserRole.HAIRDRESSER)
                        .emailVerified(true)
                        .build(),

                // ── Frizeri – Style Lab (salonId=5) ─────────────────────
                User.builder()
                        .email("frizer9@brico.ba")
                        .password(pw)
                        .fullName("Jovana Nikolić")
                        .phone("+38762100009")
                        .role(UserRole.HAIRDRESSER)
                        .emailVerified(true)
                        .build(),

                User.builder()
                        .email("frizer10@brico.ba")
                        .password(pw)
                        .fullName("Aleksandar Perić")
                        .phone("+38762100010")
                        .role(UserRole.HAIRDRESSER)
                        .emailVerified(true)
                        .build(),

                // ── Klijenti ─────────────────────────────────────────────
                User.builder()
                        .email("klijent1@brico.ba")
                        .password(pw)
                        .fullName("Amina Begić")
                        .phone("+38763100001")
                        .role(UserRole.CLIENT)
                        .emailVerified(true)
                        .build(),

                User.builder()
                        .email("klijent2@brico.ba")
                        .password(pw)
                        .fullName("Emir Zukić")
                        .phone("+38763100002")
                        .role(UserRole.CLIENT)
                        .emailVerified(true)
                        .build()
        );

        long inserted = users.stream()
                .filter(u -> !userRepository.existsByEmail(u.getEmail()))
                .map(userRepository::save)
                .count();

        log.info("=== Kreirano {} novih korisnika (preskočeno: {}) ===", inserted, users.size() - inserted);
    }
}
