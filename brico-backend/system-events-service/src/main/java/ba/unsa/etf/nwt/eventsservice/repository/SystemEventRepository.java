package ba.unsa.etf.nwt.eventsservice.repository;

import ba.unsa.etf.nwt.eventsservice.model.SystemEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SystemEventRepository extends JpaRepository<SystemEvent, String> {
    List<SystemEvent> findTop50ByOrderByTimestampDesc();
    List<SystemEvent> findTop50ByServiceNameOrderByTimestampDesc(String serviceName);
    Page<SystemEvent> findAllByOrderByTimestampDesc(Pageable pageable);
    Page<SystemEvent> findByServiceNameOrderByTimestampDesc(String serviceName, Pageable pageable);
}
