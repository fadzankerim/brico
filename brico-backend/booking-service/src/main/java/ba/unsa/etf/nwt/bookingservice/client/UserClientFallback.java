package ba.unsa.etf.nwt.bookingservice.client;

import ba.unsa.etf.nwt.bookingservice.exception.ServiceUnavailableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Fallback za UserClient — aktivira se kada user-service nije dostupan.
 * Umjesto pada sistema, baca ServiceUnavailableException s jasnom porukom.
 * Na ovaj način booking-service ostaje aktivan čak i kada user-service pada.
 */
@Component
public class UserClientFallback implements UserClient {

    private static final Logger log = LoggerFactory.getLogger(UserClientFallback.class);

    @Override
    public Map<String, Object> validateUser(Long userId) {
        log.error("user-service nije dostupan — fallback aktiviran za userId={}", userId);
        throw new ServiceUnavailableException("user-service trenutno nije dostupan. Pokušajte ponovo.");
    }
}
