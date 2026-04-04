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

@Component
@RequiredArgsConstructor
@Slf4j
public class DataLoader implements CommandLineRunner {

    private final AppointmentRepository appointmentRepository;

    @Override
    public void run(String... args) {
        log.info("=== Učitavanje početnih podataka za Booking Service ===");

        // ── Termin 1: Završen ────────────────────────────────────
        Appointment a1 = Appointment.builder()
                .clientId(7L)
                .clientName("Amina Begić")
                .clientPhone("+38763333001")
                .hairdresserId(1L)
                .hairdresserName("Lejla Mehić")
                .salonId(1L)
                .salonName("Frizerski Studio Lejla")
                .salonAddress("Ferhadija 12, Sarajevo")
                .startTime(LocalDateTime.now().minusDays(10).withHour(10).withMinute(0))
                .endTime(LocalDateTime.now().minusDays(10).withHour(11).withMinute(0))
                .status(AppointmentStatus.COMPLETED)
                .totalPrice(new BigDecimal("35.00"))
                .build();

        AppointmentItem item1 = AppointmentItem.builder()
                .serviceId(1L)
                .serviceName("Šišanje i stilizacija")
                .price(new BigDecimal("35.00"))
                .durationMinutes(60)
                .appointment(a1)
                .build();
        a1.getItems().add(item1);
        appointmentRepository.save(a1);

        // ── Termin 2: Završen (višestruke usluge) ────────────────
        Appointment a2 = Appointment.builder()
                .clientId(8L)
                .clientName("Emir Zukić")
                .clientPhone("+38763333002")
                .hairdresserId(2L)
                .hairdresserName("Tarik Bašić")
                .salonId(1L)
                .salonName("Frizerski Studio Lejla")
                .salonAddress("Ferhadija 12, Sarajevo")
                .startTime(LocalDateTime.now().minusDays(5).withHour(14).withMinute(0))
                .endTime(LocalDateTime.now().minusDays(5).withHour(15).withMinute(30))
                .status(AppointmentStatus.COMPLETED)
                .totalPrice(new BigDecimal("115.00"))
                .build();

        AppointmentItem item2a = AppointmentItem.builder()
                .serviceId(1L).serviceName("Šišanje i stilizacija")
                .price(new BigDecimal("35.00")).durationMinutes(60)
                .appointment(a2).build();
        AppointmentItem item2b = AppointmentItem.builder()
                .serviceId(2L).serviceName("Bojenje kose (puna boja)")
                .price(new BigDecimal("80.00")).durationMinutes(120)
                .appointment(a2).build();
        a2.getItems().addAll(List.of(item2a, item2b));
        appointmentRepository.save(a2);

        // ── Termin 3: Potvrđen (danas) ────────────────────────────
        Appointment a3 = Appointment.builder()
                .clientId(7L)
                .clientName("Amina Begić")
                .clientPhone("+38763333001")
                .hairdresserId(1L)
                .hairdresserName("Lejla Mehić")
                .salonId(1L)
                .salonName("Frizerski Studio Lejla")
                .salonAddress("Ferhadija 12, Sarajevo")
                .startTime(LocalDateTime.now().withHour(11).withMinute(0))
                .endTime(LocalDateTime.now().withHour(13).withMinute(0))
                .status(AppointmentStatus.CONFIRMED)
                .totalPrice(new BigDecimal("200.00"))
                .notes("Molim da se koristi Wella boja")
                .build();

        AppointmentItem item3a = AppointmentItem.builder()
                .serviceId(3L).serviceName("Pramenovi")
                .price(new BigDecimal("100.00")).durationMinutes(150)
                .appointment(a3).build();
        AppointmentItem item3b = AppointmentItem.builder()
                .serviceId(4L).serviceName("Tretman keratinom")
                .price(new BigDecimal("120.00")).durationMinutes(180)
                .appointment(a3).build();
        a3.getItems().addAll(List.of(item3a, item3b));
        appointmentRepository.save(a3);

        // ── Termin 4: Na čekanju (sutra) ─────────────────────────
        Appointment a4 = Appointment.builder()
                .clientId(9L)
                .clientName("Maja Đozić")
                .hairdresserId(3L)
                .hairdresserName("Nina Softić")
                .salonId(2L)
                .salonName("Barbershop King")
                .salonAddress("Bulevar 33, Mostar")
                .startTime(LocalDateTime.now().plusDays(1).withHour(9).withMinute(0))
                .endTime(LocalDateTime.now().plusDays(1).withHour(9).withMinute(45))
                .status(AppointmentStatus.PENDING)
                .totalPrice(new BigDecimal("30.00"))
                .build();

        AppointmentItem item4 = AppointmentItem.builder()
                .serviceId(6L).serviceName("Šišanje + brada")
                .price(new BigDecimal("30.00")).durationMinutes(45)
                .appointment(a4).build();
        a4.getItems().add(item4);
        appointmentRepository.save(a4);

        // ── Termin 5: Otkazan ─────────────────────────────────────
        Appointment a5 = Appointment.builder()
                .clientId(8L)
                .clientName("Emir Zukić")
                .hairdresserId(3L)
                .hairdresserName("Nina Softić")
                .salonId(2L)
                .salonName("Barbershop King")
                .salonAddress("Bulevar 33, Mostar")
                .startTime(LocalDateTime.now().minusDays(3).withHour(16).withMinute(0))
                .endTime(LocalDateTime.now().minusDays(3).withHour(16).withMinute(30))
                .status(AppointmentStatus.CANCELLED)
                .totalPrice(new BigDecimal("20.00"))
                .notes("Klijent otkazao")
                .build();

        AppointmentItem item5 = AppointmentItem.builder()
                .serviceId(5L).serviceName("Muško šišanje")
                .price(new BigDecimal("20.00")).durationMinutes(30)
                .appointment(a5).build();
        a5.getItems().add(item5);
        appointmentRepository.save(a5);

        log.info("=== Kreirano {} termina ===", appointmentRepository.count());
    }
}
