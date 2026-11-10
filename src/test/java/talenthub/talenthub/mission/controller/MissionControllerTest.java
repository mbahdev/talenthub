package talenthub.talenthub.mission.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import talenthub.talenthub.mission.dto.MissionRequestDto;
import talenthub.talenthub.mission.dto.MissionResponseDto;
import talenthub.talenthub.mission.entity.MissionStatus;
import talenthub.talenthub.mission.service.MissionService;
import talenthub.talenthub.security.UserPrincipal;
import talenthub.talenthub.user.entity.User;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MissionControllerTest {

    @Mock
    private MissionService missionService;

    @InjectMocks
    private MissionController missionController;

    private UserPrincipal createPrincipal() {
        UserPrincipal principal = mock(UserPrincipal.class);
        User user = mock(User.class);

        lenient().when(user.getId()).thenReturn(1L);
        when(principal.getUser()).thenReturn(user);

        return principal;
    }

    @Test
    void findByStatusShouldReturnMissions() {
        MissionResponseDto response = new MissionResponseDto();
        List<MissionResponseDto> missions = List.of(response);

        when(missionService.findByStatus(MissionStatus.OPEN))
                .thenReturn(missions);

        List<MissionResponseDto> result =
                missionController.findByStatus(MissionStatus.OPEN);

        assertThat(result).isEqualTo(missions);

        verify(missionService).findByStatus(MissionStatus.OPEN);
    }

    @Test
    void createShouldReturnCreatedMission() {
        Long clientId = 1L;

        MissionRequestDto request = new MissionRequestDto();
        MissionResponseDto response = new MissionResponseDto();

        UserPrincipal principal = mock(UserPrincipal.class);
        User user = mock(User.class);

        when(principal.getUser()).thenReturn(user);
        when(user.getId()).thenReturn(clientId);
        when(missionService.create(request, clientId)).thenReturn(response);

        MissionResponseDto result = missionController.create(request, principal);

        assertThat(result).isSameAs(response);

        verify(missionService).create(request, clientId);
    }

    @Test
    void findByIdShouldReturnMission() {
        MissionResponseDto response = new MissionResponseDto();

        when(missionService.findById(10L)).thenReturn(response);

        MissionResponseDto result = missionController.findById(10L);

        assertThat(result).isSameAs(response);

        verify(missionService).findById(10L);
    }

    @Test
    void updateShouldReturnUpdatedMission() {
        UserPrincipal principal = createPrincipal();
        MissionRequestDto request = new MissionRequestDto();
        MissionResponseDto response = new MissionResponseDto();

        when(missionService.update(10L, request, 1L))
                .thenReturn(response);

        MissionResponseDto result =
                missionController.update(10L, request, principal);

        assertThat(result).isSameAs(response);

        verify(missionService).update(10L, request, 1L);
    }

    @Test
    void deleteShouldCallService() {
        UserPrincipal principal = createPrincipal();

        missionController.delete(10L, principal);

        verify(missionService).delete(10L, 1L);
    }
}