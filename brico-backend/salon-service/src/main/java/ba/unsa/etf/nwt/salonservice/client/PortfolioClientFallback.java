package ba.unsa.etf.nwt.salonservice.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
public class PortfolioClientFallback implements PortfolioClient {

    @Override
    public Map<String, Object> getSalonLimits(Long salonId) {
        log.warn("portfolio-service nedostupan — koristim BASIC defaultne limite za salonId={}", salonId);
        return Map.of("planType", "BASIC", "maxHairdressers", 3);
    }
}
