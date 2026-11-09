
package talenthub.talenthub.user.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import talenthub.talenthub.security.UserPrincipal;
import talenthub.talenthub.user.dto.FreelanceRegistrationRequestDto;
import talenthub.talenthub.user.dto.FreelanceRequestDto;
import talenthub.talenthub.user.dto.FreelanceResponseDto;
import talenthub.talenthub.user.entity.User;
import talenthub.talenthub.user.service.FreelanceService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FreelanceControllerTest {

    @Mock
    private FreelanceService freelanceService;

    @InjectMocks
    private FreelanceController freelanceController;

    private UserPrincipal createPrincipal() {
        UserPrincipal principal = mock(UserPrincipal.class);

        User user = mock(User.class);
        when(user.getId()).thenReturn(1L);
        when(principal.getUser()).thenReturn(user);

        return principal;
    }

    @Test
    void shouldFindOwnFreelanceProfile() {
        UserPrincipal principal = createPrincipal();
        FreelanceResponseDto response = new FreelanceResponseDto();
        response.setId(1L);

        when(freelanceService.findById(1L)).thenReturn(response);

        var result = freelanceController.findById(1L, principal);

        assertThat(result.getId()).isEqualTo(1L);
        verify(freelanceService).findById(1L);
    }

    @Test
    void shouldDenyAccessToAnotherFreelanceProfile() {
        UserPrincipal principal = createPrincipal();
        assertThatThrownBy(() -> freelanceController.findById(2L, principal))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("You cannot access another freelance profile.");

        verify(freelanceService, never()).findById(any());
    }

    @Test
    void shouldUpdateOwnFreelanceProfile() {
        UserPrincipal principal = createPrincipal();
        FreelanceRequestDto request = new FreelanceRequestDto();

        FreelanceResponseDto response = new FreelanceResponseDto();
        response.setId(1L);

        when(freelanceService.update(1L, request)).thenReturn(response);

        var result = freelanceController.update(1L, request, principal);

        assertThat(result.getId()).isEqualTo(1L);
        verify(freelanceService).update(1L, request);
    }

    @Test
    void shouldDenyUpdateOfAnotherFreelanceProfile() {
        UserPrincipal principal = createPrincipal();
        FreelanceRequestDto request = new FreelanceRequestDto();

        assertThatThrownBy(() -> freelanceController.update(2L, request, principal))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("You cannot update another freelance profile.");

        verify(freelanceService, never()).update(any(), any());
    }

    @Test
    void shouldFindAllFreelances() {
        FreelanceResponseDto freelance1 = new FreelanceResponseDto();
        freelance1.setId(1L);

        FreelanceResponseDto freelance2 = new FreelanceResponseDto();
        freelance2.setId(2L);

        when(freelanceService.findAll()).thenReturn(List.of(freelance1, freelance2));

        var result = freelanceController.findAll();

        assertThat(result).hasSize(2);
        assertThat(result).extracting(FreelanceResponseDto::getId)
                .containsExactly(1L, 2L);

        verify(freelanceService).findAll();
    }

    @Test
    void shouldReturnEmptyListWhenNoFreelancesExist() {
        when(freelanceService.findAll()).thenReturn(List.of());

        var result = freelanceController.findAll();

        assertThat(result).isEmpty();
        verify(freelanceService).findAll();
    }

    @Test
    void shouldRegisterFreelance() {
        FreelanceRegistrationRequestDto request = new FreelanceRegistrationRequestDto();

        FreelanceResponseDto response = new FreelanceResponseDto();
        response.setId(1L);

        when(freelanceService.register(request)).thenReturn(response);

        var result = freelanceController.register(request);

        assertThat(result.getId()).isEqualTo(1L);
        verify(freelanceService).register(request);
    }
}
