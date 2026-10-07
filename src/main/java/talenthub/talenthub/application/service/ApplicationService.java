package talenthub.talenthub.application.service;

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
import talenthub.talenthub.user.entity.Freelance;
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

    public ApplicationResponseDto findById(Long id) {

        Application application = applicationRepository.findById(id)
                .orElseThrow(() ->
                        new ApplicationNotFoundException(id));

        return toResponseDto(application);
    }

    public ApplicationResponseDto create(ApplicationRequestDto request) {

        Long freelanceId = request.getFreelanceId();
        Long missionId = request.getMissionId();

        Freelance freelance = freelanceRepository.findById(freelanceId)
                .orElseThrow(() -> new FreelanceNotFoundException(freelanceId));

        Mission mission = missionRepository.findById(missionId)
                .orElseThrow(() -> new MissionNotFoundException(missionId));

        if (mission.getStatus() != MissionStatus.OPEN) {
            throw new MissionNotOpenException();
        }

        if (applicationRepository.existsByFreelanceIdAndMissionId(freelanceId, missionId)
        ){
            throw new ApplicationAlreadyExistsException();
        }

        Application application = new Application();
        toUpdateApplicationData(application, request, freelance, mission);

        return toResponseDto(applicationRepository.save(application));
    }

    /**
     * Rule : a freelance can withdraw an application only if its status is "PENDING"
     */
    public ApplicationResponseDto withdraw(Long id) {

        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new ApplicationNotFoundException(id));

        if (application.getStatus() != PENDING) {
            throw new ApplicationNotPendingException(id);
        }

        application.setStatus(WITHDRAWN);

        return toResponseDto(applicationRepository.save(application));
    }

    /**
     * Rule : the client of a given mission is the only one that can accept an application "PENDING" for that mission
     * Once it's accepted, its status changes into "CLOSED", all the other applications are "REJECTED"
     */
    public ApplicationResponseDto accept(Long id) {
        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new ApplicationNotFoundException(id));

        if ( application.getStatus() != PENDING) {
            throw new ApplicationNotPendingException(id);
        }

        Mission mission = application.getMission();

        application.setStatus(ACCEPTED);
        mission.setStatus(MissionStatus.CLOSED);

        List<Application> otherApplications =
                applicationRepository.findByMissionIdAndStatus(mission.getId(), PENDING);

        otherApplications.forEach(app -> app.setStatus(REJECTED));

        applicationRepository.saveAll(otherApplications);
        applicationRepository.save(application);
        missionRepository.save(mission);

        return toResponseDto(application);
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
