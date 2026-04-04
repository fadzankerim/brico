package ba.unsa.etf.nwt.salonservice.service;

import ba.unsa.etf.nwt.salonservice.dto.SalonRequest;
import ba.unsa.etf.nwt.salonservice.dto.SalonResponse;
import ba.unsa.etf.nwt.salonservice.dto.ServiceRequest;
import ba.unsa.etf.nwt.salonservice.dto.ServiceResponse;
import ba.unsa.etf.nwt.salonservice.exception.ResourceNotFoundException;
import ba.unsa.etf.nwt.salonservice.model.Salon;
import ba.unsa.etf.nwt.salonservice.model.SalonService;
import ba.unsa.etf.nwt.salonservice.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SalonMgmtServiceTest {

    @Mock SalonRepository salonRepository;
    @Mock HairdresserRepository hairdresserRepository;
    @Mock SalonServiceRepository serviceRepository;
    @Mock SalonPhotoRepository photoRepository;
    @InjectMocks SalonMgmtService salonMgmtService;

    private final ModelMapper modelMapper = new ModelMapper();

    @BeforeEach
    void setup() throws Exception {
        var field = SalonMgmtService.class.getDeclaredField("modelMapper");
        field.setAccessible(true);
        field.set(salonMgmtService, modelMapper);
    }

    private Salon sampleSalon() {
        return Salon.builder()
                .id(1L)
                .name("Elite Cut")
                .slug("elite-cut")
                .city("Sarajevo")
                .address("Titova 1")
                .ownerId(1L)
                .verified(false)
                .isActive(true)
                .build();
    }

    @Test
    void findAll_returnsAllSalons() {
        when(salonRepository.findAll()).thenReturn(List.of(sampleSalon()));
        List<SalonResponse> result = salonMgmtService.findAll();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Elite Cut");
    }

    @Test
    void findById_existing_returnsSalon() {
        when(salonRepository.findById(1L)).thenReturn(Optional.of(sampleSalon()));
        SalonResponse resp = salonMgmtService.findById(1L);
        assertThat(resp.getSlug()).isEqualTo("elite-cut");
    }

    @Test
    void findById_unknown_throwsNotFound() {
        when(salonRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> salonMgmtService.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void create_generatesSlugAndSaves() {
        when(salonRepository.existsBySlug(any())).thenReturn(false);
        when(salonRepository.save(any())).thenAnswer(inv -> {
            Salon s = inv.getArgument(0);
            s.setId(10L);
            return s;
        });

        SalonRequest req = new SalonRequest();
        req.setName("Nova Frizura");
        req.setCity("Mostar");
        req.setAddress("Bulevar 5");
        req.setOwnerId(2L);

        SalonResponse resp = salonMgmtService.create(req);
        assertThat(resp.getSlug()).isEqualTo("nova-frizura");
        assertThat(resp.getId()).isEqualTo(10L);
    }

    @Test
    void delete_existing_callsRepository() {
        when(salonRepository.findById(1L)).thenReturn(Optional.of(sampleSalon()));
        salonMgmtService.delete(1L);
        verify(salonRepository).deleteById(1L);
    }

    @Test
    void addService_validRequest_savesService() {
        when(salonRepository.findById(1L)).thenReturn(Optional.of(sampleSalon()));
        SalonService saved = SalonService.builder()
                .id(5L).name("Šišanje").price(new BigDecimal("15.00"))
                .durationMinutes(30).isActive(true).salon(sampleSalon()).build();
        when(serviceRepository.save(any())).thenReturn(saved);

        ServiceRequest req = new ServiceRequest();
        req.setName("Šišanje");
        req.setPrice(new BigDecimal("15.00"));
        req.setDurationMinutes(30);

        ServiceResponse resp = salonMgmtService.addService(1L, req);
        assertThat(resp.getName()).isEqualTo("Šišanje");
        assertThat(resp.getSalonId()).isEqualTo(1L);
    }

    @Test
    void findServicesBySalon_unknownSalon_throwsNotFound() {
        when(salonRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> salonMgmtService.findServicesBySalon(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
