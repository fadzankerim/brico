package ba.unsa.etf.nwt.reviewservice.client;

import ba.unsa.etf.nwt.reviewservice.exception.ServiceUnavailableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Fallback za BookingClient — aktivira se kada booking-service nije dostupan.
 * review-service ostaje aktivan, ali odbija kreiranje recenzije s jasnom porukom.
 */
@Component
public class BookingClientFallback implements BookingClient {

    private static final Logger log = LoggerFactory.getLogger(BookingClientFallback.class);

    @Override
    public Map<String, Object> validateAppointment(Long appointmentId) {
        log.error("booking-service nije dostupan — fallback aktiviran za appointmentId={}", appointmentId);
        throw new ServiceUnavailableException("booking-service trenutno nije dostupan. Pokušajte ponovo.");
    }
}
