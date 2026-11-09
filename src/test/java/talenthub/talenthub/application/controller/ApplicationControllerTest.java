package talenthub.talenthub.application.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.access.AccessDeniedException;

import talenthub.talenthub.application.dto.ApplicationRequestDto;
import talenthub.talenthub.application.dto.ApplicationResponseDto;
import talenthub.talenthub.application.service.ApplicationService;
import talenthub.talenthub.security.UserPrincipal;
import talenthub.talenthub.user.entity.User;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApplicationControllerTest {

    @Mock
    private ApplicationService applicationService;

    @InjectMocks
    private ApplicationController applicationController;

    private UserPrincipal createPrincipal() {
        UserPrincipal principal = mock(UserPrincipal.class);
        User user = mock(User.class);

        lenient().when(user.getId()).thenReturn(1L);
        when(principal.getUser()).thenReturn(user);

        return principal;
    }

    @Test
    void findByIdShouldReturnApplication() {
        UserPrincipal principal = createPrincipal();
        User user = principal.getUser();
        ApplicationResponseDto response = new ApplicationResponseDto();

        when(applicationService.findById(10L, user))
                .thenReturn(response);

        ApplicationResponseDto result =
                applicationController.findById(10L, principal);

        assertThat(result).isSameAs(response);

        verify(applicationService).findById(10L, user);
    }

    @Test
    void findAllShouldReturnApplications() {
        UserPrincipal principal = createPrincipal();
        ApplicationResponseDto response = new ApplicationResponseDto();

        List<ApplicationResponseDto> applications = List.of(response);

        when(applicationService.findByFreelanceId(1L, 1L))
                .thenReturn(applications);

        List<ApplicationResponseDto> result =
                applicationController.findAll(principal);

        assertThat(result).isEqualTo(applications);

        verify(applicationService).findByFreelanceId(1L, 1L);
    }

    @Test
    void createShouldReturnCreatedApplication() {
        UserPrincipal principal = createPrincipal();
        ApplicationRequestDto request = new ApplicationRequestDto();
        ApplicationResponseDto response = new ApplicationResponseDto();

        when(applicationService.create(request, 1L))
                .thenReturn(response);

        ApplicationResponseDto result =
                applicationController.create(request, principal);

        assertThat(result).isSameAs(response);

        verify(applicationService).create(request, 1L);
    }

    @Test
    void withdrawShouldReturnUpdatedApplication() {
        UserPrincipal principal = createPrincipal();
        ApplicationResponseDto response = new ApplicationResponseDto();

        when(applicationService.withdraw(10L, 1L))
                .thenReturn(response);

        ApplicationResponseDto result =
                applicationController.withdraw(10L, principal);

        assertThat(result).isSameAs(response);

        verify(applicationService).withdraw(10L, 1L);
    }

    @Test
    void acceptShouldReturnUpdatedApplication() {
        UserPrincipal principal = createPrincipal();
        ApplicationResponseDto response = new ApplicationResponseDto();

        when(applicationService.accept(10L, 1L))
                .thenReturn(response);

        ApplicationResponseDto result =
                applicationController.accept(10L, principal);

        assertThat(result).isSameAs(response);

        verify(applicationService).accept(10L, 1L);
    }

    @Test
    void rejectShouldReturnUpdatedApplication() {
        UserPrincipal principal = createPrincipal();
        ApplicationResponseDto response = new ApplicationResponseDto();

        when(applicationService.reject(10L, 1L))
                .thenReturn(response);

        ApplicationResponseDto result =
                applicationController.reject(10L, principal);

        assertThat(result).isSameAs(response);

        verify(applicationService).reject(10L, 1L);
    }

    @Test
    void findByFreelanceIdShouldReturnApplications() {
        UserPrincipal principal = createPrincipal();
        ApplicationResponseDto response = new ApplicationResponseDto();

        List<ApplicationResponseDto> applications = List.of(response);

        when(applicationService.findByFreelanceId(2L, 1L))
                .thenReturn(applications);

        List<ApplicationResponseDto> result =
                applicationController.findByFreelanceId(2L, principal);

        assertThat(result).isEqualTo(applications);

        verify(applicationService).findByFreelanceId(2L, 1L);
    }

    @Test
    void findByMissionIdShouldReturnApplications() {
        UserPrincipal principal = createPrincipal();
        ApplicationResponseDto response = new ApplicationResponseDto();

        List<ApplicationResponseDto> applications = List.of(response);

        when(applicationService.findByMissionId(5L, 1L))
                .thenReturn(applications);

        List<ApplicationResponseDto> result =
                applicationController.findByMissionId(5L, principal);

        assertThat(result).isEqualTo(applications);

        verify(applicationService).findByMissionId(5L, 1L);
    }
}