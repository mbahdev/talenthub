
package talenthub.talenthub.user.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import talenthub.talenthub.security.UserPrincipal;
import talenthub.talenthub.user.dto.ClientRegistrationRequestDto;
import talenthub.talenthub.user.dto.ClientRequestDto;
import talenthub.talenthub.user.dto.ClientResponseDto;
import talenthub.talenthub.user.entity.User;
import talenthub.talenthub.user.service.ClientService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientControllerTest {

    @Mock
    private ClientService clientService;

    @InjectMocks
    private ClientController clientController;

    private UserPrincipal createPrincipal(Long userId) {
        UserPrincipal principal = mock(UserPrincipal.class);

        User user = mock(User.class);
        when(user.getId()).thenReturn(userId);
        when(principal.getUser()).thenReturn(user);

        return principal;
    }

    @Test
    void shouldFindOwnClientProfile() {

        UserPrincipal principal = createPrincipal(1L);

        ClientResponseDto response = new ClientResponseDto();
        response.setId(1L);

        when(clientService.findById(1L)).thenReturn(response);

        var result = clientController.findById(1L, principal);

        assertThat(result.getId()).isEqualTo(1L);
        verify(clientService).findById(1L);
    }

    @Test
    void shouldDenyAccessToAnotherClientProfile() {
        UserPrincipal principal = createPrincipal(1L);
        assertThatThrownBy(() -> clientController.findById(2L, principal))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("You cannot access another client profile.");

        verify(clientService, never()).findById(any());
    }

    @Test
    void shouldUpdateOwnClientProfile() {
        UserPrincipal principal = createPrincipal(1L);
        ClientRequestDto request = new ClientRequestDto();

        ClientResponseDto response = new ClientResponseDto();
        response.setId(1L);

        when(clientService.update(1L, request)).thenReturn(response);

        var result = clientController.update(1L, request, principal);

        assertThat(result.getId()).isEqualTo(1L);
        verify(clientService).update(1L, request);
    }

    @Test
    void shouldDenyUpdateOfAnotherClientProfile() {
        UserPrincipal principal = createPrincipal(1L);
        ClientRequestDto request = new ClientRequestDto();

        assertThatThrownBy(() -> clientController.update(2L, request, principal))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("You cannot modify another client profile.");

        verify(clientService, never()).update(any(), any());
    }

    @Test
    void shouldFindAllClients() {
        ClientResponseDto client1 = new ClientResponseDto();
        client1.setId(1L);

        ClientResponseDto client2 = new ClientResponseDto();
        client2.setId(2L);

        when(clientService.findAll()).thenReturn(List.of(client1, client2));

        var result = clientController.findAll();

        assertThat(result).hasSize(2);
        assertThat(result).extracting(ClientResponseDto::getId)
                .containsExactly(1L, 2L);

        verify(clientService).findAll();
    }

    @Test
    void shouldReturnEmptyListWhenNoClientsExist() {
        when(clientService.findAll()).thenReturn(List.of());

        var result = clientController.findAll();

        assertThat(result).isEmpty();
        verify(clientService).findAll();
    }

    @Test
    void shouldRegisterClient() {
        ClientRegistrationRequestDto request = new ClientRegistrationRequestDto();

        ClientResponseDto response = new ClientResponseDto();
        response.setId(1L);

        when(clientService.register(request)).thenReturn(response);

        var result = clientController.register(request);

        assertThat(result.getId()).isEqualTo(1L);
        verify(clientService).register(request);
    }
}
