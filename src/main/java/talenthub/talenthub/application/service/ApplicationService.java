package talenthub.talenthub.application.service;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import talenthub.talenthub.application.dto.ApplicationRequestDto;
import talenthub.talenthub.application.dto.ApplicationResponseDto;
import talenthub.talenthub.application.entity.Application;
import talenthub.talenthub.application.entity.ApplicationStatus;
import talenthub.talenthub.application.exception.*;
import talenthub.talenthub.application.repository.ApplicationRepository;
import talenthub.talenthub.mission.entity.Mission;
import talenthub.talenthub.mission.entity.MissionStatus;
import talenthub.talenthub.mission.exception.MissionNotFoundException;
import talenthub.talenthub.mission.repository.MissionRepository;
import talenthub.talenthub.user.entity.Client;
import talenthub.talenthub.user.entity.Freelance;
import talenthub.talenthub.user.entity.User;
import talenthub.talenthub.user.repository.FreelanceRepository;

import java.util.List;

@Service
public class ApplicationService {
    
    private final ApplicationRepository applicationRepository;
    private final FreelanceRepository freelanceRepository;
    private final MissionRepository missionRepository;

    // Call the Statuses only one time
    ApplicationStatus PENDING =  ApplicationStatus.PENDING,
            ACCEPTED = ApplicationStatus.ACCEPTED,
            REJECTED = ApplicationStatus.REJECTED,
            WITHDRAWN = ApplicationStatus.WITHDRAWN;
    
    public ApplicationService(ApplicationRepository applicationRepository, 
                              FreelanceRepository freelanceRepository,
                              MissionRepository missionRepository) {
        this.applicationRepository = applicationRepository;
        this.missionRepository = missionRepository;
        this.freelanceRepository = freelanceRepository;
    }

    public List<ApplicationResponseDto> findAll() {
        return applicationRepository.findAll().stream().map(this::toResponseDto).toList();
    }

    public ApplicationResponseDto findById(Long id, User user) {

        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new ApplicationNotFoundException(id));

        boolean authorized = false;

        if (user instanceof Freelance) {
            authorized = application.getFreelance().getId().equals(user.getId());
        }

        if (user instanceof Client) {
            authorized = application.getMission()
                    .getClient().getId().equals(user.getId());
        }

        if (!authorized) {
            throw new AccessDeniedException("You cannot access this application.");
        }

        return toResponseDto(application);
    }

    public ApplicationResponseDto create(
            ApplicationRequestDto request,
            Long freelanceId) {

        Freelance freelance = freelanceRepository.findById(freelanceId)
                .orElseThrow(() -> new FreelanceNotFoundException(freelanceId));

        Long missionId = request.getMissionId();

        Mission mission = missionRepository.findById(missionId)
                .orElseThrow(() -> new MissionNotFoundException(missionId));

        if (mission.getStatus() != MissionStatus.OPEN) {
            throw new MissionNotOpenException();
        }

        if (applicationRepository.existsByFreelanceIdAndMissionId(freelanceId, missionId)) {
            throw new ApplicationAlreadyExistsException();
        }

        Application application = new Application();

        toUpdateApplicationData(application, request, freelance, mission);

        return toResponseDto(applicationRepository.save(application));
    }

    /**
     * Rule : a freelance can withdraw an application only if its status is "PENDING"
     */
    public ApplicationResponseDto withdraw(Long id, Long freelanceId) {

        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new ApplicationNotFoundException(id));

        if (!application.getFreelance().getId().equals(freelanceId)) {
            throw new AccessDeniedException("You cannot withdraw another freelance's application.");
        }

        if (application.getStatus() != PENDING) {
            throw new ApplicationNotPendingException(id);
        }

        application.setStatus(WITHDRAWN);

        return toResponseDto(applicationRepository.save(application));
    }

    /**
     * Rule : the client of a given mission is the only one who can accept an application "PENDING" for that mission
     * Once it's accepted, its status changes into "CLOSED", all the other applications are "REJECTED"
     */
    @Transactional
    public ApplicationResponseDto accept(Long id, Long clientId) {

        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new ApplicationNotFoundException(id));

        Mission mission = application.getMission();

        if (!mission.getClient().getId().equals(clientId)) {
            throw new AccessDeniedException("You cannot accept an application for another client's mission.");
        }

        if (application.getStatus() != PENDING) {
            throw new ApplicationNotPendingException(id);
        }

        application.setStatus(ACCEPTED);
        mission.setStatus(MissionStatus.CLOSED);

        List<Application> otherApplications =
                applicationRepository.findByMissionIdAndStatus(mission.getId(), PENDING);

        otherApplications.stream()
        .filter(app -> !app.getId().equals(application.getId()))
        .forEach(app -> app.setStatus(REJECTED));

        applicationRepository.saveAll(otherApplications);
        applicationRepository.save(application);
        missionRepository.save(mission);

        return toResponseDto(application);
    }

    public ApplicationResponseDto reject(Long id, Long clientId) {

        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new ApplicationNotFoundException(id));

        if (!application.getMission().getClient().getId().equals(clientId)) {
            throw new AccessDeniedException("You cannot reject an application for another client's mission.");
        }

        if (application.getStatus() != PENDING) {
            throw new ApplicationNotPendingException(id);
        }

        application.setStatus(REJECTED);

        return toResponseDto(applicationRepository.save(application));
    }


    //récupérer les candidatures par mission ou par freelance
    // le freelance consulte ses propres candidatures ;
    // le client consulte les candidatures reçues pour ses missions.
    public List<ApplicationResponseDto> findByFreelanceId(
            Long freelanceId,
            Long authenticatedUserId) {

        if (!freelanceId.equals(authenticatedUserId)) {
            throw new AccessDeniedException("You cannot access another freelance's applications.");
        }

        freelanceRepository.findById(freelanceId)
                .orElseThrow(() -> new FreelanceNotFoundException(freelanceId));

        return applicationRepository.findByFreelanceId(freelanceId)
                .stream()
                .map(this::toResponseDto)
                .toList();
    }

    public List<ApplicationResponseDto> findByMissionId(
            Long missionId,
            Long clientId) {

        Mission mission = missionRepository.findById(missionId)
                .orElseThrow(() -> new MissionNotFoundException(missionId));

        if (!mission.getClient().getId().equals(clientId)) {
            throw new AccessDeniedException("You cannot access applications for another client's mission.");
        }

        return applicationRepository.findByMissionId(missionId)
                .stream()
                .map(this::toResponseDto)
                .toList();
    }

    private ApplicationResponseDto toResponseDto(Application application) {

        ApplicationResponseDto dto = new ApplicationResponseDto();

        dto.setId(application.getId());
        dto.setCoverLetter(application.getCoverLetter());
        dto.setProposedPrice(application.getProposedPrice());
        dto.setStatus(application.getStatus());

        dto.setFreelanceId(
                application.getFreelance().getId()
        );

        dto.setMissionId(
                application.getMission().getId()
        );

        return dto;
    }

    public void toUpdateApplicationData(
            Application application, ApplicationRequestDto request, Freelance freelance, Mission mission) {
        application.setCoverLetter(request.getCoverLetter());
        application.setProposedPrice(request.getProposedPrice());
        application.setFreelance(freelance);
        application.setMission(mission);
        application.setStatus(PENDING);
    }

}
