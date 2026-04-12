package com.example.lizaalert.service;

import com.example.lizaalert.domain.model.CaseStatus;
import com.example.lizaalert.domain.model.LostCase;
import com.example.lizaalert.domain.model.MediaAsset;
import com.example.lizaalert.domain.model.MediaType;
import com.example.lizaalert.domain.model.PetProfile;
import com.example.lizaalert.domain.model.PetSex;
import com.example.lizaalert.domain.model.PetSpecies;
import com.example.lizaalert.domain.model.ReportStatus;
import com.example.lizaalert.domain.model.SearchLocation;
import com.example.lizaalert.domain.model.SightingReport;
import com.example.lizaalert.domain.model.UserAccount;
import com.example.lizaalert.domain.model.UserRole;
import com.example.lizaalert.repository.LostCaseRepository;
import com.example.lizaalert.repository.MediaAssetRepository;
import com.example.lizaalert.repository.PetProfileRepository;
import com.example.lizaalert.repository.SightingReportRepository;
import com.example.lizaalert.repository.UserAccountRepository;
import com.example.lizaalert.web.dto.CreateLostCaseRequest;
import com.example.lizaalert.web.dto.CreateSightingRequest;
import com.example.lizaalert.web.dto.LocationDto;
import com.example.lizaalert.web.dto.LostCaseDetailsResponse;
import com.example.lizaalert.web.dto.LostCaseSummaryResponse;
import com.example.lizaalert.web.dto.MediaAssetResponse;
import com.example.lizaalert.web.dto.PetResponse;
import com.example.lizaalert.web.dto.SightingResponse;
import com.example.lizaalert.web.dto.UpdateCaseStatusRequest;
import com.example.lizaalert.web.dto.UserContactResponse;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LostCaseService {

    private static final Map<CaseStatus, EnumSet<CaseStatus>> ALLOWED_TRANSITIONS = Map.of(
        CaseStatus.DRAFT, EnumSet.of(CaseStatus.ACTIVE, CaseStatus.ARCHIVED),
        CaseStatus.ACTIVE, EnumSet.of(CaseStatus.IN_VERIFICATION, CaseStatus.FOUND, CaseStatus.CLOSED, CaseStatus.ARCHIVED),
        CaseStatus.IN_VERIFICATION, EnumSet.of(CaseStatus.ACTIVE, CaseStatus.FOUND, CaseStatus.CLOSED, CaseStatus.ARCHIVED),
        CaseStatus.FOUND, EnumSet.of(CaseStatus.CLOSED, CaseStatus.ARCHIVED),
        CaseStatus.CLOSED, EnumSet.of(CaseStatus.ARCHIVED),
        CaseStatus.ARCHIVED, EnumSet.noneOf(CaseStatus.class)
    );

    private final LostCaseRepository lostCaseRepository;
    private final UserAccountRepository userAccountRepository;
    private final PetProfileRepository petProfileRepository;
    private final SightingReportRepository sightingReportRepository;
    private final MediaAssetRepository mediaAssetRepository;

    public LostCaseService(
        LostCaseRepository lostCaseRepository,
        UserAccountRepository userAccountRepository,
        PetProfileRepository petProfileRepository,
        SightingReportRepository sightingReportRepository,
        MediaAssetRepository mediaAssetRepository
    ) {
        this.lostCaseRepository = lostCaseRepository;
        this.userAccountRepository = userAccountRepository;
        this.petProfileRepository = petProfileRepository;
        this.sightingReportRepository = sightingReportRepository;
        this.mediaAssetRepository = mediaAssetRepository;
    }

    @Transactional(readOnly = true)
    public List<LostCaseSummaryResponse> findCases(CaseStatus status, PetSpecies species) {
        CaseStatus effectiveStatus = status == null ? CaseStatus.ACTIVE : status;
        List<LostCase> cases = species == null
            ? lostCaseRepository.findAllByStatus(effectiveStatus)
            : lostCaseRepository.findAllByStatusAndPetProfileSpecies(effectiveStatus, species);

        return cases.stream()
            .sorted(Comparator.comparing(LostCase::isUrgent).reversed().thenComparing(LostCase::getLostAt).reversed())
            .map(this::toSummary)
            .toList();
    }

    @Transactional(readOnly = true)
    public LostCaseDetailsResponse getCase(UUID caseId) {
        LostCase lostCase = getExistingCase(caseId);
        List<SightingReport> sightings = sightingReportRepository.findAllByLostCaseIdOrderBySeenAtDesc(caseId);
        List<MediaAsset> media = mediaAssetRepository.findAllByLostCaseIdOrderBySortOrderAsc(caseId);
        return toDetails(lostCase, sightings, media);
    }

    @Transactional
    public LostCaseDetailsResponse createCase(CreateLostCaseRequest request) {
        UserAccount owner = createOwner(request.owner());
        PetProfile petProfile = createPetProfile(owner, request.pet());

        LostCase lostCase = new LostCase();
        lostCase.setPetProfile(petProfile);
        lostCase.setCreatedBy(owner);
        lostCase.setStatus(CaseStatus.ACTIVE);
        lostCase.setLostAt(request.caseData().lostAt());
        lostCase.setLastSeenLocation(toLocation(request.caseData().lastSeenLocation()));
        lostCase.setCircumstances(request.caseData().circumstances());
        lostCase.setContactPhone(request.caseData().contactPhone());
        lostCase.setRewardDetails(request.caseData().rewardDetails());
        lostCase.setUrgent(request.caseData().urgent());
        lostCaseRepository.save(lostCase);

        if (request.media() != null) {
            int index = 0;
            for (CreateLostCaseRequest.MediaRequest mediaRequest : request.media()) {
                MediaAsset mediaAsset = new MediaAsset();
                mediaAsset.setLostCase(lostCase);
                mediaAsset.setMediaType(mediaRequest.mediaType() == null ? MediaType.PHOTO : mediaRequest.mediaType());
                mediaAsset.setExternalUrl(mediaRequest.externalUrl());
                mediaAsset.setCaption(mediaRequest.caption());
                mediaAsset.setSortOrder(index++);
                mediaAssetRepository.save(mediaAsset);
            }
        }

        return getCase(lostCase.getId());
    }

    @Transactional
    public LostCaseDetailsResponse addSighting(UUID caseId, CreateSightingRequest request) {
        LostCase lostCase = getExistingCase(caseId);

        SightingReport report = new SightingReport();
        report.setLostCase(lostCase);
        report.setReporterName(request.reporterName());
        report.setReporterPhone(request.reporterPhone());
        report.setSeenAt(request.seenAt());
        report.setStatus(ReportStatus.NEW);
        report.setConfidenceScore(request.confidenceScore());
        report.setLocation(toLocation(request.location()));
        report.setNotes(request.notes());
        sightingReportRepository.save(report);

        return getCase(caseId);
    }

    @Transactional
    public LostCaseDetailsResponse updateStatus(UUID caseId, UpdateCaseStatusRequest request) {
        LostCase lostCase = getExistingCase(caseId);
        CaseStatus nextStatus = request.status();

        if (lostCase.getStatus() == nextStatus) {
            return getCase(caseId);
        }

        if (!ALLOWED_TRANSITIONS.getOrDefault(lostCase.getStatus(), EnumSet.noneOf(CaseStatus.class)).contains(nextStatus)) {
            throw new ApiException(
                HttpStatus.BAD_REQUEST,
                "Недопустимый переход статуса: %s -> %s".formatted(lostCase.getStatus(), nextStatus)
            );
        }

        lostCase.setStatus(nextStatus);
        return getCase(caseId);
    }

    private LostCase getExistingCase(UUID caseId) {
        return lostCaseRepository.findDetailedById(caseId)
            .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Кейс пропажи не найден"));
    }

    private UserAccount createOwner(CreateLostCaseRequest.OwnerRequest request) {
        if (request.email() != null && userAccountRepository.findByEmail(request.email()).isPresent()) {
            throw new ApiException(HttpStatus.CONFLICT, "Пользователь с таким email уже существует");
        }

        UserAccount owner = new UserAccount();
        owner.setFullName(request.fullName());
        owner.setEmail(request.email());
        owner.setPhone(request.phone());
        owner.setTelegramHandle(request.telegramHandle());
        owner.setRole(UserRole.OWNER);
        try {
            return userAccountRepository.saveAndFlush(owner);
        } catch (DataIntegrityViolationException exception) {
            throw new ApiException(HttpStatus.CONFLICT, "Пользователь с таким email уже существует");
        }
    }

    private PetProfile createPetProfile(UserAccount owner, CreateLostCaseRequest.PetRequest request) {
        PetProfile petProfile = new PetProfile();
        petProfile.setOwner(owner);
        petProfile.setSpecies(request.species());
        petProfile.setName(request.name());
        petProfile.setBreed(request.breed());
        petProfile.setSex(request.sex() == null ? PetSex.UNKNOWN : request.sex());
        petProfile.setAgeYears(request.ageYears());
        petProfile.setSizeLabel(request.sizeLabel());
        petProfile.setPrimaryColor(request.primaryColor());
        petProfile.setSecondaryColor(request.secondaryColor());
        petProfile.setSpecialMarks(request.specialMarks());
        petProfile.setMicrochipId(request.microchipId());
        petProfile.setCollarDetails(request.collarDetails());
        petProfile.setBehaviorNotes(request.behaviorNotes());
        return petProfileRepository.save(petProfile);
    }

    private LostCaseSummaryResponse toSummary(LostCase lostCase) {
        return new LostCaseSummaryResponse(
            lostCase.getId(),
            lostCase.getStatus(),
            lostCase.getLostAt(),
            lostCase.isUrgent(),
            toPetResponse(lostCase.getPetProfile()),
            toLocationDto(lostCase.getLastSeenLocation())
        );
    }

    private LostCaseDetailsResponse toDetails(LostCase lostCase, List<SightingReport> sightings, List<MediaAsset> media) {
        return new LostCaseDetailsResponse(
            lostCase.getId(),
            lostCase.getStatus(),
            lostCase.getLostAt(),
            lostCase.getCircumstances(),
            lostCase.getContactPhone(),
            lostCase.getRewardDetails(),
            lostCase.isUrgent(),
            toPetResponse(lostCase.getPetProfile()),
            new UserContactResponse(
                lostCase.getCreatedBy().getId(),
                lostCase.getCreatedBy().getFullName(),
                lostCase.getCreatedBy().getPhone(),
                lostCase.getCreatedBy().getTelegramHandle()
            ),
            toLocationDto(lostCase.getLastSeenLocation()),
            sightings.stream().map(sighting -> new SightingResponse(
                sighting.getId(),
                sighting.getReporterName(),
                sighting.getReporterPhone(),
                sighting.getSeenAt(),
                sighting.getStatus(),
                sighting.getConfidenceScore(),
                toLocationDto(sighting.getLocation()),
                sighting.getNotes()
            )).toList(),
            media.stream().map(asset -> new MediaAssetResponse(
                asset.getId(),
                asset.getMediaType(),
                asset.getExternalUrl(),
                asset.getCaption(),
                asset.getSortOrder()
            )).toList()
        );
    }

    private PetResponse toPetResponse(PetProfile petProfile) {
        return new PetResponse(
            petProfile.getId(),
            petProfile.getSpecies(),
            petProfile.getName(),
            petProfile.getBreed(),
            petProfile.getSex(),
            petProfile.getAgeYears(),
            petProfile.getSizeLabel(),
            petProfile.getPrimaryColor(),
            petProfile.getSecondaryColor(),
            petProfile.getSpecialMarks(),
            petProfile.getMicrochipId(),
            petProfile.getCollarDetails(),
            petProfile.getBehaviorNotes()
        );
    }

    private SearchLocation toLocation(LocationDto locationDto) {
        if (locationDto == null) {
            return null;
        }

        SearchLocation location = new SearchLocation();
        location.setCity(locationDto.city());
        location.setDistrict(locationDto.district());
        location.setAddress(locationDto.address());
        location.setLatitude(locationDto.latitude());
        location.setLongitude(locationDto.longitude());
        location.setNotes(locationDto.notes());
        return location;
    }

    private LocationDto toLocationDto(SearchLocation location) {
        if (location == null) {
            return null;
        }

        return new LocationDto(
            location.getCity(),
            location.getDistrict(),
            location.getAddress(),
            location.getLatitude(),
            location.getLongitude(),
            location.getNotes()
        );
    }
}

