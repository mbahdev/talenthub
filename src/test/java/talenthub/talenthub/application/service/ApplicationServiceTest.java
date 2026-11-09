
package talenthub.talenthub.application.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import talenthub.talenthub.application.dto.ApplicationRequestDto;
import talenthub.talenthub.application.dto.ApplicationResponseDto;
import talenthub.talenthub.application.entity.Application;
import talenthub.talenthub.application.entity.ApplicationStatus;
import talenthub.talenthub.application.exception.ApplicationAlreadyExistsException;
import talenthub.talenthub.application.exception.ApplicationNotPendingException;
import talenthub.talenthub.application.exception.FreelanceNotFoundException;
import talenthub.talenthub.application.exception.MissionNotOpenException;
import talenthub.talenthub.application.repository.ApplicationRepository;
import talenthub.talenthub.mission.entity.Mission;
import talenthub.talenthub.mission.entity.MissionStatus;
import talenthub.talenthub.mission.exception.MissionNotFoundException;
import talenthub.talenthub.mission.repository.MissionRepository;
import talenthub.talenthub.user.entity.Client;
import talenthub.talenthub.user.entity.Freelance;
import talenthub.talenthub.user.repository.FreelanceRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApplicationServiceTest {

    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private FreelanceRepository freelanceRepository;

    @Mock
    private MissionRepository missionRepository;

    @InjectMocks
    private ApplicationService applicationService;

    // --------------------------------------------------
    // CREATE
    // --------------------------------------------------

    @Test
    void shouldCreateApplicationSuccessfully() {
        Long freelanceId = 1L;
        Long missionId = 10L;

        Freelance freelance = new Freelance();
        freelance.setId(freelanceId);

        Client client = new Client();
        client.setId(2L);

        Mission mission = new Mission();
        mission.setId(missionId);
        mission.setClient(client);
        mission.setStatus(MissionStatus.OPEN);

        ApplicationRequestDto request = new ApplicationRequestDto();
        request.setMissionId(missionId);

        Application savedApplication = new Application();
        savedApplication.setId(100L);
        savedApplication.setFreelance(freelance);
        savedApplication.setMission(mission);
        savedApplication.setStatus(ApplicationStatus.PENDING);

        when(freelanceRepository.findById(freelanceId))
                .thenReturn(Optional.of(freelance));
        when(missionRepository.findById(missionId))
                .thenReturn(Optional.of(mission));
        when(applicationRepository.existsByFreelanceIdAndMissionId(freelanceId, missionId))
                .thenReturn(false);
        when(applicationRepository.save(any(Application.class)))
                .thenReturn(savedApplication);

        ApplicationResponseDto result = applicationService.create(request, freelanceId);

        assertThat(result.getId()).isEqualTo(100L);
        assertThat(result.getFreelanceId()).isEqualTo(freelanceId);
        assertThat(result.getMissionId()).isEqualTo(missionId);
        assertThat(result.getStatus()).isEqualTo(ApplicationStatus.PENDING);

        verify(applicationRepository).save(any(Application.class));
    }

    @Test
    void shouldThrowExceptionWhenFreelanceDoesNotExist() {
        Long freelanceId = 1L;
        Long missionId = 10L;

        ApplicationRequestDto request = new ApplicationRequestDto();
        request.setMissionId(missionId);

        when(freelanceRepository.findById(freelanceId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> applicationService.create(request, freelanceId))
                .isInstanceOf(FreelanceNotFoundException.class);

        verify(missionRepository, never()).findById(anyLong());
        verify(applicationRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenMissionDoesNotExist() {
        Long freelanceId = 1L;
        Long missionId = 10L;

        Freelance freelance = new Freelance();
        freelance.setId(freelanceId);

        ApplicationRequestDto request = new ApplicationRequestDto();
        request.setMissionId(missionId);

        when(freelanceRepository.findById(freelanceId))
                .thenReturn(Optional.of(freelance));
        when(missionRepository.findById(missionId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> applicationService.create(request, freelanceId))
                .isInstanceOf(MissionNotFoundException.class);

        verify(applicationRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenMissionIsNotOpen() {
        Long freelanceId = 1L;
        Long missionId = 10L;

        Freelance freelance = new Freelance();
        freelance.setId(freelanceId);

        Mission mission = new Mission();
        mission.setId(missionId);
        mission.setStatus(MissionStatus.CLOSED);

        ApplicationRequestDto request = new ApplicationRequestDto();
        request.setMissionId(missionId);

        when(freelanceRepository.findById(freelanceId))
                .thenReturn(Optional.of(freelance));
        when(missionRepository.findById(missionId))
                .thenReturn(Optional.of(mission));

        assertThatThrownBy(() -> applicationService.create(request, freelanceId))
                .isInstanceOf(MissionNotOpenException.class);

        verify(applicationRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenApplicationAlreadyExists() {
        Long freelanceId = 1L;
        Long missionId = 10L;

        Freelance freelance = new Freelance();
        freelance.setId(freelanceId);

        Mission mission = new Mission();
        mission.setId(missionId);
        mission.setStatus(MissionStatus.OPEN);

        ApplicationRequestDto request = new ApplicationRequestDto();
        request.setMissionId(missionId);

        when(freelanceRepository.findById(freelanceId))
                .thenReturn(Optional.of(freelance));
        when(missionRepository.findById(missionId))
                .thenReturn(Optional.of(mission));
        when(applicationRepository.existsByFreelanceIdAndMissionId(freelanceId, missionId))
                .thenReturn(true);

        assertThatThrownBy(() -> applicationService.create(request, freelanceId))
                .isInstanceOf(ApplicationAlreadyExistsException.class);

        verify(applicationRepository, never()).save(any());
    }

    // --------------------------------------------------
    // WITHDRAW
    // --------------------------------------------------

    @Test
    void shouldWithdrawApplicationSuccessfully() {
        Long applicationId = 1L;
        Long freelanceId = 10L;

        Freelance freelance = new Freelance();
        freelance.setId(freelanceId);

        Mission mission = new Mission();
        mission.setId(20L);

        Application application = new Application();
        application.setId(applicationId);
        application.setFreelance(freelance);
        application.setMission(mission);
        application.setStatus(ApplicationStatus.PENDING);

        when(applicationRepository.findById(applicationId))
                .thenReturn(Optional.of(application));
        when(applicationRepository.save(any(Application.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ApplicationResponseDto result =
                applicationService.withdraw(applicationId, freelanceId);

        assertThat(result.getStatus()).isEqualTo(ApplicationStatus.WITHDRAWN);
        assertThat(application.getStatus()).isEqualTo(ApplicationStatus.WITHDRAWN);

        verify(applicationRepository).save(application);
    }

    @Test
    void shouldRejectWithdrawalOfAnotherFreelancesApplication() {
        Long applicationId = 1L;
        Long ownerFreelanceId = 10L;
        Long otherFreelanceId = 20L;

        Freelance owner = new Freelance();
        owner.setId(ownerFreelanceId);

        Application application = new Application();
        application.setId(applicationId);
        application.setFreelance(owner);
        application.setStatus(ApplicationStatus.PENDING);

        when(applicationRepository.findById(applicationId))
                .thenReturn(Optional.of(application));

        assertThatThrownBy(() ->
                applicationService.withdraw(applicationId, otherFreelanceId))
                .isInstanceOf(AccessDeniedException.class);

        verify(applicationRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenWithdrawingNonPendingApplication() {
        Long applicationId = 1L;
        Long freelanceId = 10L;

        Freelance freelance = new Freelance();
        freelance.setId(freelanceId);

        Application application = new Application();
        application.setId(applicationId);
        application.setFreelance(freelance);
        application.setStatus(ApplicationStatus.ACCEPTED);

        when(applicationRepository.findById(applicationId))
                .thenReturn(Optional.of(application));

        assertThatThrownBy(() ->
                applicationService.withdraw(applicationId, freelanceId))
                .isInstanceOf(ApplicationNotPendingException.class);

        verify(applicationRepository, never()).save(any());
    }

    // --------------------------------------------------
    // ACCEPT
    // --------------------------------------------------

    @Test
    void shouldAcceptApplicationAndRejectOthers() {
        Long applicationId = 1L;
        Long clientId = 2L;

        Client client = new Client();
        client.setId(clientId);

        Mission mission = new Mission();
        mission.setId(10L);
        mission.setClient(client);
        mission.setStatus(MissionStatus.OPEN);

        Application acceptedApplication = new Application();
        acceptedApplication.setId(applicationId);
        acceptedApplication.setMission(mission);
        acceptedApplication.setFreelance(new Freelance());
        acceptedApplication.setStatus(ApplicationStatus.PENDING);

        Application otherApplication = new Application();
        otherApplication.setId(2L);
        otherApplication.setMission(mission);
        otherApplication.setFreelance(new Freelance());
        otherApplication.setStatus(ApplicationStatus.PENDING);

        when(applicationRepository.findById(applicationId))
                .thenReturn(Optional.of(acceptedApplication));
        when(applicationRepository.findByMissionIdAndStatus(
                10L, ApplicationStatus.PENDING))
                .thenReturn(List.of(acceptedApplication, otherApplication));
        when(applicationRepository.save(any(Application.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ApplicationResponseDto result =
                applicationService.accept(applicationId, clientId);

        assertThat(result.getStatus()).isEqualTo(ApplicationStatus.ACCEPTED);
        assertThat(acceptedApplication.getStatus()).isEqualTo(ApplicationStatus.ACCEPTED);
        assertThat(otherApplication.getStatus()).isEqualTo(ApplicationStatus.REJECTED);
        assertThat(mission.getStatus()).isEqualTo(MissionStatus.CLOSED);

        verify(applicationRepository).saveAll(anyList());
        verify(applicationRepository).save(acceptedApplication);
        verify(missionRepository).save(mission);
    }

    // --------------------------------------------------
    // REJECT
    // --------------------------------------------------

    @Test
    void shouldRejectApplicationSuccessfully() {
        Long applicationId = 1L;
        Long clientId = 2L;

        Client client = new Client();
        client.setId(clientId);

        Mission mission = new Mission();
        mission.setId(10L);
        mission.setClient(client);

        Application application = new Application();
        application.setId(applicationId);
        application.setMission(mission);
        application.setFreelance(new Freelance());
        application.setStatus(ApplicationStatus.PENDING);

        when(applicationRepository.findById(applicationId))
                .thenReturn(Optional.of(application));
        when(applicationRepository.save(any(Application.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ApplicationResponseDto result =
                applicationService.reject(applicationId, clientId);

        assertThat(result.getStatus()).isEqualTo(ApplicationStatus.REJECTED);
        assertThat(application.getStatus()).isEqualTo(ApplicationStatus.REJECTED);

        verify(applicationRepository).save(application);
    }

    @Test
    void shouldRejectRejectionByAnotherClient() {
        Long applicationId = 1L;
        Long ownerClientId = 2L;
        Long otherClientId = 3L;

        Client owner = new Client();
        owner.setId(ownerClientId);

        Mission mission = new Mission();
        mission.setClient(owner);

        Application application = new Application();
        application.setId(applicationId);
        application.setMission(mission);
        application.setStatus(ApplicationStatus.PENDING);

        when(applicationRepository.findById(applicationId))
                .thenReturn(Optional.of(application));

        assertThatThrownBy(() ->
                applicationService.reject(applicationId, otherClientId))
                .isInstanceOf(AccessDeniedException.class);

        verify(applicationRepository, never()).save(any());
    }

    // --------------------------------------------------
    // ACCESS CONTROL
    // --------------------------------------------------

    @Test
    void shouldRejectAccessToAnotherFreelancesApplications() {
        Long freelanceId = 10L;
        Long authenticatedUserId = 20L;

        assertThatThrownBy(() ->
                applicationService.findByFreelanceId(freelanceId, authenticatedUserId))
                .isInstanceOf(AccessDeniedException.class);

        verify(freelanceRepository, never()).findById(anyLong());
        verify(applicationRepository, never()).findByFreelanceId(anyLong());
    }

    @Test
    void shouldRejectAccessToAnotherClientsMissionApplications() {
        Long missionId = 10L;
        Long ownerClientId = 2L;
        Long otherClientId = 3L;

        Client owner = new Client();
        owner.setId(ownerClientId);

        Mission mission = new Mission();
        mission.setId(missionId);
        mission.setClient(owner);

        when(missionRepository.findById(missionId))
                .thenReturn(Optional.of(mission));

        assertThatThrownBy(() ->
                applicationService.findByMissionId(missionId, otherClientId))
                .isInstanceOf(AccessDeniedException.class);

        verify(applicationRepository, never()).findByMissionId(anyLong());
    }
}
