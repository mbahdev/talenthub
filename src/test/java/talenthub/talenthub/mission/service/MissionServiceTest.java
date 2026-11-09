
package talenthub.talenthub.mission.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import talenthub.talenthub.mission.dto.MissionRequestDto;
import talenthub.talenthub.mission.dto.MissionResponseDto;
import talenthub.talenthub.mission.entity.Mission;
import talenthub.talenthub.mission.entity.MissionStatus;
import talenthub.talenthub.mission.exception.MissionNotFoundException;
import talenthub.talenthub.mission.repository.MissionRepository;
import talenthub.talenthub.skill.entity.Skill;
import talenthub.talenthub.skill.repository.SkillRepository;
import talenthub.talenthub.user.entity.Client;
import talenthub.talenthub.user.exception.ClientNotFoundException;
import talenthub.talenthub.user.repository.ClientRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MissionServiceTest {

    @Mock
    private MissionRepository missionRepository;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private SkillRepository skillRepository;

    @InjectMocks
    private MissionService missionService;

    // --------------------------------------------------
    // FIND BY STATUS
    // --------------------------------------------------

    @Test
    void shouldFindMissionsByStatus() {
        Client client = createClient(1L);
        Skill skill = createSkill(1L, "Java");

        Mission mission = createMission(
                10L, client, Set.of(skill)
        );

        when(missionRepository.findByStatus(MissionStatus.OPEN))
                .thenReturn(List.of(mission));

        List<MissionResponseDto> result =
                missionService.findByStatus(MissionStatus.OPEN);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getId()).isEqualTo(10L);
        assertThat(result.getFirst().getClientId()).isEqualTo(1L);
        assertThat(result.getFirst().getSkillIds()).containsExactly(1L);
        assertThat(result.getFirst().getStatus()).isEqualTo(MissionStatus.OPEN);

        verify(missionRepository).findByStatus(MissionStatus.OPEN);
    }

    @Test
    void shouldReturnEmptyListWhenNoMissionMatchesStatus() {
        when(missionRepository.findByStatus(MissionStatus.CLOSED))
                .thenReturn(List.of());

        List<MissionResponseDto> result =
                missionService.findByStatus(MissionStatus.CLOSED);

        assertThat(result).isEmpty();
    }

    // --------------------------------------------------
    // FIND BY ID
    // --------------------------------------------------

    @Test
    void shouldFindMissionById() {
        Long missionId = 10L;

        Client client = createClient(1L);
        Skill skill = createSkill(1L, "Java");

        Mission mission = createMission(
                missionId, client, Set.of(skill)
        );

        when(missionRepository.findById(missionId))
                .thenReturn(Optional.of(mission));

        MissionResponseDto result = missionService.findById(missionId);

        assertThat(result.getId()).isEqualTo(missionId);
        assertThat(result.getTitle()).isEqualTo(mission.getTitle());
        assertThat(result.getClientId()).isEqualTo(1L);
        assertThat(result.getSkillIds()).containsExactly(1L);
    }

    @Test
    void shouldThrowExceptionWhenMissionDoesNotExist() {
        Long missionId = 10L;

        when(missionRepository.findById(missionId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> missionService.findById(missionId))
                .isInstanceOf(MissionNotFoundException.class);
    }

    // --------------------------------------------------
    // CREATE
    // --------------------------------------------------

    @Test
    void shouldCreateMissionSuccessfully() {
        Long clientId = 1L;

        Client client = createClient(clientId);
        Skill java = createSkill(1L, "Java");
        Skill spring = createSkill(2L, "Spring Boot");

        MissionRequestDto request = createMissionRequest(clientId);
        request.setSkillIds(Set.of(1L, 2L));

        when(clientRepository.findById(clientId))
                .thenReturn(Optional.of(client));
        when(skillRepository.findAllById(Set.of(1L, 2L)))
                .thenReturn(List.of(java, spring));

        when(missionRepository.save(any(Mission.class)))
                .thenAnswer(invocation -> {
                    Mission mission = invocation.getArgument(0);
                    mission.setId(10L);
                    return mission;
                });

        MissionResponseDto result = missionService.create(request);

        assertThat(result.getId()).isEqualTo(10L);
        assertThat(result.getTitle()).isEqualTo(request.getTitle());
        assertThat(result.getClientId()).isEqualTo(clientId);
        assertThat(result.getSkillIds()).containsExactlyInAnyOrder(1L, 2L);
        assertThat(result.getStatus()).isEqualTo(MissionStatus.OPEN);

        verify(missionRepository).save(any(Mission.class));
    }

    @Test
    void shouldCreateMissionWithProvidedStatus() {
        Long clientId = 1L;

        Client client = createClient(clientId);
        MissionRequestDto request = createMissionRequest(clientId);
        request.setStatus(MissionStatus.CLOSED);
        request.setSkillIds(Set.of());

        when(clientRepository.findById(clientId))
                .thenReturn(Optional.of(client));
        when(skillRepository.findAllById(Set.of()))
                .thenReturn(List.of());

        when(missionRepository.save(any(Mission.class)))
                .thenAnswer(invocation -> {
                    Mission mission = invocation.getArgument(0);
                    mission.setId(10L);
                    return mission;
                });

        MissionResponseDto result = missionService.create(request);

        assertThat(result.getStatus()).isEqualTo(MissionStatus.CLOSED);
    }

    @Test
    void shouldThrowExceptionWhenClientDoesNotExistDuringCreation() {
        Long clientId = 1L;

        MissionRequestDto request = createMissionRequest(clientId);

        when(clientRepository.findById(clientId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> missionService.create(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Client not found");

        verify(missionRepository, never()).save(any());
        verify(skillRepository, never()).findAllById(any());
    }

    // --------------------------------------------------
    // UPDATE
    // --------------------------------------------------

    @Test
    void shouldUpdateMissionSuccessfully() {
        Long missionId = 10L;
        Long clientId = 1L;

        Client client = createClient(clientId);
        Skill skill = createSkill(1L, "Java");

        Mission mission = createMission(
                missionId, client, Set.of()
        );

        MissionRequestDto request = createMissionRequest(clientId);
        request.setTitle("Updated mission");
        request.setSkillIds(Set.of(1L));

        when(missionRepository.findById(missionId))
                .thenReturn(Optional.of(mission));
        when(clientRepository.findById(clientId))
                .thenReturn(Optional.of(client));
        when(skillRepository.findAllById(Set.of(1L)))
                .thenReturn(List.of(skill));
        when(missionRepository.save(any(Mission.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        MissionResponseDto result =
                missionService.update(missionId, request, clientId);

        assertThat(result.getTitle()).isEqualTo("Updated mission");
        assertThat(result.getClientId()).isEqualTo(clientId);
        assertThat(result.getSkillIds()).containsExactly(1L);

        verify(missionRepository).save(mission);
    }

    @Test
    void shouldThrowExceptionWhenMissionDoesNotExistDuringUpdate() {
        Long missionId = 10L;
        Long clientId = 1L;

        MissionRequestDto request = createMissionRequest(clientId);

        when(missionRepository.findById(missionId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                missionService.update(missionId, request, clientId))
                .isInstanceOf(MissionNotFoundException.class);

        verify(missionRepository, never()).save(any());
    }

    @Test
    void shouldRejectUpdateByAnotherClient() {
        Long missionId = 10L;
        Long ownerClientId = 1L;
        Long otherClientId = 2L;

        Client owner = createClient(ownerClientId);

        Mission mission = createMission(
                missionId, owner, Set.of()
        );

        MissionRequestDto request = createMissionRequest(otherClientId);

        when(missionRepository.findById(missionId))
                .thenReturn(Optional.of(mission));

        assertThatThrownBy(() ->
                missionService.update(missionId, request, otherClientId))
                .isInstanceOf(AccessDeniedException.class);

        verify(clientRepository, never()).findById(any());
        verify(missionRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenClientDoesNotExistDuringUpdate() {
        Long missionId = 10L;
        Long clientId = 1L;

        Client client = createClient(clientId);

        Mission mission = createMission(
                missionId, client, Set.of()
        );

        MissionRequestDto request = createMissionRequest(clientId);

        when(missionRepository.findById(missionId))
                .thenReturn(Optional.of(mission));
        when(clientRepository.findById(clientId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                missionService.update(missionId, request, clientId))
                .isInstanceOf(ClientNotFoundException.class);

        verify(missionRepository, never()).save(any());
    }

    // --------------------------------------------------
    // DELETE
    // --------------------------------------------------

    @Test
    void shouldDeleteMissionSuccessfully() {
        Long missionId = 10L;
        Long clientId = 1L;

        Client client = createClient(clientId);

        Mission mission = createMission(
                missionId, client, Set.of()
        );

        when(missionRepository.findById(missionId))
                .thenReturn(Optional.of(mission));

        missionService.delete(missionId, clientId);

        verify(missionRepository).delete(mission);
    }

    @Test
    void shouldThrowExceptionWhenMissionDoesNotExistDuringDeletion() {
        Long missionId = 10L;
        Long clientId = 1L;

        when(missionRepository.findById(missionId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                missionService.delete(missionId, clientId))
                .isInstanceOf(MissionNotFoundException.class);

        verify(missionRepository, never()).delete(any());
    }

    @Test
    void shouldRejectDeletionByAnotherClient() {
        Long missionId = 10L;
        Long ownerClientId = 1L;
        Long otherClientId = 2L;

        Client owner = createClient(ownerClientId);

        Mission mission = createMission(
                missionId, owner, Set.of()
        );

        when(missionRepository.findById(missionId))
                .thenReturn(Optional.of(mission));

        assertThatThrownBy(() ->
                missionService.delete(missionId, otherClientId))
                .isInstanceOf(AccessDeniedException.class);

        verify(missionRepository, never()).delete(any());
    }

    // --------------------------------------------------
    // TEST HELPERS
    // --------------------------------------------------

    private Client createClient(Long id) {
        Client client = new Client();
        client.setId(id);
        return client;
    }

    private Skill createSkill(Long id, String name) {
        Skill skill = new Skill();
        skill.setId(id);
        skill.setName(name);
        return skill;
    }

    private Mission createMission(
            Long id,
            Client client,
            Set<Skill> skills) {

        Mission mission = new Mission();
        mission.setId(id);
        mission.setTitle("Backend Developer");
        mission.setDescription("Develop a REST API");
        mission.setBudget(new BigDecimal("500.00"));
        mission.setLocation("Paris");
        mission.setRemote(true);
        mission.setStatus(MissionStatus.OPEN);
        mission.setClient(client);
        mission.setRequiredSkills(skills);

        return mission;
    }

    private MissionRequestDto createMissionRequest(Long clientId) {
        MissionRequestDto request = new MissionRequestDto();
        request.setClientId(clientId);
        request.setTitle("Backend Developer");
        request.setDescription("Develop a REST API");
        request.setBudget(new BigDecimal("500.00"));
        request.setLocation("Paris");
        request.setRemote(true);
        request.setSkillIds(Set.of());
        return request;
    }
}
