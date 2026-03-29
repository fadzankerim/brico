package com.nwt.salonservice.service;

import com.nwt.salonservice.dto.request.SalonRequest;
import com.nwt.salonservice.dto.response.SalonResponse;
import com.nwt.salonservice.exception.ResourceNotFoundException;
import com.nwt.salonservice.model.Salon;
import com.nwt.salonservice.repository.SalonRepository;
import com.nwt.salonservice.service.impl.SalonServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class SalonServiceTest {

    @Mock
    private SalonRepository salonRepository;

    @InjectMocks
    private SalonServiceImpl salonService;

    private Salon testSalon;
    private SalonRequest testRequest;

    @BeforeEach
    void setUp() {
        testSalon = Salon.builder()
                .id(1L)
                .ownerId(10L)
                .name("Glam Studio")
                .city("Sarajevo")
                .address("Ferhadija 15")
                .avgRating(4.8)
                .verified(true)
                .phone("+387 33 123 456")
                .build();

        testRequest = SalonRequest.builder()
                .ownerId(10L)
                .name("Glam Studio")
                .city("Sarajevo")
                .address("Ferhadija 15")
                .phone("+387 33 123 456")
                .build();
    }

    @Test
    void findById_WhenSalonExists_ReturnsSalonResponse() {
        when(salonRepository.findById(1L)).thenReturn(Optional.of(testSalon));

        SalonResponse result = salonService.findById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Glam Studio");
        assertThat(result.getCity()).isEqualTo("Sarajevo");
        assertThat(result.getAvgRating()).isEqualTo(4.8);
    }

    @Test
    void findById_WhenSalonNotFound_ThrowsResourceNotFoundException() {
        when(salonRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> salonService.findById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("999");
    }

    @Test
    void create_WithValidRequest_ReturnsSalonResponse() {
        when(salonRepository.save(any(Salon.class))).thenReturn(testSalon);

        SalonResponse result = salonService.create(testRequest);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Glam Studio");
        assertThat(result.getCity()).isEqualTo("Sarajevo");
        verify(salonRepository, times(1)).save(any(Salon.class));
    }

    @Test
    void searchSalons_ReturnsPaginatedResults() {
        PageRequest pageable = PageRequest.of(0, 10);
        Page<Salon> salonPage = new PageImpl<>(List.of(testSalon), pageable, 1);
        when(salonRepository.searchSalons(eq("Sarajevo"), isNull(), eq(pageable)))
                .thenReturn(salonPage);

        Page<com.nwt.salonservice.dto.response.SalonSummaryResponse> result =
                salonService.searchSalons("Sarajevo", null, pageable);

        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("Glam Studio");
    }

    @Test
    void verify_WhenSalonExists_SetsVerifiedTrue() {
        testSalon.setVerified(false);
        Salon verifiedSalon = Salon.builder()
                .id(1L)
                .ownerId(10L)
                .name("Glam Studio")
                .city("Sarajevo")
                .verified(true)
                .avgRating(0.0)
                .build();

        when(salonRepository.findById(1L)).thenReturn(Optional.of(testSalon));
        when(salonRepository.save(any(Salon.class))).thenReturn(verifiedSalon);

        SalonResponse result = salonService.verify(1L);

        assertThat(result.getVerified()).isTrue();
        verify(salonRepository, times(1)).save(any(Salon.class));
    }

    @Test
    void delete_WhenSalonNotFound_ThrowsResourceNotFoundException() {
        when(salonRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> salonService.delete(999L, 1L, "ADMIN"))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
