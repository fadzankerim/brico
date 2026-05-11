package ba.unsa.etf.nwt.salonservice.messaging;

import ba.unsa.etf.nwt.salonservice.model.BookedSlot;
import ba.unsa.etf.nwt.salonservice.model.BookedSlot.SlotStatus;
import ba.unsa.etf.nwt.salonservice.repository.BookedSlotRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class SlotReservationListener {

    private final BookedSlotRepository bookedSlotRepository;
    private final RabbitTemplate       rabbitTemplate;

    @RabbitListener(queues = RabbitConfig.APPOINTMENT_CREATED_QUEUE)
    @Transactional
    public void onAppointmentCreated(AppointmentCreatedEvent event) {
        log.info("Primljen appointment.created za appointmentId={}, hairdresserId={}, {}–{}",
                event.getAppointmentId(), event.getHairdresserId(),
                event.getStartTime(), event.getEndTime());

        boolean conflict = bookedSlotRepository.existsConflict(
                event.getHairdresserId(),
                event.getStartTime(),
                event.getEndTime(),
                BookedSlot.SlotStatus.RESERVED);

        if (conflict) {
            // Frizer nije slobodan — kompenzacijska akcija: odbij rezervaciju
            log.warn("Konflikt termina za hairdresserId={} — odbijam rezervaciju appointmentId={}",
                    event.getHairdresserId(), event.getAppointmentId());

            rabbitTemplate.convertAndSend(
                    RabbitConfig.EXCHANGE,
                    RabbitConfig.SLOT_REJECTED_KEY,
                    new SlotResponseEvent(event.getAppointmentId(),
                            "Frizer je zauzet u traženom terminu",
                            event.getClientId(), null,
                            event.getStartTime() != null ? event.getStartTime().toString() : null));
        } else {
            // Frizer slobodan — spremi slot u bazu (lokalna transakcija 2)
            bookedSlotRepository.save(BookedSlot.builder()
                    .appointmentId(event.getAppointmentId())
                    .hairdresserId(event.getHairdresserId())
                    .salonId(event.getSalonId())
                    .startTime(event.getStartTime())
                    .endTime(event.getEndTime())
                    .status(BookedSlot.SlotStatus.RESERVED)
                    .build());

            log.info("Slot rezervisan za hairdresserId={} appointmentId={}",
                    event.getHairdresserId(), event.getAppointmentId());

            // Obavijesti booking-service da potvrdi termin (finalna akcija)
            rabbitTemplate.convertAndSend(
                    RabbitConfig.EXCHANGE,
                    RabbitConfig.SLOT_RESERVED_KEY,
                    new SlotResponseEvent(event.getAppointmentId(), null,
                            event.getClientId(), null,
                            event.getStartTime() != null ? event.getStartTime().toString() : null));
        }
    }
}
