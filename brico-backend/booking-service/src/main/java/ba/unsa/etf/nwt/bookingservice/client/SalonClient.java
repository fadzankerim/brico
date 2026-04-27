package ba.unsa.etf.nwt.bookingservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

/**
 * Feign klijent za komunikaciju sa salon-service.
 * Ime "salon-service" se resolvuje putem Eureka service discovery — bez hardkodiranja IP/porta.
 */
@FeignClient(name = "salon-service", fallback = SalonClientFallback.class)
public interface SalonClient {

    /**
     * Validacija salona — provjerava da li salon postoji i aktivan je.
     * Vraća: { id, active, name }
     */
    @GetMapping("/api/salons/{id}/validate")
    Map<String, Object> validateSalon(@PathVariable("id") Long salonId);

    /**
     * Validacija usluge — provjerava da li usluga postoji i pripada traženom salonu.
     * Vraća: { id, salonId, active, name, durationMinutes }
     */
    @GetMapping("/api/salons/{salonId}/services/{serviceId}/validate")
    Map<String, Object> validateService(@PathVariable("salonId") Long salonId,
                                        @PathVariable("serviceId") Long serviceId);
}
