package ba.unsa.etf.nwt.bookingservice;

import ba.unsa.etf.nwt.bookingservice.model.*;
import ba.unsa.etf.nwt.bookingservice.repository.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// ═══════════════════════════════════════════════════════════════════════
//  Referentni ID-ovi (konzistentni sa ostalim servisima):
//
//  Klijenti:
//   17=Amina Begić  18=Emir Zukić  36=Marko Petrović  37=Ivana Horvat  38=Dino Bajrić
//
//  Frizeri (hairdresserId u salon-service):
//   1=Lejla(EC)  2=Tarik(EC)  3=Edin(UB)  4=Amina Z.(UB)
//   5=Nina(GS)   6=Sara(GS)   7=Haris(BK) 8=Denis(BK)
//   9=Jovana(SL) 10=Aleksandar(SL)
//  11=Naida(EC)  12=Muhamed(UB)  13=Lejla Č.(GS)  14=Nermin(BK)  15=Tijana(SL)
//  16=Viktor(TGC)  17=Dario(TGC)  18=Bojan(TGC)
//  19=Belma(LBL)   20=Dina(LBL)   21=Amra(LBL)
//  22=Stefan(BS)   23=Lana(BS)    24=Niko(BS)
//
//  Saloni:
//   1=Elite Cut(EC)  2=Urban Barber(UB)  3=Glam Studio(GS)
//   4=Barber Kingdom(BK)  5=Style Lab(SL)
//   6=The Gentlemen's Club(TGC)  7=Luxe Beauty Lounge(LBL)  8=Brico Studio(BS)
//
//  Usluge (originalne IDs 1–21, extra IDs 22–50, novi saloni IDs 51–80):
//  EC orig: 1=Šišanje(15)  2=Pranje+Fen(10)  3=Balayage(80)  4=Keratin(120)  5=Brijanje(18)
//  UB orig: 6=Muško šiš.(12)  7=Brijanje(15)  8=Fade(18)  9=Dječije(10)
//  GS orig: 10=Bojenje(50)  11=Pramenovi(70)  12=Šiš+fen(20)  13=Braz.ker.(100)
//  BK orig: 14=Klasično(14)  15=Moderna(16)   16=Brij+maska(22)  17=Komplet(35)
//  SL orig: 18=Kreativno boj.(60)  19=Šišanje SL(15)  20=Kompl.boja(90)  21=Fen styling(25)
//  EC extra: 22=Bojenje kose EC(55)  23=Pramenovi folijom EC(65)
//  UB extra: 27=Skin fade(20)
//  GS extra: 33=Rekonstrukcija kose(60)  35=Ombre bojenje(80)
//  SL extra: 50=Balayage SL(70)
//  TGC: 51=Klasično muško(20)  52=Royal shave(45)  53=Skin fade+konture(25)
//       55=Hot towel TGC(30)   56=VIP komplet(80)  58=Šišanje s maskom(35)
//  LBL: 63=Balayage premium(120)  64=Keratin luxury(130)  66=Pramenovi folijom LBL(90)
//       67=Svečana LBL(60)  69=Tretman Olaplex(70)  70=Duboka hidratacija(50)
//  BS:  71=Unisex šišanje(18)  72=Bojenje kose BS(55)  74=Muško+brada(25)
//       75=Ombre efekt(75)
// ═══════════════════════════════════════════════════════════════════════

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
        //  ORIGINALNI TERMINI (a1–a25) — nepromijenjeni
        // ═══════════════════════════════════════════════════════════

        // a1 | -90d | Amina → Lejla @ Elite Cut | Šišanje 15 KM
        save(Appointment.builder()
                .clientId(17L).clientName("Amina Begić").clientPhone("+38763100001")
                .hairdresserId(1L).hairdresserName("Lejla Mehić")
                .salonId(1L).salonName("Elite Cut").salonAddress("Titova 1, Sarajevo")
                .startTime(now.minusDays(90).withHour(10).withMinute(0).withSecond(0).withNano(0))
                .endTime(  now.minusDays(90).withHour(10).withMinute(30).withSecond(0).withNano(0))
                .status(AppointmentStatus.COMPLETED)
                .totalPrice(bd("15.00")),
                item(1L, "Šišanje", "15.00", 30));

        // a2 | -87d | Emir → Tarik @ Elite Cut | Šišanje+Brijanje 33 KM
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

        // a3 | -82d | Amina → Nina @ Glam Studio | Bojenje 50 KM
        save(Appointment.builder()
                .clientId(17L).clientName("Amina Begić").clientPhone("+38763100001")
                .hairdresserId(5L).hairdresserName("Nina Softić")
                .salonId(3L).salonName("Glam Studio").salonAddress("Ferhadija 22, Sarajevo")
                .startTime(now.minusDays(82).withHour(10).withMinute(0).withSecond(0).withNano(0))
                .endTime(  now.minusDays(82).withHour(11).withMinute(30).withSecond(0).withNano(0))
                .status(AppointmentStatus.COMPLETED)
                .totalPrice(bd("50.00")),
                item(10L, "Bojenje", "50.00", 90));

        // a4 | -79d | Emir → Haris @ Barber Kingdom | Komplet 35 KM
        save(Appointment.builder()
                .clientId(18L).clientName("Emir Zukić").clientPhone("+38763100002")
                .hairdresserId(7L).hairdresserName("Haris Muratović")
                .salonId(4L).salonName("Barber Kingdom").salonAddress("Armijska bb, Tuzla")
                .startTime(now.minusDays(79).withHour(11).withMinute(0).withSecond(0).withNano(0))
                .endTime(  now.minusDays(79).withHour(12).withMinute(15).withSecond(0).withNano(0))
                .status(AppointmentStatus.COMPLETED)
                .totalPrice(bd("35.00")),
                item(17L, "Komplet usluga", "35.00", 75));

        // a5 | -70d | Emir → Denis @ Barber Kingdom | Moderna 16 KM
        save(Appointment.builder()
                .clientId(18L).clientName("Emir Zukić").clientPhone("+38763100002")
                .hairdresserId(8L).hairdresserName("Denis Avdić")
                .salonId(4L).salonName("Barber Kingdom").salonAddress("Armijska bb, Tuzla")
                .startTime(now.minusDays(70).withHour(15).withMinute(0).withSecond(0).withNano(0))
                .endTime(  now.minusDays(70).withHour(15).withMinute(35).withSecond(0).withNano(0))
                .status(AppointmentStatus.COMPLETED)
                .totalPrice(bd("16.00")),
                item(15L, "Moderna frizura", "16.00", 35));

        // a6 | -63d | Amina → Lejla @ Elite Cut | Balayage 80 KM
        save(Appointment.builder()
                .clientId(17L).clientName("Amina Begić").clientPhone("+38763100001")
                .hairdresserId(1L).hairdresserName("Lejla Mehić")
                .salonId(1L).salonName("Elite Cut").salonAddress("Titova 1, Sarajevo")
                .startTime(now.minusDays(63).withHour(10).withMinute(0).withSecond(0).withNano(0))
                .endTime(  now.minusDays(63).withHour(12).withMinute(0).withSecond(0).withNano(0))
                .status(AppointmentStatus.COMPLETED)
                .totalPrice(bd("80.00")),
                item(3L, "Balayage", "80.00", 120));

        // a7 | -58d | Emir → Edin @ Urban Barber | Muško+Fade 30 KM
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

        // a8 | -50d | Amina → Sara @ Glam Studio | Pramenovi 70 KM
        save(Appointment.builder()
                .clientId(17L).clientName("Amina Begić").clientPhone("+38763100001")
                .hairdresserId(6L).hairdresserName("Sara Begić")
                .salonId(3L).salonName("Glam Studio").salonAddress("Ferhadija 22, Sarajevo")
                .startTime(now.minusDays(50).withHour(14).withMinute(0).withSecond(0).withNano(0))
                .endTime(  now.minusDays(50).withHour(16).withMinute(0).withSecond(0).withNano(0))
                .status(AppointmentStatus.COMPLETED)
                .totalPrice(bd("70.00")),
                item(11L, "Pramenovi", "70.00", 120));

        // a9 | -47d | Emir → Haris @ Barber Kingdom | Komplet 35 KM
        save(Appointment.builder()
                .clientId(18L).clientName("Emir Zukić").clientPhone("+38763100002")
                .hairdresserId(7L).hairdresserName("Haris Muratović")
                .salonId(4L).salonName("Barber Kingdom").salonAddress("Armijska bb, Tuzla")
                .startTime(now.minusDays(47).withHour(10).withMinute(0).withSecond(0).withNano(0))
                .endTime(  now.minusDays(47).withHour(11).withMinute(15).withSecond(0).withNano(0))
                .status(AppointmentStatus.COMPLETED)
                .totalPrice(bd("35.00")),
                item(17L, "Komplet usluga", "35.00", 75));

        // a10 | -42d | Amina → Jovana @ Style Lab | Kreativno bojenje 60 KM
        save(Appointment.builder()
                .clientId(17L).clientName("Amina Begić").clientPhone("+38763100001")
                .hairdresserId(9L).hairdresserName("Jovana Nikolić")
                .salonId(5L).salonName("Style Lab").salonAddress("Krajiška 5, Banja Luka")
                .startTime(now.minusDays(42).withHour(11).withMinute(0).withSecond(0).withNano(0))
                .endTime(  now.minusDays(42).withHour(13).withMinute(0).withSecond(0).withNano(0))
                .status(AppointmentStatus.COMPLETED)
                .totalPrice(bd("60.00")),
                item(18L, "Kreativno bojenje", "60.00", 120));

        // a11 | -38d | Emir → Tarik @ Elite Cut | Šišanje+Brijanje 33 KM
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

        // a12 | -31d | Amina → Lejla @ Elite Cut | Keratin tretman 120 KM
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

        // a13 | -27d | Emir → Denis @ Barber Kingdom | Brijanje+maska 22 KM
        save(Appointment.builder()
                .clientId(18L).clientName("Emir Zukić").clientPhone("+38763100002")
                .hairdresserId(8L).hairdresserName("Denis Avdić")
                .salonId(4L).salonName("Barber Kingdom").salonAddress("Armijska bb, Tuzla")
                .startTime(now.minusDays(27).withHour(16).withMinute(0).withSecond(0).withNano(0))
                .endTime(  now.minusDays(27).withHour(16).withMinute(45).withSecond(0).withNano(0))
                .status(AppointmentStatus.COMPLETED)
                .totalPrice(bd("22.00")),
                item(16L, "Brijanje + maska", "22.00", 45));

        // a14 | -22d | Amina → Nina @ Glam Studio | Šišanje+fen+Bojenje 70 KM
        save(Appointment.builder()
                .clientId(17L).clientName("Amina Begić").clientPhone("+38763100001")
                .hairdresserId(5L).hairdresserName("Nina Softić")
                .salonId(3L).salonName("Glam Studio").salonAddress("Ferhadija 22, Sarajevo")
                .startTime(now.minusDays(22).withHour(11).withMinute(0).withSecond(0).withNano(0))
                .endTime(  now.minusDays(22).withHour(13).withMinute(15).withSecond(0).withNano(0))
                .status(AppointmentStatus.COMPLETED)
                .totalPrice(bd("70.00")),
                item(12L, "Šišanje + fen", "20.00", 45),
                item(10L, "Bojenje",        "50.00", 90));

        // a15 | -18d | Emir → Edin @ Urban Barber | Muško šišanje 12 KM
        save(Appointment.builder()
                .clientId(18L).clientName("Emir Zukić").clientPhone("+38763100002")
                .hairdresserId(3L).hairdresserName("Edin Kovač")
                .salonId(2L).salonName("Urban Barber").salonAddress("Bulevar 12, Mostar")
                .startTime(now.minusDays(18).withHour(14).withMinute(0).withSecond(0).withNano(0))
                .endTime(  now.minusDays(18).withHour(14).withMinute(30).withSecond(0).withNano(0))
                .status(AppointmentStatus.COMPLETED)
                .totalPrice(bd("12.00")),
                item(6L, "Muško šišanje", "12.00", 30));

        // a16 | -14d | Amina → Aleksandar @ Style Lab | Kompleksna boja 90 KM
        save(Appointment.builder()
                .clientId(17L).clientName("Amina Begić").clientPhone("+38763100001")
                .hairdresserId(10L).hairdresserName("Aleksandar Perić")
                .salonId(5L).salonName("Style Lab").salonAddress("Krajiška 5, Banja Luka")
                .startTime(now.minusDays(14).withHour(10).withMinute(0).withSecond(0).withNano(0))
                .endTime(  now.minusDays(14).withHour(12).withMinute(30).withSecond(0).withNano(0))
                .status(AppointmentStatus.COMPLETED)
                .totalPrice(bd("90.00")),
                item(20L, "Kompleksna boja", "90.00", 150));

        // a17 | -11d | Emir → Haris @ Barber Kingdom | Komplet 35 KM
        save(Appointment.builder()
                .clientId(18L).clientName("Emir Zukić").clientPhone("+38763100002")
                .hairdresserId(7L).hairdresserName("Haris Muratović")
                .salonId(4L).salonName("Barber Kingdom").salonAddress("Armijska bb, Tuzla")
                .startTime(now.minusDays(11).withHour(11).withMinute(0).withSecond(0).withNano(0))
                .endTime(  now.minusDays(11).withHour(12).withMinute(15).withSecond(0).withNano(0))
                .status(AppointmentStatus.COMPLETED)
                .totalPrice(bd("35.00")),
                item(17L, "Komplet usluga", "35.00", 75));

        // a18 | -7d | Amina → Lejla @ Elite Cut | Balayage+Pranje+Fen 90 KM
        save(Appointment.builder()
                .clientId(17L).clientName("Amina Begić").clientPhone("+38763100001")
                .hairdresserId(1L).hairdresserName("Lejla Mehić")
                .salonId(1L).salonName("Elite Cut").salonAddress("Titova 1, Sarajevo")
                .startTime(now.minusDays(7).withHour(13).withMinute(0).withSecond(0).withNano(0))
                .endTime(  now.minusDays(7).withHour(15).withMinute(20).withSecond(0).withNano(0))
                .status(AppointmentStatus.COMPLETED)
                .totalPrice(bd("90.00")),
                item(3L, "Balayage",     "80.00", 120),
                item(2L, "Pranje + Fen", "10.00",  20));

        // a19 | -5d | Emir → Amina Z. @ Urban Barber | Muško+Brijanje 27 KM
        save(Appointment.builder()
                .clientId(18L).clientName("Emir Zukić").clientPhone("+38763100002")
                .hairdresserId(4L).hairdresserName("Amina Zukić")
                .salonId(2L).salonName("Urban Barber").salonAddress("Bulevar 12, Mostar")
                .startTime(now.minusDays(5).withHour(15).withMinute(0).withSecond(0).withNano(0))
                .endTime(  now.minusDays(5).withHour(16).withMinute(0).withSecond(0).withNano(0))
                .status(AppointmentStatus.COMPLETED)
                .totalPrice(bd("27.00")),
                item(6L, "Muško šišanje",   "12.00", 30),
                item(7L, "Brijanje britvom", "15.00", 30));

        // a20 | -35d | Emir → Denis @ Barber Kingdom | CANCELLED
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

        // a21 | -16d | Amina → Sara @ Glam Studio | CANCELLED
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

        // a22 | +1d | Amina → Lejla @ Elite Cut | Keratin 120 KM | CONFIRMED
        save(Appointment.builder()
                .clientId(17L).clientName("Amina Begić").clientPhone("+38763100001")
                .hairdresserId(1L).hairdresserName("Lejla Mehić")
                .salonId(1L).salonName("Elite Cut").salonAddress("Titova 1, Sarajevo")
                .startTime(now.plusDays(1).withHour(10).withMinute(0).withSecond(0).withNano(0))
                .endTime(  now.plusDays(1).withHour(12).withMinute(30).withSecond(0).withNano(0))
                .status(AppointmentStatus.CONFIRMED)
                .totalPrice(bd("120.00")),
                item(4L, "Keratin tretman", "120.00", 150));

        // a23 | +2d | Emir → Haris @ Barber Kingdom | Komplet 35 KM | CONFIRMED
        save(Appointment.builder()
                .clientId(18L).clientName("Emir Zukić").clientPhone("+38763100002")
                .hairdresserId(7L).hairdresserName("Haris Muratović")
                .salonId(4L).salonName("Barber Kingdom").salonAddress("Armijska bb, Tuzla")
                .startTime(now.plusDays(2).withHour(11).withMinute(0).withSecond(0).withNano(0))
                .endTime(  now.plusDays(2).withHour(12).withMinute(15).withSecond(0).withNano(0))
                .status(AppointmentStatus.CONFIRMED)
                .totalPrice(bd("35.00")),
                item(17L, "Komplet usluga", "35.00", 75));

        // a24 | +4d | Amina → Nina @ Glam Studio | Pramenovi 70 KM | PENDING
        save(Appointment.builder()
                .clientId(17L).clientName("Amina Begić").clientPhone("+38763100001")
                .hairdresserId(5L).hairdresserName("Nina Softić")
                .salonId(3L).salonName("Glam Studio").salonAddress("Ferhadija 22, Sarajevo")
                .startTime(now.plusDays(4).withHour(14).withMinute(0).withSecond(0).withNano(0))
                .endTime(  now.plusDays(4).withHour(16).withMinute(0).withSecond(0).withNano(0))
                .status(AppointmentStatus.PENDING)
                .totalPrice(bd("70.00")),
                item(11L, "Pramenovi", "70.00", 120));

        // a25 | +6d | Emir → Tarik @ Elite Cut | Šišanje+Brijanje 33 KM | PENDING
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

        // ═══════════════════════════════════════════════════════════
        //  NOVI TERMINI ZA MARKO PETROVIĆA (clientId=36)
        // ═══════════════════════════════════════════════════════════

        // a26 | -75d | Marko → Haris @ Barber Kingdom | Komplet 35 KM
        save(Appointment.builder()
                .clientId(36L).clientName("Marko Petrović").clientPhone("+38763100003")
                .hairdresserId(7L).hairdresserName("Haris Muratović")
                .salonId(4L).salonName("Barber Kingdom").salonAddress("Armijska bb, Tuzla")
                .startTime(now.minusDays(75).withHour(11).withMinute(0).withSecond(0).withNano(0))
                .endTime(  now.minusDays(75).withHour(12).withMinute(15).withSecond(0).withNano(0))
                .status(AppointmentStatus.COMPLETED)
                .totalPrice(bd("35.00")),
                item(17L, "Komplet usluga", "35.00", 75));

        // a27 | -60d | Marko → Viktor @ The Gentlemen's Club | Klasično muško šišanje 20 KM
        save(Appointment.builder()
                .clientId(36L).clientName("Marko Petrović").clientPhone("+38763100003")
                .hairdresserId(16L).hairdresserName("Viktor Blažević")
                .salonId(6L).salonName("The Gentlemen's Club").salonAddress("Ilićeva 8, Sarajevo")
                .startTime(now.minusDays(60).withHour(10).withMinute(0).withSecond(0).withNano(0))
                .endTime(  now.minusDays(60).withHour(10).withMinute(35).withSecond(0).withNano(0))
                .status(AppointmentStatus.COMPLETED)
                .totalPrice(bd("20.00")),
                item(51L, "Klasično muško šišanje", "20.00", 35));

        // a28 | -45d | Marko → Dario @ The Gentlemen's Club | Royal shave 45 KM
        save(Appointment.builder()
                .clientId(36L).clientName("Marko Petrović").clientPhone("+38763100003")
                .hairdresserId(17L).hairdresserName("Dario Šimić")
                .salonId(6L).salonName("The Gentlemen's Club").salonAddress("Ilićeva 8, Sarajevo")
                .startTime(now.minusDays(45).withHour(14).withMinute(0).withSecond(0).withNano(0))
                .endTime(  now.minusDays(45).withHour(15).withMinute(0).withSecond(0).withNano(0))
                .status(AppointmentStatus.COMPLETED)
                .totalPrice(bd("45.00")),
                item(52L, "Royal shave", "45.00", 60));

        // a29 | -30d | Marko → Aleksandar @ Style Lab | Šišanje 15 KM
        save(Appointment.builder()
                .clientId(36L).clientName("Marko Petrović").clientPhone("+38763100003")
                .hairdresserId(10L).hairdresserName("Aleksandar Perić")
                .salonId(5L).salonName("Style Lab").salonAddress("Krajiška 5, Banja Luka")
                .startTime(now.minusDays(30).withHour(12).withMinute(0).withSecond(0).withNano(0))
                .endTime(  now.minusDays(30).withHour(12).withMinute(30).withSecond(0).withNano(0))
                .status(AppointmentStatus.COMPLETED)
                .totalPrice(bd("15.00")),
                item(19L, "Šišanje", "15.00", 30));

        // a30 | -15d | Marko → Bojan @ The Gentlemen's Club | Skin fade + konture 25 KM
        save(Appointment.builder()
                .clientId(36L).clientName("Marko Petrović").clientPhone("+38763100003")
                .hairdresserId(18L).hairdresserName("Bojan Krsmanović")
                .salonId(6L).salonName("The Gentlemen's Club").salonAddress("Ilićeva 8, Sarajevo")
                .startTime(now.minusDays(15).withHour(15).withMinute(0).withSecond(0).withNano(0))
                .endTime(  now.minusDays(15).withHour(15).withMinute(45).withSecond(0).withNano(0))
                .status(AppointmentStatus.COMPLETED)
                .totalPrice(bd("25.00")),
                item(53L, "Skin fade + konture", "25.00", 45));

        // a31 | +5d | Marko → Viktor @ The Gentlemen's Club | VIP komplet paket 80 KM | CONFIRMED
        save(Appointment.builder()
                .clientId(36L).clientName("Marko Petrović").clientPhone("+38763100003")
                .hairdresserId(16L).hairdresserName("Viktor Blažević")
                .salonId(6L).salonName("The Gentlemen's Club").salonAddress("Ilićeva 8, Sarajevo")
                .startTime(now.plusDays(5).withHour(10).withMinute(0).withSecond(0).withNano(0))
                .endTime(  now.plusDays(5).withHour(12).withMinute(0).withSecond(0).withNano(0))
                .status(AppointmentStatus.CONFIRMED)
                .totalPrice(bd("80.00")),
                item(56L, "VIP komplet paket", "80.00", 120));

        // a32 | +10d | Marko → Muhamed @ Urban Barber | Skin fade 20 KM | PENDING
        save(Appointment.builder()
                .clientId(36L).clientName("Marko Petrović").clientPhone("+38763100003")
                .hairdresserId(12L).hairdresserName("Muhamed Husić")
                .salonId(2L).salonName("Urban Barber").salonAddress("Bulevar 12, Mostar")
                .startTime(now.plusDays(10).withHour(11).withMinute(0).withSecond(0).withNano(0))
                .endTime(  now.plusDays(10).withHour(11).withMinute(45).withSecond(0).withNano(0))
                .status(AppointmentStatus.PENDING)
                .totalPrice(bd("20.00")),
                item(27L, "Skin fade", "20.00", 45));

        // ═══════════════════════════════════════════════════════════
        //  NOVI TERMINI ZA IVANU HORVAT (clientId=37)
        // ═══════════════════════════════════════════════════════════

        // a33 | -80d | Ivana → Belma @ Luxe Beauty Lounge | Balayage premium 120 KM
        save(Appointment.builder()
                .clientId(37L).clientName("Ivana Horvat").clientPhone("+38763100004")
                .hairdresserId(19L).hairdresserName("Belma Selimović")
                .salonId(7L).salonName("Luxe Beauty Lounge").salonAddress("Kralja Tomislava 3, Mostar")
                .startTime(now.minusDays(80).withHour(10).withMinute(0).withSecond(0).withNano(0))
                .endTime(  now.minusDays(80).withHour(13).withMinute(0).withSecond(0).withNano(0))
                .status(AppointmentStatus.COMPLETED)
                .totalPrice(bd("120.00")),
                item(63L, "Balayage premium", "120.00", 180));

        // a34 | -65d | Ivana → Nina @ Glam Studio | Pramenovi 70 KM
        save(Appointment.builder()
                .clientId(37L).clientName("Ivana Horvat").clientPhone("+38763100004")
                .hairdresserId(5L).hairdresserName("Nina Softić")
                .salonId(3L).salonName("Glam Studio").salonAddress("Ferhadija 22, Sarajevo")
                .startTime(now.minusDays(65).withHour(11).withMinute(0).withSecond(0).withNano(0))
                .endTime(  now.minusDays(65).withHour(13).withMinute(0).withSecond(0).withNano(0))
                .status(AppointmentStatus.COMPLETED)
                .totalPrice(bd("70.00")),
                item(11L, "Pramenovi", "70.00", 120));

        // a35 | -50d | Ivana → Dina @ Luxe Beauty Lounge | Keratin tretman luxury 130 KM
        save(Appointment.builder()
                .clientId(37L).clientName("Ivana Horvat").clientPhone("+38763100004")
                .hairdresserId(20L).hairdresserName("Dina Terzić")
                .salonId(7L).salonName("Luxe Beauty Lounge").salonAddress("Kralja Tomislava 3, Mostar")
                .startTime(now.minusDays(50).withHour(10).withMinute(0).withSecond(0).withNano(0))
                .endTime(  now.minusDays(50).withHour(13).withMinute(0).withSecond(0).withNano(0))
                .status(AppointmentStatus.COMPLETED)
                .totalPrice(bd("130.00")),
                item(64L, "Keratin tretman luxury", "130.00", 180));

        // a36 | -35d | Ivana → Lejla Č. @ Glam Studio | Ombre bojenje 80 KM
        save(Appointment.builder()
                .clientId(37L).clientName("Ivana Horvat").clientPhone("+38763100004")
                .hairdresserId(13L).hairdresserName("Lejla Čaušević")
                .salonId(3L).salonName("Glam Studio").salonAddress("Ferhadija 22, Sarajevo")
                .startTime(now.minusDays(35).withHour(11).withMinute(0).withSecond(0).withNano(0))
                .endTime(  now.minusDays(35).withHour(13).withMinute(0).withSecond(0).withNano(0))
                .status(AppointmentStatus.COMPLETED)
                .totalPrice(bd("80.00")),
                item(35L, "Ombre bojenje", "80.00", 120));

        // a37 | -20d | Ivana → Amra @ Luxe Beauty Lounge | Tretman Olaplex 70 KM
        save(Appointment.builder()
                .clientId(37L).clientName("Ivana Horvat").clientPhone("+38763100004")
                .hairdresserId(21L).hairdresserName("Amra Husejnović")
                .salonId(7L).salonName("Luxe Beauty Lounge").salonAddress("Kralja Tomislava 3, Mostar")
                .startTime(now.minusDays(20).withHour(14).withMinute(0).withSecond(0).withNano(0))
                .endTime(  now.minusDays(20).withHour(15).withMinute(30).withSecond(0).withNano(0))
                .status(AppointmentStatus.COMPLETED)
                .totalPrice(bd("70.00")),
                item(69L, "Tretman Olaplex", "70.00", 90));

        // a38 | -8d | Ivana → Belma @ Luxe Beauty Lounge | Svečana frizura 60 KM
        save(Appointment.builder()
                .clientId(37L).clientName("Ivana Horvat").clientPhone("+38763100004")
                .hairdresserId(19L).hairdresserName("Belma Selimović")
                .salonId(7L).salonName("Luxe Beauty Lounge").salonAddress("Kralja Tomislava 3, Mostar")
                .startTime(now.minusDays(8).withHour(12).withMinute(0).withSecond(0).withNano(0))
                .endTime(  now.minusDays(8).withHour(13).withMinute(30).withSecond(0).withNano(0))
                .status(AppointmentStatus.COMPLETED)
                .totalPrice(bd("60.00")),
                item(67L, "Svečana frizura", "60.00", 90));

        // a39 | +3d | Ivana → Dina @ Luxe Beauty Lounge | Pramenovi folijom 90 KM | CONFIRMED
        save(Appointment.builder()
                .clientId(37L).clientName("Ivana Horvat").clientPhone("+38763100004")
                .hairdresserId(20L).hairdresserName("Dina Terzić")
                .salonId(7L).salonName("Luxe Beauty Lounge").salonAddress("Kralja Tomislava 3, Mostar")
                .startTime(now.plusDays(3).withHour(10).withMinute(0).withSecond(0).withNano(0))
                .endTime(  now.plusDays(3).withHour(12).withMinute(30).withSecond(0).withNano(0))
                .status(AppointmentStatus.CONFIRMED)
                .totalPrice(bd("90.00")),
                item(66L, "Pramenovi folijom", "90.00", 150));

        // a40 | +7d | Ivana → Lejla Č. @ Glam Studio | Rekonstrukcija kose 60 KM | PENDING
        save(Appointment.builder()
                .clientId(37L).clientName("Ivana Horvat").clientPhone("+38763100004")
                .hairdresserId(13L).hairdresserName("Lejla Čaušević")
                .salonId(3L).salonName("Glam Studio").salonAddress("Ferhadija 22, Sarajevo")
                .startTime(now.plusDays(7).withHour(11).withMinute(0).withSecond(0).withNano(0))
                .endTime(  now.plusDays(7).withHour(12).withMinute(30).withSecond(0).withNano(0))
                .status(AppointmentStatus.PENDING)
                .totalPrice(bd("60.00")),
                item(33L, "Rekonstrukcija kose", "60.00", 90));

        // ═══════════════════════════════════════════════════════════
        //  NOVI TERMINI ZA DINU BAJRIĆA (clientId=38)
        // ═══════════════════════════════════════════════════════════

        // a41 | -70d | Dino → Stefan @ Brico Studio | Unisex šišanje 18 KM
        save(Appointment.builder()
                .clientId(38L).clientName("Dino Bajrić").clientPhone("+38763100005")
                .hairdresserId(22L).hairdresserName("Stefan Đorđević")
                .salonId(8L).salonName("Brico Studio").salonAddress("Jovana Dučića 15, Banja Luka")
                .startTime(now.minusDays(70).withHour(10).withMinute(0).withSecond(0).withNano(0))
                .endTime(  now.minusDays(70).withHour(10).withMinute(35).withSecond(0).withNano(0))
                .status(AppointmentStatus.COMPLETED)
                .totalPrice(bd("18.00")),
                item(71L, "Unisex šišanje", "18.00", 35));

        // a42 | -55d | Dino → Edin @ Urban Barber | Fade šišanje 18 KM
        save(Appointment.builder()
                .clientId(38L).clientName("Dino Bajrić").clientPhone("+38763100005")
                .hairdresserId(3L).hairdresserName("Edin Kovač")
                .salonId(2L).salonName("Urban Barber").salonAddress("Bulevar 12, Mostar")
                .startTime(now.minusDays(55).withHour(13).withMinute(0).withSecond(0).withNano(0))
                .endTime(  now.minusDays(55).withHour(13).withMinute(45).withSecond(0).withNano(0))
                .status(AppointmentStatus.COMPLETED)
                .totalPrice(bd("18.00")),
                item(8L, "Fade šišanje", "18.00", 45));

        // a43 | -40d | Dino → Lana @ Brico Studio | Bojenje kose 55 KM
        save(Appointment.builder()
                .clientId(38L).clientName("Dino Bajrić").clientPhone("+38763100005")
                .hairdresserId(23L).hairdresserName("Lana Begović")
                .salonId(8L).salonName("Brico Studio").salonAddress("Jovana Dučića 15, Banja Luka")
                .startTime(now.minusDays(40).withHour(11).withMinute(0).withSecond(0).withNano(0))
                .endTime(  now.minusDays(40).withHour(12).withMinute(30).withSecond(0).withNano(0))
                .status(AppointmentStatus.COMPLETED)
                .totalPrice(bd("55.00")),
                item(72L, "Bojenje kose", "55.00", 90));

        // a44 | -25d | Dino → Tijana @ Style Lab | Kreativno bojenje 60 KM
        save(Appointment.builder()
                .clientId(38L).clientName("Dino Bajrić").clientPhone("+38763100005")
                .hairdresserId(15L).hairdresserName("Tijana Marković")
                .salonId(5L).salonName("Style Lab").salonAddress("Krajiška 5, Banja Luka")
                .startTime(now.minusDays(25).withHour(14).withMinute(0).withSecond(0).withNano(0))
                .endTime(  now.minusDays(25).withHour(16).withMinute(0).withSecond(0).withNano(0))
                .status(AppointmentStatus.COMPLETED)
                .totalPrice(bd("60.00")),
                item(18L, "Kreativno bojenje", "60.00", 120));

        // a45 | -10d | Dino → Niko @ Brico Studio | Ombre efekt 75 KM
        save(Appointment.builder()
                .clientId(38L).clientName("Dino Bajrić").clientPhone("+38763100005")
                .hairdresserId(24L).hairdresserName("Niko Perišić")
                .salonId(8L).salonName("Brico Studio").salonAddress("Jovana Dučića 15, Banja Luka")
                .startTime(now.minusDays(10).withHour(11).withMinute(0).withSecond(0).withNano(0))
                .endTime(  now.minusDays(10).withHour(13).withMinute(0).withSecond(0).withNano(0))
                .status(AppointmentStatus.COMPLETED)
                .totalPrice(bd("75.00")),
                item(75L, "Ombre efekt", "75.00", 120));

        // a46 | +4d | Dino → Stefan @ Brico Studio | Muško šišanje + brada 25 KM | CONFIRMED
        save(Appointment.builder()
                .clientId(38L).clientName("Dino Bajrić").clientPhone("+38763100005")
                .hairdresserId(22L).hairdresserName("Stefan Đorđević")
                .salonId(8L).salonName("Brico Studio").salonAddress("Jovana Dučića 15, Banja Luka")
                .startTime(now.plusDays(4).withHour(10).withMinute(0).withSecond(0).withNano(0))
                .endTime(  now.plusDays(4).withHour(10).withMinute(45).withSecond(0).withNano(0))
                .status(AppointmentStatus.CONFIRMED)
                .totalPrice(bd("25.00")),
                item(74L, "Muško šišanje + brada", "25.00", 45));

        // a47 | +8d | Dino → Tijana @ Style Lab | Balayage 70 KM | PENDING
        save(Appointment.builder()
                .clientId(38L).clientName("Dino Bajrić").clientPhone("+38763100005")
                .hairdresserId(15L).hairdresserName("Tijana Marković")
                .salonId(5L).salonName("Style Lab").salonAddress("Krajiška 5, Banja Luka")
                .startTime(now.plusDays(8).withHour(14).withMinute(0).withSecond(0).withNano(0))
                .endTime(  now.plusDays(8).withHour(16).withMinute(0).withSecond(0).withNano(0))
                .status(AppointmentStatus.PENDING)
                .totalPrice(bd("70.00")),
                item(50L, "Balayage", "70.00", 120));

        // ═══════════════════════════════════════════════════════════
        //  BUDUĆI TERMINI ZA POSTOJEĆE HAIRDRESSERE (h3,h4,h6,h8,h9,h10)
        // ═══════════════════════════════════════════════════════════

        // a48 | +3d | Emir → Edin @ Urban Barber | Muško šišanje 12 KM | CONFIRMED
        save(Appointment.builder()
                .clientId(18L).clientName("Emir Zukić").clientPhone("+38763100002")
                .hairdresserId(3L).hairdresserName("Edin Kovač")
                .salonId(2L).salonName("Urban Barber").salonAddress("Bulevar 12, Mostar")
                .startTime(now.plusDays(3).withHour(14).withMinute(0).withSecond(0).withNano(0))
                .endTime(  now.plusDays(3).withHour(14).withMinute(30).withSecond(0).withNano(0))
                .status(AppointmentStatus.CONFIRMED)
                .totalPrice(bd("12.00")),
                item(6L, "Muško šišanje", "12.00", 30));

        // a49 | +5d | Amina → Sara @ Glam Studio | Pramenovi 70 KM | CONFIRMED
        save(Appointment.builder()
                .clientId(17L).clientName("Amina Begić").clientPhone("+38763100001")
                .hairdresserId(6L).hairdresserName("Sara Begić")
                .salonId(3L).salonName("Glam Studio").salonAddress("Ferhadija 22, Sarajevo")
                .startTime(now.plusDays(5).withHour(11).withMinute(0).withSecond(0).withNano(0))
                .endTime(  now.plusDays(5).withHour(13).withMinute(0).withSecond(0).withNano(0))
                .status(AppointmentStatus.CONFIRMED)
                .totalPrice(bd("70.00")),
                item(11L, "Pramenovi", "70.00", 120));

        // a50 | +9d | Emir → Denis @ Barber Kingdom | Brijanje + maska 22 KM | PENDING
        save(Appointment.builder()
                .clientId(18L).clientName("Emir Zukić").clientPhone("+38763100002")
                .hairdresserId(8L).hairdresserName("Denis Avdić")
                .salonId(4L).salonName("Barber Kingdom").salonAddress("Armijska bb, Tuzla")
                .startTime(now.plusDays(9).withHour(16).withMinute(0).withSecond(0).withNano(0))
                .endTime(  now.plusDays(9).withHour(16).withMinute(45).withSecond(0).withNano(0))
                .status(AppointmentStatus.PENDING)
                .totalPrice(bd("22.00")),
                item(16L, "Brijanje + maska", "22.00", 45));

        // a51 | +6d | Amina → Jovana @ Style Lab | Kompleksna boja 90 KM | CONFIRMED
        save(Appointment.builder()
                .clientId(17L).clientName("Amina Begić").clientPhone("+38763100001")
                .hairdresserId(9L).hairdresserName("Jovana Nikolić")
                .salonId(5L).salonName("Style Lab").salonAddress("Krajiška 5, Banja Luka")
                .startTime(now.plusDays(6).withHour(11).withMinute(0).withSecond(0).withNano(0))
                .endTime(  now.plusDays(6).withHour(13).withMinute(30).withSecond(0).withNano(0))
                .status(AppointmentStatus.CONFIRMED)
                .totalPrice(bd("90.00")),
                item(20L, "Kompleksna boja", "90.00", 150));

        // a52 | +11d | Amina → Aleksandar @ Style Lab | Fen styling 25 KM | PENDING
        save(Appointment.builder()
                .clientId(17L).clientName("Amina Begić").clientPhone("+38763100001")
                .hairdresserId(10L).hairdresserName("Aleksandar Perić")
                .salonId(5L).salonName("Style Lab").salonAddress("Krajiška 5, Banja Luka")
                .startTime(now.plusDays(11).withHour(10).withMinute(0).withSecond(0).withNano(0))
                .endTime(  now.plusDays(11).withHour(10).withMinute(30).withSecond(0).withNano(0))
                .status(AppointmentStatus.PENDING)
                .totalPrice(bd("25.00")),
                item(21L, "Fen styling", "25.00", 30));

        // a53 | +8d | Emir → Amina Z. @ Urban Barber | Brijanje britvom 15 KM | CONFIRMED
        save(Appointment.builder()
                .clientId(18L).clientName("Emir Zukić").clientPhone("+38763100002")
                .hairdresserId(4L).hairdresserName("Amina Zukić")
                .salonId(2L).salonName("Urban Barber").salonAddress("Bulevar 12, Mostar")
                .startTime(now.plusDays(8).withHour(15).withMinute(0).withSecond(0).withNano(0))
                .endTime(  now.plusDays(8).withHour(15).withMinute(30).withSecond(0).withNano(0))
                .status(AppointmentStatus.CONFIRMED)
                .totalPrice(bd("15.00")),
                item(7L, "Brijanje britvom", "15.00", 30));

        // ═══════════════════════════════════════════════════════════
        //  TERMINI ZA NOVE (3.) FRIZERE POSTOJEĆIH SALONA (h11–h15)
        // ═══════════════════════════════════════════════════════════

        // a54 | -55d | Amina → Naida @ Elite Cut | Šišanje 15 KM
        save(Appointment.builder()
                .clientId(17L).clientName("Amina Begić").clientPhone("+38763100001")
                .hairdresserId(11L).hairdresserName("Naida Halilović")
                .salonId(1L).salonName("Elite Cut").salonAddress("Titova 1, Sarajevo")
                .startTime(now.minusDays(55).withHour(10).withMinute(0).withSecond(0).withNano(0))
                .endTime(  now.minusDays(55).withHour(10).withMinute(30).withSecond(0).withNano(0))
                .status(AppointmentStatus.COMPLETED)
                .totalPrice(bd("15.00")),
                item(1L, "Šišanje", "15.00", 30));

        // a55 | +12d | Amina → Naida @ Elite Cut | Bojenje kose 55 KM | PENDING
        save(Appointment.builder()
                .clientId(17L).clientName("Amina Begić").clientPhone("+38763100001")
                .hairdresserId(11L).hairdresserName("Naida Halilović")
                .salonId(1L).salonName("Elite Cut").salonAddress("Titova 1, Sarajevo")
                .startTime(now.plusDays(12).withHour(11).withMinute(0).withSecond(0).withNano(0))
                .endTime(  now.plusDays(12).withHour(12).withMinute(30).withSecond(0).withNano(0))
                .status(AppointmentStatus.PENDING)
                .totalPrice(bd("55.00")),
                item(22L, "Bojenje kose", "55.00", 90));

        // a56 | -45d | Emir → Muhamed @ Urban Barber | Muško šišanje 12 KM
        save(Appointment.builder()
                .clientId(18L).clientName("Emir Zukić").clientPhone("+38763100002")
                .hairdresserId(12L).hairdresserName("Muhamed Husić")
                .salonId(2L).salonName("Urban Barber").salonAddress("Bulevar 12, Mostar")
                .startTime(now.minusDays(45).withHour(13).withMinute(0).withSecond(0).withNano(0))
                .endTime(  now.minusDays(45).withHour(13).withMinute(30).withSecond(0).withNano(0))
                .status(AppointmentStatus.COMPLETED)
                .totalPrice(bd("12.00")),
                item(6L, "Muško šišanje", "12.00", 30));

        // a57 | +14d | Emir → Muhamed @ Urban Barber | Fade šišanje 18 KM | PENDING
        save(Appointment.builder()
                .clientId(18L).clientName("Emir Zukić").clientPhone("+38763100002")
                .hairdresserId(12L).hairdresserName("Muhamed Husić")
                .salonId(2L).salonName("Urban Barber").salonAddress("Bulevar 12, Mostar")
                .startTime(now.plusDays(14).withHour(14).withMinute(0).withSecond(0).withNano(0))
                .endTime(  now.plusDays(14).withHour(14).withMinute(45).withSecond(0).withNano(0))
                .status(AppointmentStatus.PENDING)
                .totalPrice(bd("18.00")),
                item(8L, "Fade šišanje", "18.00", 45));

        // a58 | -20d | Marko → Nermin @ Barber Kingdom | Klasično šišanje 14 KM
        save(Appointment.builder()
                .clientId(36L).clientName("Marko Petrović").clientPhone("+38763100003")
                .hairdresserId(14L).hairdresserName("Nermin Salihović")
                .salonId(4L).salonName("Barber Kingdom").salonAddress("Armijska bb, Tuzla")
                .startTime(now.minusDays(20).withHour(12).withMinute(0).withSecond(0).withNano(0))
                .endTime(  now.minusDays(20).withHour(12).withMinute(30).withSecond(0).withNano(0))
                .status(AppointmentStatus.COMPLETED)
                .totalPrice(bd("14.00")),
                item(14L, "Klasično šišanje", "14.00", 30));

        // a59 | +7d | Marko → Nermin @ Barber Kingdom | Moderna frizura 16 KM | CONFIRMED
        save(Appointment.builder()
                .clientId(36L).clientName("Marko Petrović").clientPhone("+38763100003")
                .hairdresserId(14L).hairdresserName("Nermin Salihović")
                .salonId(4L).salonName("Barber Kingdom").salonAddress("Armijska bb, Tuzla")
                .startTime(now.plusDays(7).withHour(11).withMinute(0).withSecond(0).withNano(0))
                .endTime(  now.plusDays(7).withHour(11).withMinute(35).withSecond(0).withNano(0))
                .status(AppointmentStatus.CONFIRMED)
                .totalPrice(bd("16.00")),
                item(15L, "Moderna frizura", "16.00", 35));

        // ═══════════════════════════════════════════════════════════
        //  TERMINI ZA FRIZERE NOVIH SALONA (h16–h24)
        // ═══════════════════════════════════════════════════════════

        // a60 | -25d | Marko → Bojan @ TGC | Klasično muško šišanje 20 KM
        save(Appointment.builder()
                .clientId(36L).clientName("Marko Petrović").clientPhone("+38763100003")
                .hairdresserId(18L).hairdresserName("Bojan Krsmanović")
                .salonId(6L).salonName("The Gentlemen's Club").salonAddress("Ilićeva 8, Sarajevo")
                .startTime(now.minusDays(25).withHour(11).withMinute(0).withSecond(0).withNano(0))
                .endTime(  now.minusDays(25).withHour(11).withMinute(35).withSecond(0).withNano(0))
                .status(AppointmentStatus.COMPLETED)
                .totalPrice(bd("20.00")),
                item(51L, "Klasično muško šišanje", "20.00", 35));

        // a61 | +16d | Marko → Bojan @ TGC | Šišanje s maskom 35 KM | PENDING
        save(Appointment.builder()
                .clientId(36L).clientName("Marko Petrović").clientPhone("+38763100003")
                .hairdresserId(18L).hairdresserName("Bojan Krsmanović")
                .salonId(6L).salonName("The Gentlemen's Club").salonAddress("Ilićeva 8, Sarajevo")
                .startTime(now.plusDays(16).withHour(10).withMinute(0).withSecond(0).withNano(0))
                .endTime(  now.plusDays(16).withHour(11).withMinute(0).withSecond(0).withNano(0))
                .status(AppointmentStatus.PENDING)
                .totalPrice(bd("35.00")),
                item(58L, "Šišanje s maskom", "35.00", 60));

        // a62 | +13d | Marko → Dario @ TGC | Hot towel brijanje 30 KM | PENDING
        save(Appointment.builder()
                .clientId(36L).clientName("Marko Petrović").clientPhone("+38763100003")
                .hairdresserId(17L).hairdresserName("Dario Šimić")
                .salonId(6L).salonName("The Gentlemen's Club").salonAddress("Ilićeva 8, Sarajevo")
                .startTime(now.plusDays(13).withHour(14).withMinute(0).withSecond(0).withNano(0))
                .endTime(  now.plusDays(13).withHour(14).withMinute(45).withSecond(0).withNano(0))
                .status(AppointmentStatus.PENDING)
                .totalPrice(bd("30.00")),
                item(55L, "Hot towel brijanje", "30.00", 45));

        // a63 | +15d | Ivana → Amra @ Luxe Beauty Lounge | Duboka hidratacija 50 KM | PENDING
        save(Appointment.builder()
                .clientId(37L).clientName("Ivana Horvat").clientPhone("+38763100004")
                .hairdresserId(21L).hairdresserName("Amra Husejnović")
                .salonId(7L).salonName("Luxe Beauty Lounge").salonAddress("Kralja Tomislava 3, Mostar")
                .startTime(now.plusDays(15).withHour(11).withMinute(0).withSecond(0).withNano(0))
                .endTime(  now.plusDays(15).withHour(12).withMinute(0).withSecond(0).withNano(0))
                .status(AppointmentStatus.PENDING)
                .totalPrice(bd("50.00")),
                item(70L, "Duboka hidratacija kose", "50.00", 60));

        // a64 | +5d | Dino → Lana @ Brico Studio | Unisex šišanje 18 KM | PENDING
        save(Appointment.builder()
                .clientId(38L).clientName("Dino Bajrić").clientPhone("+38763100005")
                .hairdresserId(23L).hairdresserName("Lana Begović")
                .salonId(8L).salonName("Brico Studio").salonAddress("Jovana Dučića 15, Banja Luka")
                .startTime(now.plusDays(5).withHour(11).withMinute(0).withSecond(0).withNano(0))
                .endTime(  now.plusDays(5).withHour(11).withMinute(35).withSecond(0).withNano(0))
                .status(AppointmentStatus.PENDING)
                .totalPrice(bd("18.00")),
                item(71L, "Unisex šišanje", "18.00", 35));

        // a65 | +12d | Dino → Niko @ Brico Studio | Bojenje kose 55 KM | CONFIRMED
        save(Appointment.builder()
                .clientId(38L).clientName("Dino Bajrić").clientPhone("+38763100005")
                .hairdresserId(24L).hairdresserName("Niko Perišić")
                .salonId(8L).salonName("Brico Studio").salonAddress("Jovana Dučića 15, Banja Luka")
                .startTime(now.plusDays(12).withHour(14).withMinute(0).withSecond(0).withNano(0))
                .endTime(  now.plusDays(12).withHour(15).withMinute(30).withSecond(0).withNano(0))
                .status(AppointmentStatus.CONFIRMED)
                .totalPrice(bd("55.00")),
                item(72L, "Bojenje kose", "55.00", 90));

        // ═══════════════════════════════════════════════════════════
        //  OTKAZANI I NO-SHOW TERMINI (realizam podataka)
        // ═══════════════════════════════════════════════════════════

        // a66 | -15d | Dino → Stefan @ Brico Studio | NO_SHOW
        save(Appointment.builder()
                .clientId(38L).clientName("Dino Bajrić").clientPhone("+38763100005")
                .hairdresserId(22L).hairdresserName("Stefan Đorđević")
                .salonId(8L).salonName("Brico Studio").salonAddress("Jovana Dučića 15, Banja Luka")
                .startTime(now.minusDays(15).withHour(9).withMinute(0).withSecond(0).withNano(0))
                .endTime(  now.minusDays(15).withHour(9).withMinute(35).withSecond(0).withNano(0))
                .status(AppointmentStatus.NO_SHOW)
                .totalPrice(bd("18.00")),
                item(71L, "Unisex šišanje", "18.00", 35));

        // a67 | -5d | Marko → Haris @ Barber Kingdom | CANCELLED
        save(Appointment.builder()
                .clientId(36L).clientName("Marko Petrović").clientPhone("+38763100003")
                .hairdresserId(7L).hairdresserName("Haris Muratović")
                .salonId(4L).salonName("Barber Kingdom").salonAddress("Armijska bb, Tuzla")
                .startTime(now.minusDays(5).withHour(14).withMinute(0).withSecond(0).withNano(0))
                .endTime(  now.minusDays(5).withHour(14).withMinute(30).withSecond(0).withNano(0))
                .status(AppointmentStatus.CANCELLED)
                .totalPrice(bd("14.00"))
                .cancelReason("Klijent otkazao — putovanje")
                .cancelledAt(now.minusDays(6).withHour(18).withMinute(0).withSecond(0).withNano(0)),
                item(14L, "Klasično šišanje", "14.00", 30));

        // a68 | -10d | Ivana → Nina @ Glam Studio | CANCELLED
        save(Appointment.builder()
                .clientId(37L).clientName("Ivana Horvat").clientPhone("+38763100004")
                .hairdresserId(5L).hairdresserName("Nina Softić")
                .salonId(3L).salonName("Glam Studio").salonAddress("Ferhadija 22, Sarajevo")
                .startTime(now.minusDays(10).withHour(10).withMinute(0).withSecond(0).withNano(0))
                .endTime(  now.minusDays(10).withHour(11).withMinute(30).withSecond(0).withNano(0))
                .status(AppointmentStatus.CANCELLED)
                .totalPrice(bd("50.00"))
                .cancelReason("Klijentica otkazala — zdravstveni razlozi")
                .cancelledAt(now.minusDays(11).withHour(19).withMinute(0).withSecond(0).withNano(0)),
                item(10L, "Bojenje", "50.00", 90));

        // a69 | -8d | Dino → Edin @ Urban Barber | CANCELLED
        save(Appointment.builder()
                .clientId(38L).clientName("Dino Bajrić").clientPhone("+38763100005")
                .hairdresserId(3L).hairdresserName("Edin Kovač")
                .salonId(2L).salonName("Urban Barber").salonAddress("Bulevar 12, Mostar")
                .startTime(now.minusDays(8).withHour(13).withMinute(0).withSecond(0).withNano(0))
                .endTime(  now.minusDays(8).withHour(13).withMinute(45).withSecond(0).withNano(0))
                .status(AppointmentStatus.CANCELLED)
                .totalPrice(bd("18.00"))
                .cancelReason("Klijent otkazao — promjena planova")
                .cancelledAt(now.minusDays(9).withHour(21).withMinute(0).withSecond(0).withNano(0)),
                item(8L, "Fade šišanje", "18.00", 45));

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
