package ba.unsa.etf.nwt.salonservice.messaging;

import ba.unsa.etf.nwt.salonservice.model.BookedSlot;
import ba.unsa.etf.nwt.salonservice.model.Hairdresser;
import ba.unsa.etf.nwt.salonservice.model.Salon;
import ba.unsa.etf.nwt.salonservice.repository.BookedSlotRepository;
import ba.unsa.etf.nwt.salonservice.repository.HairdresserRepository;
import ba.unsa.etf.nwt.salonservice.repository.SalonRepository;
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

    private final BookedSlotRepository  bookedSlotRepository;
    private final HairdresserRepository hairdresserRepository;
    private final SalonRepository       salonRepository;
    private final RabbitTemplate        rabbitTemplate;

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
            log.warn("Konflikt termina za hairdresserId={} — odbijam rezervaciju appointmentId={}",
                    event.getHairdresserId(), event.getAppointmentId());

            rabbitTemplate.convertAndSend(
                    RabbitConfig.EXCHANGE,
                    RabbitConfig.SLOT_REJECTED_KEY,
                    SlotResponseEvent.builder()
                            .appointmentId(event.getAppointmentId())
                            .reason("Frizer je zauzet u traženom terminu")
                            .clientId(event.getClientId())
                            .clientEmail(event.getClientEmail())
                            .clientName(event.getClientName())
                            .salonName(event.getSalonName())
                            .startTime(event.getStartTime() != null ? event.getStartTime().toString() : null)
                            .build());
        } else {
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

            // Dohvati userId frizera i ownerId salona za email notifikacije
            Long hairdresserUserId = null;
            Long salonOwnerId      = null;
            try {
                Hairdresser h = hairdresserRepository.findById(event.getHairdresserId()).orElse(null);
                if (h != null) hairdresserUserId = h.getUserId();

                Salon s = salonRepository.findById(event.getSalonId()).orElse(null);
                if (s != null) salonOwnerId = s.getOwnerId();
            } catch (Exception e) {
                log.warn("Nije moguće dohvatiti hairdresserUserId/salonOwnerId: {}", e.getMessage());
            }

            rabbitTemplate.convertAndSend(
                    RabbitConfig.EXCHANGE,
                    RabbitConfig.SLOT_RESERVED_KEY,
                    SlotResponseEvent.builder()
                            .appointmentId(event.getAppointmentId())
                            .clientId(event.getClientId())
                            .clientEmail(event.getClientEmail())
                            .clientName(event.getClientName())
                            .hairdresserUserId(hairdresserUserId)
                            .hairdresserName(event.getHairdresserName())
                            .salonOwnerId(salonOwnerId)
                            .salonName(event.getSalonName())
                            .salonAddress(event.getSalonAddress())
                            .startTime(event.getStartTime() != null ? event.getStartTime().toString() : null)
                            .totalPrice(event.getTotalPrice())
                            .servicesSummary(event.getServicesSummary())
                            .build());
        }
    }
}
