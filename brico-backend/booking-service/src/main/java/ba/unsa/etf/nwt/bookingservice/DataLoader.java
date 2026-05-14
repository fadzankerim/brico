package ba.unsa.etf.nwt.bookingservice;

import ba.unsa.etf.nwt.bookingservice.model.*;
import ba.unsa.etf.nwt.bookingservice.repository.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

// Referentni ID-ovi (moraju biti konzistentni sa ostalim servisima):
//
// Klijenti (user-service):   17=Amina Begić, 18=Emir Zukić
// Frizeri (salon-service):    1=Lejla(EC), 2=Tarik(EC), 3=Edin(UB), 4=Amina Z.(UB),
//                              5=Nina(GS), 6=Sara(GS), 7=Haris(BK), 8=Denis(BK),
//                              9=Jovana(SL), 10=Aleksandar(SL)
// Saloni:  1=Elite Cut, 2=Urban Barber, 3=Glam Studio, 4=Barber Kingdom, 5=Style Lab
//
// Usluge: EC: 1=Šišanje(15), 2=Pranje+Fen(10), 3=Balayage(80), 4=Keratin(120), 5=Brijanje(18)
//         UB: 6=Muško šiš.(12), 7=Brijanje(15), 8=Fade(18), 9=Dječije(10)
//         GS: 10=Bojenje(50), 11=Pramenovi(70), 12=Šiš+fen(20), 13=Braz.keratin(100)
//         BK: 14=Klasično(14), 15=Moderna(16), 16=Brijanje+maska(22), 17=Komplet(35)
//         SL: 18=Kreativno bojenje(60), 19=Šišanje(15), 20=Kompleksna boja(90), 21=Fen styling(25)

@Component
@RequiredArgsConstructor
@Slf4j
public class DataLoader implements CommandLineRunner {

    private final AppointmentRepository appointmentRepository;

