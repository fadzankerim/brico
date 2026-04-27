package ba.unsa.etf.nwt.reviewservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

/**
 * Feign klijent za komunikaciju sa booking-service.
 * Ime "booking-service" se resolvuje putem Eureka service discovery — bez hardkodiranja IP/porta.
 * Koristi se pri kreiranju recenzije da bi se provjerilo da termin postoji i ima status COMPLETED.
 */
@FeignClient(name = "booking-service", fallback = BookingClientFallback.class)
public interface BookingClient {

    /**
     * Validacija termina — provjerava status termina.
     * Vraća: { id, status, salonId, clientId, completed }
     * Baca 404 ako termin ne postoji.
     */
    @GetMapping("/api/appointments/{id}/validate")
    Map<String, Object> validateAppointment(@PathVariable("id") Long appointmentId);
}
