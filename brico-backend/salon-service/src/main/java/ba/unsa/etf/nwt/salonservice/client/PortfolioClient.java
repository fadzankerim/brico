package ba.unsa.etf.nwt.salonservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@FeignClient(name = "portfolio-service", fallback = PortfolioClientFallback.class)
public interface PortfolioClient {

    @GetMapping("/api/subscriptions/salon/{salonId}/limits")
    Map<String, Object> getSalonLimits(@PathVariable Long salonId);
}