    @Override
    public void run(String... args) {
        if (appointmentRepository.count() > 0) {
            log.info("=== Booking Service: podaci već postoje, preskačem seed ===");
            return;
        }
        log.info("=== Seed: booking-service ===");

        LocalDateTime now = LocalDateTime.now();

        // ═══════════════════════════════════════════════════════════
        //  ZAVRŠENI TERMINI (COMPLETED) — proteklih ~3 mjeseca
        // ═══════════════════════════════════════════════════════════

        // a1 | -90 dana | Amina → Lejla @ Elite Cut | Šišanje 15 KM
        save(Appointment.builder()
                .clientId(17L).clientName("Amina Begić").clientPhone("+38763100001")
                .hairdresserId(1L).hairdresserName("Lejla Mehić")
                .salonId(1L).salonName("Elite Cut").salonAddress("Titova 1, Sarajevo")
                .startTime(now.minusDays(90).withHour(10).withMinute(0).withSecond(0).withNano(0))
                .endTime(  now.minusDays(90).withHour(10).withMinute(30).withSecond(0).withNano(0))
                .status(AppointmentStatus.COMPLETED)
                .totalPrice(bd("15.00")),
                item(1L, "Šišanje", "15.00", 30));

        // a2 | -87 dana | Emir → Tarik @ Elite Cut | Šišanje+Brijanje 33 KM
        save(Appointment.builder()
                .clientId(18L).clientName("Emir Zukić").clientPhone("+38763100002")
                .hairdresserId(2L).hairdresserName("Tarik Bašić")
                .salonId(1L).salonName("Elite Cut").salonAddress("Titova 1, Sarajevo")
                .startTime(now.minusDays(87).withHour(14).withMinute(0).withSecond(0).withNano(0))
                .endTime(  now.minusDays(87).withHour(15).withMinute(0).withSecond(0).withNano(0))
                .status(AppointmentStatus.COMPLETED)
                .totalPrice(bd("33.00")),
                item(1L, "Šišanje",          "15.00", 30),
                item(5L, "Brijanje britvom",  "18.00", 30));

        // a3 | -82 dana | Amina → Nina @ Glam Studio | Bojenje 50 KM
        save(Appointment.builder()
                .clientId(17L).clientName("Amina Begić").clientPhone("+38763100001")
                .hairdresserId(5L).hairdresserName("Nina Softić")
                .salonId(3L).salonName("Glam Studio").salonAddress("Ferhadija 22, Sarajevo")
                .startTime(now.minusDays(82).withHour(10).withMinute(0).withSecond(0).withNano(0))
                .endTime(  now.minusDays(82).withHour(11).withMinute(30).withSecond(0).withNano(0))
                .status(AppointmentStatus.COMPLETED)
                .totalPrice(bd("50.00")),
                item(10L, "Bojenje", "50.00", 90));

        // a4 | -79 dana | Emir → Haris @ Barber Kingdom | Komplet 35 KM
        save(Appointment.builder()
                .clientId(18L).clientName("Emir Zukić").clientPhone("+38763100002")
                .hairdresserId(7L).hairdresserName("Haris Muratović")
                .salonId(4L).salonName("Barber Kingdom").salonAddress("Armijska bb, Tuzla")
                .startTime(now.minusDays(79).withHour(11).withMinute(0).withSecond(0).withNano(0))
                .endTime(  now.minusDays(79).withHour(12).withMinute(15).withSecond(0).withNano(0))
                .status(AppointmentStatus.COMPLETED)
                .totalPrice(bd("35.00")),
                item(17L, "Komplet usluga", "35.00", 75));

        // a5 | -70 dana | Emir → Denis @ Barber Kingdom | Moderna 16 KM
        save(Appointment.builder()
                .clientId(18L).clientName("Emir Zukić").clientPhone("+38763100002")
                .hairdresserId(8L).hairdresserName("Denis Avdić")
                .salonId(4L).salonName("Barber Kingdom").salonAddress("Armijska bb, Tuzla")
                .startTime(now.minusDays(70).withHour(15).withMinute(0).withSecond(0).withNano(0))
                .endTime(  now.minusDays(70).withHour(15).withMinute(35).withSecond(0).withNano(0))
                .status(AppointmentStatus.COMPLETED)
                .totalPrice(bd("16.00")),
                item(15L, "Moderna frizura", "16.00", 35));

        // a6 | -63 dana | Amina → Lejla @ Elite Cut | Balayage 80 KM
        save(Appointment.builder()
                .clientId(17L).clientName("Amina Begić").clientPhone("+38763100001")
                .hairdresserId(1L).hairdresserName("Lejla Mehić")
                .salonId(1L).salonName("Elite Cut").salonAddress("Titova 1, Sarajevo")
                .startTime(now.minusDays(63).withHour(10).withMinute(0).withSecond(0).withNano(0))
                .endTime(  now.minusDays(63).withHour(12).withMinute(0).withSecond(0).withNano(0))
                .status(AppointmentStatus.COMPLETED)
                .totalPrice(bd("80.00")),
                item(3L, "Balayage", "80.00", 120));

        // a7 | -58 dana | Emir → Edin @ Urban Barber | Muško šišanje+Fade 30 KM
        save(Appointment.builder()
                .clientId(18L).clientName("Emir Zukić").clientPhone("+38763100002")
                .hairdresserId(3L).hairdresserName("Edin Kovač")
                .salonId(2L).salonName("Urban Barber").salonAddress("Bulevar 12, Mostar")
                .startTime(now.minusDays(58).withHour(11).withMinute(0).withSecond(0).withNano(0))
                .endTime(  now.minusDays(58).withHour(12).withMinute(15).withSecond(0).withNano(0))
                .status(AppointmentStatus.COMPLETED)
                .totalPrice(bd("30.00")),
                item(6L, "Muško šišanje", "12.00", 30),
                item(8L, "Fade šišanje",  "18.00", 45));

        // a8 | -50 dana | Amina → Sara @ Glam Studio | Pramenovi 70 KM
        save(Appointment.builder()
                .clientId(17L).clientName("Amina Begić").clientPhone("+38763100001")
                .hairdresserId(6L).hairdresserName("Sara Begić")
                .salonId(3L).salonName("Glam Studio").salonAddress("Ferhadija 22, Sarajevo")
                .startTime(now.minusDays(50).withHour(14).withMinute(0).withSecond(0).withNano(0))
                .endTime(  now.minusDays(50).withHour(16).withMinute(0).withSecond(0).withNano(0))
                .status(AppointmentStatus.COMPLETED)
                .totalPrice(bd("70.00")),
                item(11L, "Pramenovi", "70.00", 120));

        // a9 | -47 dana | Emir → Haris @ Barber Kingdom | Komplet 35 KM
        save(Appointment.builder()
                .clientId(18L).clientName("Emir Zukić").clientPhone("+38763100002")
                .hairdresserId(7L).hairdresserName("Haris Muratović")
                .salonId(4L).salonName("Barber Kingdom").salonAddress("Armijska bb, Tuzla")
                .startTime(now.minusDays(47).withHour(10).withMinute(0).withSecond(0).withNano(0))
                .endTime(  now.minusDays(47).withHour(11).withMinute(15).withSecond(0).withNano(0))
                .status(AppointmentStatus.COMPLETED)
                .totalPrice(bd("35.00")),
                item(17L, "Komplet usluga", "35.00", 75));

        // a10 | -42 dana | Amina → Jovana @ Style Lab | Kreativno bojenje 60 KM
        save(Appointment.builder()
                .clientId(17L).clientName("Amina Begić").clientPhone("+38763100001")
                .hairdresserId(9L).hairdresserName("Jovana Nikolić")
                .salonId(5L).salonName("Style Lab").salonAddress("Krajiška 5, Banja Luka")
                .startTime(now.minusDays(42).withHour(11).withMinute(0).withSecond(0).withNano(0))
                .endTime(  now.minusDays(42).withHour(13).withMinute(0).withSecond(0).withNano(0))
                .status(AppointmentStatus.COMPLETED)
                .totalPrice(bd("60.00")),
                item(18L, "Kreativno bojenje", "60.00", 120));

        // a11 | -38 dana | Emir → Tarik @ Elite Cut | Šišanje+Brijanje 33 KM
        save(Appointment.builder()
                .clientId(18L).clientName("Emir Zukić").clientPhone("+38763100002")
                .hairdresserId(2L).hairdresserName("Tarik Bašić")
                .salonId(1L).salonName("Elite Cut").salonAddress("Titova 1, Sarajevo")
                .startTime(now.minusDays(38).withHour(14).withMinute(0).withSecond(0).withNano(0))
                .endTime(  now.minusDays(38).withHour(15).withMinute(0).withSecond(0).withNano(0))
                .status(AppointmentStatus.COMPLETED)
                .totalPrice(bd("33.00")),
                item(1L, "Šišanje",          "15.00", 30),
                item(5L, "Brijanje britvom",  "18.00", 30));

        // a12 | -31 dan | Amina → Lejla @ Elite Cut | Keratin tretman 120 KM
        save(Appointment.builder()
                .clientId(17L).clientName("Amina Begić").clientPhone("+38763100001")
                .hairdresserId(1L).hairdresserName("Lejla Mehić")
                .salonId(1L).salonName("Elite Cut").salonAddress("Titova 1, Sarajevo")
                .startTime(now.minusDays(31).withHour(9).withMinute(0).withSecond(0).withNano(0))
                .endTime(  now.minusDays(31).withHour(11).withMinute(30).withSecond(0).withNano(0))
                .status(AppointmentStatus.COMPLETED)
                .notes("Molim da se koristi Wella boja")
                .totalPrice(bd("120.00")),
                item(4L, "Keratin tretman", "120.00", 150));

        // a13 | -27 dana | Emir → Denis @ Barber Kingdom | Brijanje+maska 22 KM
        save(Appointment.builder()
                .clientId(18L).clientName("Emir Zukić").clientPhone("+38763100002")
                .hairdresserId(8L).hairdresserName("Denis Avdić")
                .salonId(4L).salonName("Barber Kingdom").salonAddress("Armijska bb, Tuzla")
                .startTime(now.minusDays(27).withHour(16).withMinute(0).withSecond(0).withNano(0))
                .endTime(  now.minusDays(27).withHour(16).withMinute(45).withSecond(0).withNano(0))
                .status(AppointmentStatus.COMPLETED)
                .totalPrice(bd("22.00")),
                item(16L, "Brijanje + maska", "22.00", 45));

        // a14 | -22 dana | Amina → Nina @ Glam Studio | Šišanje+fen+Bojenje 70 KM
        save(Appointment.builder()
                .clientId(17L).clientName("Amina Begić").clientPhone("+38763100001")
                .hairdresserId(5L).hairdresserName("Nina Softić")
                .salonId(3L).salonName("Glam Studio").salonAddress("Ferhadija 22, Sarajevo")
                .startTime(now.minusDays(22).withHour(11).withMinute(0).withSecond(0).withNano(0))
                .endTime(  now.minusDays(22).withHour(13).withMinute(15).withSecond(0).withNano(0))
                .status(AppointmentStatus.COMPLETED)
                .totalPrice(bd("70.00")),
                item(12L, "Šišanje + fen", "20.00",  45),
                item(10L, "Bojenje",        "50.00",  90));

        // a15 | -18 dana | Emir → Edin @ Urban Barber | Muško šišanje 12 KM
        save(Appointment.builder()
                .clientId(18L).clientName("Emir Zukić").clientPhone("+38763100002")
                .hairdresserId(3L).hairdresserName("Edin Kovač")
                .salonId(2L).salonName("Urban Barber").salonAddress("Bulevar 12, Mostar")
                .startTime(now.minusDays(18).withHour(14).withMinute(0).withSecond(0).withNano(0))
                .endTime(  now.minusDays(18).withHour(14).withMinute(30).withSecond(0).withNano(0))
                .status(AppointmentStatus.COMPLETED)
                .totalPrice(bd("12.00")),
                item(6L, "Muško šišanje", "12.00", 30));

        // a16 | -14 dana | Amina → Aleksandar @ Style Lab | Kompleksna boja 90 KM
        save(Appointment.builder()
                .clientId(17L).clientName("Amina Begić").clientPhone("+38763100001")
                .hairdresserId(10L).hairdresserName("Aleksandar Perić")
                .salonId(5L).salonName("Style Lab").salonAddress("Krajiška 5, Banja Luka")
                .startTime(now.minusDays(14).withHour(10).withMinute(0).withSecond(0).withNano(0))
                .endTime(  now.minusDays(14).withHour(12).withMinute(30).withSecond(0).withNano(0))
                .status(AppointmentStatus.COMPLETED)
                .totalPrice(bd("90.00")),
                item(20L, "Kompleksna boja", "90.00", 150));

        // a17 | -11 dana | Emir → Haris @ Barber Kingdom | Komplet 35 KM
        save(Appointment.builder()
                .clientId(18L).clientName("Emir Zukić").clientPhone("+38763100002")
                .hairdresserId(7L).hairdresserName("Haris Muratović")
                .salonId(4L).salonName("Barber Kingdom").salonAddress("Armijska bb, Tuzla")
                .startTime(now.minusDays(11).withHour(11).withMinute(0).withSecond(0).withNano(0))
                .endTime(  now.minusDays(11).withHour(12).withMinute(15).withSecond(0).withNano(0))
                .status(AppointmentStatus.COMPLETED)
                .totalPrice(bd("35.00")),
                item(17L, "Komplet usluga", "35.00", 75));

        // a18 | -7 dana | Amina → Lejla @ Elite Cut | Balayage+Pranje+Fen 90 KM
        save(Appointment.builder()
                .clientId(17L).clientName("Amina Begić").clientPhone("+38763100001")
                .hairdresserId(1L).hairdresserName("Lejla Mehić")
                .salonId(1L).salonName("Elite Cut").salonAddress("Titova 1, Sarajevo")
                .startTime(now.minusDays(7).withHour(13).withMinute(0).withSecond(0).withNano(0))
                .endTime(  now.minusDays(7).withHour(15).withMinute(20).withSecond(0).withNano(0))
                .status(AppointmentStatus.COMPLETED)
                .totalPrice(bd("90.00")),
                item(3L, "Balayage",      "80.00", 120),
                item(2L, "Pranje + Fen",  "10.00",  20));

        // a19 | -5 dana | Emir → Amina Z. @ Urban Barber | Muško+Brijanje 27 KM
        save(Appointment.builder()
                .clientId(18L).clientName("Emir Zukić").clientPhone("+38763100002")
                .hairdresserId(4L).hairdresserName("Amina Zukić")
                .salonId(2L).salonName("Urban Barber").salonAddress("Bulevar 12, Mostar")
                .startTime(now.minusDays(5).withHour(15).withMinute(0).withSecond(0).withNano(0))
                .endTime(  now.minusDays(5).withHour(16).withMinute(0).withSecond(0).withNano(0))
                .status(AppointmentStatus.COMPLETED)
                .totalPrice(bd("27.00")),
                item(6L, "Muško šišanje",    "12.00", 30),
                item(7L, "Brijanje britvom",  "15.00", 30));

        // ═══════════════════════════════════════════════════════════
        //  OTKAZANI TERMINI (CANCELLED)
        // ═══════════════════════════════════════════════════════════

        // a20 | -35 dana | Emir → Denis @ Barber Kingdom | CANCELLED
        save(Appointment.builder()
                .clientId(18L).clientName("Emir Zukić").clientPhone("+38763100002")
                .hairdresserId(8L).hairdresserName("Denis Avdić")
                .salonId(4L).salonName("Barber Kingdom").salonAddress("Armijska bb, Tuzla")
                .startTime(now.minusDays(35).withHour(14).withMinute(0).withSecond(0).withNano(0))
                .endTime(  now.minusDays(35).withHour(14).withMinute(35).withSecond(0).withNano(0))
                .status(AppointmentStatus.CANCELLED)
                .totalPrice(bd("16.00"))
                .cancelReason("Klijent otkazao — hitna obaveza")
                .cancelledAt(now.minusDays(36).withHour(20).withMinute(0).withSecond(0).withNano(0)),
                item(15L, "Moderna frizura", "16.00", 35));

        // a21 | -16 dana | Amina → Sara @ Glam Studio | CANCELLED
        save(Appointment.builder()
                .clientId(17L).clientName("Amina Begić").clientPhone("+38763100001")
                .hairdresserId(6L).hairdresserName("Sara Begić")
                .salonId(3L).salonName("Glam Studio").salonAddress("Ferhadija 22, Sarajevo")
                .startTime(now.minusDays(16).withHour(10).withMinute(0).withSecond(0).withNano(0))
                .endTime(  now.minusDays(16).withHour(12).withMinute(0).withSecond(0).withNano(0))
                .status(AppointmentStatus.CANCELLED)
                .totalPrice(bd("70.00"))
                .cancelReason("Klijentica otkazala — vanredna situacija")
                .cancelledAt(now.minusDays(17).withHour(18).withMinute(0).withSecond(0).withNano(0)),
                item(11L, "Pramenovi", "70.00", 120));

        // ═══════════════════════════════════════════════════════════
        //  POTVRĐENI TERMINI (CONFIRMED) — uskoro
        // ═══════════════════════════════════════════════════════════

        // a22 | +1 dan | Amina → Lejla @ Elite Cut | Keratin 120 KM
        save(Appointment.builder()
                .clientId(17L).clientName("Amina Begić").clientPhone("+38763100001")
                .hairdresserId(1L).hairdresserName("Lejla Mehić")
                .salonId(1L).salonName("Elite Cut").salonAddress("Titova 1, Sarajevo")
                .startTime(now.plusDays(1).withHour(10).withMinute(0).withSecond(0).withNano(0))
                .endTime(  now.plusDays(1).withHour(12).withMinute(30).withSecond(0).withNano(0))
                .status(AppointmentStatus.CONFIRMED)
                .totalPrice(bd("120.00")),
                item(4L, "Keratin tretman", "120.00", 150));

        // a23 | +2 dana | Emir → Haris @ Barber Kingdom | Komplet 35 KM
        save(Appointment.builder()
                .clientId(18L).clientName("Emir Zukić").clientPhone("+38763100002")
                .hairdresserId(7L).hairdresserName("Haris Muratović")
                .salonId(4L).salonName("Barber Kingdom").salonAddress("Armijska bb, Tuzla")
                .startTime(now.plusDays(2).withHour(11).withMinute(0).withSecond(0).withNano(0))
                .endTime(  now.plusDays(2).withHour(12).withMinute(15).withSecond(0).withNano(0))
                .status(AppointmentStatus.CONFIRMED)
                .totalPrice(bd("35.00")),
                item(17L, "Komplet usluga", "35.00", 75));

        // ═══════════════════════════════════════════════════════════
        //  TERMINI NA ČEKANJU (PENDING) — uskoro
        // ═══════════════════════════════════════════════════════════

        // a24 | +4 dana | Amina → Nina @ Glam Studio | Pramenovi 70 KM
        save(Appointment.builder()
                .clientId(17L).clientName("Amina Begić").clientPhone("+38763100001")
                .hairdresserId(5L).hairdresserName("Nina Softić")
                .salonId(3L).salonName("Glam Studio").salonAddress("Ferhadija 22, Sarajevo")
                .startTime(now.plusDays(4).withHour(14).withMinute(0).withSecond(0).withNano(0))
                .endTime(  now.plusDays(4).withHour(16).withMinute(0).withSecond(0).withNano(0))
                .status(AppointmentStatus.PENDING)
                .totalPrice(bd("70.00")),
                item(11L, "Pramenovi", "70.00", 120));

        // a25 | +6 dana | Emir → Tarik @ Elite Cut | Šišanje+Brijanje 33 KM
        save(Appointment.builder()
                .clientId(18L).clientName("Emir Zukić").clientPhone("+38763100002")
                .hairdresserId(2L).hairdresserName("Tarik Bašić")
                .salonId(1L).salonName("Elite Cut").salonAddress("Titova 1, Sarajevo")
                .startTime(now.plusDays(6).withHour(10).withMinute(0).withSecond(0).withNano(0))
                .endTime(  now.plusDays(6).withHour(11).withMinute(0).withSecond(0).withNano(0))
                .status(AppointmentStatus.PENDING)
                .totalPrice(bd("33.00")),
                item(1L, "Šišanje",          "15.00", 30),
                item(5L, "Brijanje britvom",  "18.00", 30));

        log.info("=== Booking Service seed završen: {} termina ===", appointmentRepository.count());
    }

    @SafeVarargs
    private void save(Appointment.AppointmentBuilder builder, AppointmentItem... items) {
        Appointment appt = builder.build();
        for (AppointmentItem item : items) {
            item.setAppointment(appt);
            appt.getItems().add(item);
        }
        appointmentRepository.save(appt);
    }

    private AppointmentItem item(Long serviceId, String name, String price, int durationMin) {
        return AppointmentItem.builder()
                .serviceId(serviceId)
                .serviceName(name)
                .price(bd(price))
                .durationMinutes(durationMin)
                .build();
    }

    private BigDecimal bd(String val) {
        return new BigDecimal(val);
    }
}
