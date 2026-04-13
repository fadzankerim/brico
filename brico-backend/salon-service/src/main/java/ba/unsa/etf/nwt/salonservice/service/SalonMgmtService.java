package ba.unsa.etf.nwt.salonservice.service;

import ba.unsa.etf.nwt.salonservice.dto.*;
import ba.unsa.etf.nwt.salonservice.exception.ResourceNotFoundException;
import ba.unsa.etf.nwt.salonservice.model.*;
import ba.unsa.etf.nwt.salonservice.repository.*;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SalonMgmtService {

    private final SalonRepository salonRepository;
    private final HairdresserRepository hairdresserRepository;
    private final SalonServiceRepository serviceRepository;
    private final SalonPhotoRepository photoRepository;
    private final ModelMapper modelMapper;

    // ── Salons ─────────────────────────────────────────────────────────

    public List<SalonResponse> findAll() {
        return salonRepository.findAll().stream()
                .map(s -> modelMapper.map(s, SalonResponse.class)).toList();
    }

    // Paginacija + sortiranje
    public Page<SalonResponse> findAllPaged(Pageable pageable) {
        return salonRepository.findAll(pageable)
                .map(s -> modelMapper.map(s, SalonResponse.class));
    }

    // Custom pretraga po gradu i minimalnoj ocjeni
    public List<SalonResponse> searchByCityAndRating(String city, Double minRating) {
        return salonRepository.searchByCityAndRating(city, minRating).stream()
                .map(s -> modelMapper.map(s, SalonResponse.class)).toList();
    }

    // Full-text pretraga sa paginacijom
    public Page<SalonResponse> search(String q, Pageable pageable) {
        return salonRepository.searchByNameOrCity(q, pageable)
                .map(s -> modelMapper.map(s, SalonResponse.class));
    }

    public List<SalonResponse> findByOwnerId(Long ownerId) {
        return salonRepository.findByOwnerId(ownerId).stream()
                .map(s -> modelMapper.map(s, SalonResponse.class)).toList();
    }

    public SalonResponse findById(Long id) {
        return modelMapper.map(getSalonOrThrow(id), SalonResponse.class);
    }

    public SalonResponse findBySlug(String slug) {
        Salon salon = salonRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Salon sa slugom '" + slug + "' nije pronađen"));
        return modelMapper.map(salon, SalonResponse.class);
    }

    @Transactional
    public SalonResponse create(SalonRequest req) {
        String slug = generateSlug(req.getName());
        if (salonRepository.existsBySlug(slug)) {
            slug = slug + "-" + System.currentTimeMillis();
        }
        Salon salon = modelMapper.map(req, Salon.class);
        salon.setSlug(slug);
        salon.setVerified(false);
        salon.setIsActive(true);
        return modelMapper.map(salonRepository.save(salon), SalonResponse.class);
    }

    @Transactional
    public SalonResponse update(Long id, SalonRequest req) {
        Salon salon = getSalonOrThrow(id);
        if (req.getName()        != null) salon.setName(req.getName());
        if (req.getDescription() != null) salon.setDescription(req.getDescription());
        if (req.getCity()        != null) salon.setCity(req.getCity());
        if (req.getAddress()     != null) salon.setAddress(req.getAddress());
        if (req.getPhone()       != null) salon.setPhone(req.getPhone());
        if (req.getWebsite()     != null) salon.setWebsite(req.getWebsite());
        if (req.getLatitude()    != null) salon.setLatitude(req.getLatitude());
        if (req.getLongitude()   != null) salon.setLongitude(req.getLongitude());
        return modelMapper.map(salonRepository.save(salon), SalonResponse.class);
    }

    @Transactional
    public void delete(Long id) {
        getSalonOrThrow(id);
        salonRepository.deleteById(id);
    }

    // ── Hairdressers ───────────────────────────────────────────────────

    public List<HairdresserResponse> findHairdressersBySalon(Long salonId) {
        getSalonOrThrow(salonId);
        return hairdresserRepository.findBySalonId(salonId).stream()
                .map(h -> {
                    HairdresserResponse r = modelMapper.map(h, HairdresserResponse.class);
                    r.setSalonId(salonId);
                    return r;
                }).toList();
    }

    @Transactional
    public HairdresserResponse addHairdresser(Long salonId, HairdresserRequest req) {
        Salon salon = getSalonOrThrow(salonId);
        Hairdresser h = modelMapper.map(req, Hairdresser.class);
        h.setSalon(salon);
        h.setIsActive(true);
        Hairdresser saved = hairdresserRepository.save(h);
        HairdresserResponse r = modelMapper.map(saved, HairdresserResponse.class);
        r.setSalonId(salonId);
        return r;
    }

    @Transactional
    public HairdresserResponse updateHairdresser(Long salonId, Long hairdresserId, HairdresserRequest req) {
        getSalonOrThrow(salonId);
        Hairdresser h = hairdresserRepository.findById(hairdresserId)
                .orElseThrow(() -> new ResourceNotFoundException("Frizer sa ID=" + hairdresserId + " nije pronađen"));
        if (req.getFullName()    != null) h.setFullName(req.getFullName());
        if (req.getBio()         != null) h.setBio(req.getBio());
        if (req.getSpecialties() != null) h.setSpecialties(req.getSpecialties());
        if (req.getProfilePhoto() != null) h.setProfilePhoto(req.getProfilePhoto());
        Hairdresser saved = hairdresserRepository.save(h);
        HairdresserResponse r = modelMapper.map(saved, HairdresserResponse.class);
        r.setSalonId(salonId);
        return r;
    }

    @Transactional
    public void removeHairdresser(Long salonId, Long hairdresserId) {
        getSalonOrThrow(salonId);
        hairdresserRepository.deleteById(hairdresserId);
    }

    // ── Services ───────────────────────────────────────────────────────

    public List<ServiceResponse> findServicesBySalon(Long salonId) {
        getSalonOrThrow(salonId);
        return serviceRepository.findBySalonId(salonId).stream()
                .map(s -> {
                    ServiceResponse r = modelMapper.map(s, ServiceResponse.class);
                    r.setSalonId(salonId);
                    return r;
                }).toList();
    }

    @Transactional
    public ServiceResponse addService(Long salonId, ServiceRequest req) {
        Salon salon = getSalonOrThrow(salonId);
        SalonService svc = modelMapper.map(req, SalonService.class);
        svc.setSalon(salon);
        svc.setIsActive(true);
        SalonService saved = serviceRepository.save(svc);
        ServiceResponse r = modelMapper.map(saved, ServiceResponse.class);
        r.setSalonId(salonId);
        return r;
    }

    // Batch insert usluga (transakcijski — ili sve ili ništa)
    @Transactional
    public List<ServiceResponse> addServicesBatch(Long salonId, List<ServiceRequest> requests) {
        Salon salon = getSalonOrThrow(salonId);
        List<SalonService> services = requests.stream().map(req -> {
            SalonService svc = modelMapper.map(req, SalonService.class);
            svc.setSalon(salon);
            svc.setIsActive(true);
            return svc;
        }).toList();
        return serviceRepository.saveAll(services).stream().map(saved -> {
            ServiceResponse r = modelMapper.map(saved, ServiceResponse.class);
            r.setSalonId(salonId);
            return r;
        }).toList();
    }

    @Transactional
    public ServiceResponse updateService(Long salonId, Long serviceId, ServiceRequest req) {
        getSalonOrThrow(salonId);
        SalonService svc = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Usluga sa ID=" + serviceId + " nije pronađena"));
        if (req.getName()            != null) svc.setName(req.getName());
        if (req.getDescription()     != null) svc.setDescription(req.getDescription());
        if (req.getPrice()           != null) svc.setPrice(req.getPrice());
        if (req.getDurationMinutes() != null) svc.setDurationMinutes(req.getDurationMinutes());
        SalonService saved = serviceRepository.save(svc);
        ServiceResponse r = modelMapper.map(saved, ServiceResponse.class);
        r.setSalonId(salonId);
        return r;
    }

    @Transactional
    public void deleteService(Long salonId, Long serviceId) {
        getSalonOrThrow(salonId);
        serviceRepository.deleteById(serviceId);
    }

    // ── Photos ─────────────────────────────────────────────────────────

    public List<PhotoResponse> findPhotosBySalon(Long salonId) {
        getSalonOrThrow(salonId);
        return photoRepository.findBySalonId(salonId).stream()
                .map(p -> modelMapper.map(p, PhotoResponse.class)).toList();
    }

    @Transactional
    public PhotoResponse addPhoto(Long salonId, PhotoRequest req) {
        Salon salon = getSalonOrThrow(salonId);
        // If this is the first photo or explicitly marked primary, clear existing primary
        if (Boolean.TRUE.equals(req.getIsPrimary())) {
            photoRepository.findBySalonIdAndIsPrimary(salonId, true)
                    .ifPresent(p -> { p.setIsPrimary(false); photoRepository.save(p); });
        }
        SalonPhoto photo = SalonPhoto.builder()
                .url(req.getUrl())
                .isPrimary(Boolean.TRUE.equals(req.getIsPrimary()))
                .displayOrder(req.getDisplayOrder() != null ? req.getDisplayOrder() : 0)
                .salon(salon)
                .build();
        return modelMapper.map(photoRepository.save(photo), PhotoResponse.class);
    }

    @Transactional
    public PhotoResponse setPrimary(Long salonId, Long photoId) {
        getSalonOrThrow(salonId);
        photoRepository.findBySalonIdAndIsPrimary(salonId, true)
                .ifPresent(p -> { p.setIsPrimary(false); photoRepository.save(p); });
        SalonPhoto photo = photoRepository.findById(photoId)
                .orElseThrow(() -> new ResourceNotFoundException("Fotografija sa ID=" + photoId + " nije pronađena"));
        photo.setIsPrimary(true);
        return modelMapper.map(photoRepository.save(photo), PhotoResponse.class);
    }

    @Transactional
    public void deletePhoto(Long salonId, Long photoId) {
        getSalonOrThrow(salonId);
        photoRepository.deleteById(photoId);
    }

    // ── Helpers ────────────────────────────────────────────────────────

    private Salon getSalonOrThrow(Long id) {
        return salonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Salon sa ID=" + id + " nije pronađen"));
    }

    private String generateSlug(String name) {
        String normalized = Normalizer.normalize(name.toLowerCase(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .replaceAll("[^a-z0-9\\s-]", "")
                .trim()
                .replaceAll("\\s+", "-");
        return normalized;
    }
}
