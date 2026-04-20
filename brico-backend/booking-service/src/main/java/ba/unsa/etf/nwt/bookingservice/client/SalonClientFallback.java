package ba.unsa.etf.nwt.bookingservice.client;

import ba.unsa.etf.nwt.bookingservice.exception.ServiceUnavailableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Fallback za SalonClient — aktivira se kada salon-service nije dostupan.
 * booking-service ostaje aktivan, ali odbija kreiranje termina s jasnom porukom.
 */
@Component
public class SalonClientFallback implements SalonClient {

    private static final Logger log = LoggerFactory.getLogger(SalonClientFallback.class);

    @Override
    public Map<String, Object> validateSalon(Long salonId) {
        log.error("salon-service nije dostupan — fallback aktiviran za salonId={}", salonId);
        throw new ServiceUnavailableException("salon-service trenutno nije dostupan. Pokušajte ponovo.");
    }

    @Override
    public Map<String, Object> validateService(Long salonId, Long serviceId) {
        log.error("salon-service nije dostupan — fallback aktiviran za salonId={}, serviceId={}", salonId, serviceId);
        throw new ServiceUnavailableException("salon-service trenutno nije dostupan. Pokušajte ponovo.");
    }
}
