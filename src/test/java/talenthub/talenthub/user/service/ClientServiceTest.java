
package talenthub.talenthub.user.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import talenthub.talenthub.user.dto.ClientRegistrationRequestDto;
import talenthub.talenthub.user.dto.ClientRequestDto;
import talenthub.talenthub.user.entity.Client;
import talenthub.talenthub.user.exception.ClientNotFoundException;
import talenthub.talenthub.user.exception.EmailAlreadyExistsException;
import talenthub.talenthub.user.repository.ClientRepository;
import talenthub.talenthub.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private ClientService clientService;

    @Test
    void shouldFindClientById() {
        Client client = createClient(1L);

        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));

        var response = clientService.findById(1L);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getEmail()).isEqualTo("client@test.com");
        assertThat(response.getFirstName()).isEqualTo("John");
        assertThat(response.getCompanyName()).isEqualTo("Test Company");

        verify(clientRepository).findById(1L);
    }

    @Test
    void shouldThrowExceptionWhenClientDoesNotExist() {
        when(clientRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> clientService.findById(1L))
                .isInstanceOf(ClientNotFoundException.class);

        verify(clientRepository).findById(1L);
    }

    @Test
    void shouldUpdateClientSuccessfully() {
        Client client = createClient(1L);

        ClientRequestDto request = new ClientRequestDto();
        request.setFirstName("Jane");
        request.setLastName("Smith");
        request.setPhone("0600000000");
        request.setCompanyName("New Company");
        request.setCompanyDescription("New description");
        request.setLocation("Paris");

        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(clientRepository.save(any(Client.class))).thenAnswer(invocation ->
                invocation.getArgument(0));

        var response = clientService.update(1L, request);

        assertThat(response.getFirstName()).isEqualTo("Jane");
        assertThat(response.getLastName()).isEqualTo("Smith");
        assertThat(response.getPhone()).isEqualTo("0600000000");
        assertThat(response.getCompanyName()).isEqualTo("New Company");
        assertThat(response.getCompanyDescription()).isEqualTo("New description");
        assertThat(response.getLocation()).isEqualTo("Paris");

        verify(clientRepository).save(client);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistingClient() {
        ClientRequestDto request = new ClientRequestDto();

        when(clientRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> clientService.update(1L, request))
                .isInstanceOf(ClientNotFoundException.class);

        verify(clientRepository, never()).save(any());
    }

    @Test
    void shouldFindAllClients() {
        Client client1 = createClient(1L);
        Client client2 = createClient(2L);
        client2.setEmail("client2@test.com");

        when(clientRepository.findAll()).thenReturn(List.of(client1, client2));

        var responses = clientService.findAll();

        assertThat(responses).hasSize(2);
        assertThat(responses)
                .extracting(response -> response.getId())
                .containsExactly(1L, 2L);

        verify(clientRepository).findAll();
    }

    @Test
    void shouldReturnEmptyListWhenNoClientsExist() {
        when(clientRepository.findAll()).thenReturn(List.of());

        var responses = clientService.findAll();

        assertThat(responses).isEmpty();

        verify(clientRepository).findAll();
    }

    @Test
    void shouldRegisterClientSuccessfully() {
        ClientRegistrationRequestDto request = createRegistrationRequest();

        when(userRepository.existsByEmail("client@test.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");

        when(clientRepository.save(any(Client.class))).thenAnswer(invocation -> {
            Client client = invocation.getArgument(0);
            client.setId(1L);
            return client;
        });

        var response = clientService.register(request);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getEmail()).isEqualTo("client@test.com");
        assertThat(response.getFirstName()).isEqualTo("John");
        assertThat(response.getCompanyName()).isEqualTo("Test Company");

        verify(userRepository).existsByEmail("client@test.com");
        verify(passwordEncoder).encode("password123");
        verify(clientRepository).save(any(Client.class));
    }

    @Test
    void shouldThrowExceptionWhenRegisteringWithExistingEmail() {
        ClientRegistrationRequestDto request = createRegistrationRequest();

        when(userRepository.existsByEmail("client@test.com")).thenReturn(true);

        assertThatThrownBy(() -> clientService.register(request))
                .isInstanceOf(EmailAlreadyExistsException.class);

        verify(passwordEncoder, never()).encode(anyString());
        verify(clientRepository, never()).save(any());
    }

    private Client createClient(Long id) {
        Client client = new Client();
        client.setId(id);
        client.setEmail("client@test.com");
        client.setFirstName("John");
        client.setLastName("Doe");
        client.setPhone("0612345678");
        client.setCompanyName("Test Company");
        client.setCompanyDescription("Test description");
        client.setLocation("Paris");
        return client;
    }

    private ClientRegistrationRequestDto createRegistrationRequest() {
        ClientRegistrationRequestDto request = new ClientRegistrationRequestDto();
        request.setEmail("client@test.com");
        request.setPassword("password123");
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setPhone("0612345678");
        request.setCompanyName("Test Company");
        request.setCompanyDescription("Test description");
        request.setLocation("Paris");
        return request;
    }
}
