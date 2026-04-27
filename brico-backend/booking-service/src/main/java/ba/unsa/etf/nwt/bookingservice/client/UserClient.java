package ba.unsa.etf.nwt.bookingservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

/**
 * Feign klijent za komunikaciju sa user-service.
 * Ime "user-service" se resolvuje putem Eureka service discovery — bez hardkodiranja IP/porta.
 * Fallback klasa se aktivira kada user-service nije dostupan (CircuitBreaker scenarij).
 */
@FeignClient(name = "user-service", fallback = UserClientFallback.class)
public interface UserClient {

    /**
     * Validacija korisnika — provjerava da li korisnik postoji i aktivan je.
     * Vraća: { id, active, role }
     * Baca 404 ako korisnik ne postoji.
     */
    @GetMapping("/api/users/{id}/validate")
    Map<String, Object> validateUser(@PathVariable("id") Long userId);
}
