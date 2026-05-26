package ba.unsa.etf.nwt.notificationservice.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
public class UserClientFallback implements UserClient {

    @Override
    public Map<String, Object> getUser(Long userId) {
        log.warn("user-service nije dostupan — getUser fallback za userId={}", userId);
        return Map.of();
    }
}
