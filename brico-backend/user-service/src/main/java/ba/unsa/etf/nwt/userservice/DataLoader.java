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
//  7 → frizer1@brico.ba   (Lejla Mehić        – Elite Cut,        h_id=1)
//  8 → frizer2@brico.ba   (Tarik Bašić        – Elite Cut,        h_id=2)
//  9 → frizer3@brico.ba   (Edin Kovač         – Urban Barber,     h_id=3)
// 10 → frizer4@brico.ba   (Amina Zukić        – Urban Barber,     h_id=4)
// 11 → frizer5@brico.ba   (Nina Softić        – Glam Studio,      h_id=5)
// 12 → frizer6@brico.ba   (Sara Begić         – Glam Studio,      h_id=6)
// 13 → frizer7@brico.ba   (Haris Muratović    – Barber Kingdom,   h_id=7)
// 14 → frizer8@brico.ba   (Denis Avdić        – Barber Kingdom,   h_id=8)
// 15 → frizer9@brico.ba   (Jovana Nikolić     – Style Lab,        h_id=9)
// 16 → frizer10@brico.ba  (Aleksandar Perić   – Style Lab,        h_id=10)
// 17 → klijent1@brico.ba  (Amina Begić)
// 18 → klijent2@brico.ba  (Emir Zukić)
// 19 → vlasnik6@brico.ba  (The Gentlemen's Club)
// 20 → vlasnik7@brico.ba  (Luxe Beauty Lounge)
// 21 → vlasnik8@brico.ba  (Brico Studio)
// 22 → frizer11@brico.ba  (Naida Halilović    – Elite Cut,        h_id=11)
// 23 → frizer12@brico.ba  (Muhamed Husić      – Urban Barber,     h_id=12)
// 24 → frizer13@brico.ba  (Lejla Čaušević     – Glam Studio,      h_id=13)
// 25 → frizer14@brico.ba  (Nermin Salihović   – Barber Kingdom,   h_id=14)
// 26 → frizer15@brico.ba  (Tijana Marković    – Style Lab,        h_id=15)
// 27 → frizer16@brico.ba  (Viktor Blažević    – The Gentlemen's Club, h_id=16)
// 28 → frizer17@brico.ba  (Dario Šimić        – The Gentlemen's Club, h_id=17)
// 29 → frizer18@brico.ba  (Bojan Krsmanović   – The Gentlemen's Club, h_id=18)
// 30 → frizer19@brico.ba  (Belma Selimović    – Luxe Beauty Lounge,   h_id=19)
// 31 → frizer20@brico.ba  (Dina Terzić        – Luxe Beauty Lounge,   h_id=20)
// 32 → frizer21@brico.ba  (Amra Husejnović    – Luxe Beauty Lounge,   h_id=21)
// 33 → frizer22@brico.ba  (Stefan Đorđević    – Brico Studio,     h_id=22)
// 34 → frizer23@brico.ba  (Lana Begović       – Brico Studio,     h_id=23)
// 35 → frizer24@brico.ba  (Niko Perišić       – Brico Studio,     h_id=24)
// 36 → klijent3@brico.ba  (Marko Petrović)
// 37 → klijent4@brico.ba  (Ivana Horvat)
// 38 → klijent5@brico.ba  (Dino Bajrić)

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

                // ── Admin ────────────────────────────────────────────────────────
                User.builder()
                        .email("admin@brico.ba")
                        .password(pw)
                        .fullName("Super Admin")
                        .phone("+38761000001")
                        .role(UserRole.ADMIN)
                        .emailVerified(true)
                        .build(),

                // ── Vlasnici salona (originalni, IDs 2–6) ────────────────────────
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

                // ── Frizeri – Elite Cut (salonId=1) ─────────────────────────────
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

                // ── Frizeri – Urban Barber (salonId=2) ──────────────────────────
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

                // ── Frizeri – Glam Studio (salonId=3) ───────────────────────────
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

                // ── Frizeri – Barber Kingdom (salonId=4) ────────────────────────
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

                // ── Frizeri – Style Lab (salonId=5) ─────────────────────────────
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

                // ── Klijenti (originalni, IDs 17–18) ────────────────────────────
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
                        .build(),

                // ── Novi vlasnici salona (IDs 19–21) ────────────────────────────
                User.builder()
                        .email("vlasnik6@brico.ba")
                        .password(pw)
                        .fullName("Džemal Čolić")
                        .phone("+38761100006")
                        .role(UserRole.SALON_OWNER)
                        .emailVerified(true)
                        .build(),

                User.builder()
                        .email("vlasnik7@brico.ba")
                        .password(pw)
                        .fullName("Azra Mahmutović")
                        .phone("+38761100007")
                        .role(UserRole.SALON_OWNER)
                        .emailVerified(true)
                        .build(),

                User.builder()
                        .email("vlasnik8@brico.ba")
                        .password(pw)
                        .fullName("Igor Stanković")
                        .phone("+38761100008")
                        .role(UserRole.SALON_OWNER)
                        .emailVerified(true)
                        .build(),

                // ── 3. frizeri za postojeće salone (IDs 22–26) ──────────────────
                User.builder()
                        .email("frizer11@brico.ba")
                        .password(pw)
                        .fullName("Naida Halilović")
                        .phone("+38762100011")
                        .role(UserRole.HAIRDRESSER)
                        .emailVerified(true)
                        .build(),

                User.builder()
                        .email("frizer12@brico.ba")
                        .password(pw)
                        .fullName("Muhamed Husić")
                        .phone("+38762100012")
                        .role(UserRole.HAIRDRESSER)
                        .emailVerified(true)
                        .build(),

                User.builder()
                        .email("frizer13@brico.ba")
                        .password(pw)
                        .fullName("Lejla Čaušević")
                        .phone("+38762100013")
                        .role(UserRole.HAIRDRESSER)
                        .emailVerified(true)
                        .build(),

                User.builder()
                        .email("frizer14@brico.ba")
                        .password(pw)
                        .fullName("Nermin Salihović")
                        .phone("+38762100014")
                        .role(UserRole.HAIRDRESSER)
                        .emailVerified(true)
                        .build(),

                User.builder()
                        .email("frizer15@brico.ba")
                        .password(pw)
                        .fullName("Tijana Marković")
                        .phone("+38762100015")
                        .role(UserRole.HAIRDRESSER)
                        .emailVerified(true)
                        .build(),

                // ── Frizeri – The Gentlemen's Club (salonId=6, IDs 27–29) ────────
                User.builder()
                        .email("frizer16@brico.ba")
                        .password(pw)
                        .fullName("Viktor Blažević")
                        .phone("+38762100016")
                        .role(UserRole.HAIRDRESSER)
                        .emailVerified(true)
                        .build(),

                User.builder()
                        .email("frizer17@brico.ba")
                        .password(pw)
                        .fullName("Dario Šimić")
                        .phone("+38762100017")
                        .role(UserRole.HAIRDRESSER)
                        .emailVerified(true)
                        .build(),

                User.builder()
                        .email("frizer18@brico.ba")
                        .password(pw)
                        .fullName("Bojan Krsmanović")
                        .phone("+38762100018")
                        .role(UserRole.HAIRDRESSER)
                        .emailVerified(true)
                        .build(),

                // ── Frizeri – Luxe Beauty Lounge (salonId=7, IDs 30–32) ──────────
                User.builder()
                        .email("frizer19@brico.ba")
                        .password(pw)
                        .fullName("Belma Selimović")
                        .phone("+38762100019")
                        .role(UserRole.HAIRDRESSER)
                        .emailVerified(true)
                        .build(),

                User.builder()
                        .email("frizer20@brico.ba")
                        .password(pw)
                        .fullName("Dina Terzić")
                        .phone("+38762100020")
                        .role(UserRole.HAIRDRESSER)
                        .emailVerified(true)
                        .build(),

                User.builder()
                        .email("frizer21@brico.ba")
                        .password(pw)
                        .fullName("Amra Husejnović")
                        .phone("+38762100021")
                        .role(UserRole.HAIRDRESSER)
                        .emailVerified(true)
                        .build(),

                // ── Frizeri – Brico Studio (salonId=8, IDs 33–35) ───────────────
                User.builder()
                        .email("frizer22@brico.ba")
                        .password(pw)
                        .fullName("Stefan Đorđević")
                        .phone("+38762100022")
                        .role(UserRole.HAIRDRESSER)
                        .emailVerified(true)
                        .build(),

                User.builder()
                        .email("frizer23@brico.ba")
                        .password(pw)
                        .fullName("Lana Begović")
                        .phone("+38762100023")
                        .role(UserRole.HAIRDRESSER)
                        .emailVerified(true)
                        .build(),

                User.builder()
                        .email("frizer24@brico.ba")
                        .password(pw)
                        .fullName("Niko Perišić")
                        .phone("+38762100024")
                        .role(UserRole.HAIRDRESSER)
                        .emailVerified(true)
                        .build(),

                // ── Novi klijenti (IDs 36–38) ────────────────────────────────────
                User.builder()
                        .email("klijent3@brico.ba")
                        .password(pw)
                        .fullName("Marko Petrović")
                        .phone("+38763100003")
                        .role(UserRole.CLIENT)
                        .emailVerified(true)
                        .build(),

                User.builder()
                        .email("klijent4@brico.ba")
                        .password(pw)
                        .fullName("Ivana Horvat")
                        .phone("+38763100004")
                        .role(UserRole.CLIENT)
                        .emailVerified(true)
                        .build(),

                User.builder()
                        .email("klijent5@brico.ba")
                        .password(pw)
                        .fullName("Dino Bajrić")
                        .phone("+38763100005")
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
