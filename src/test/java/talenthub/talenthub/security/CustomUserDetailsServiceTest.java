
package talenthub.talenthub.security;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import talenthub.talenthub.user.entity.Client;
import talenthub.talenthub.user.entity.Freelance;
import talenthub.talenthub.user.repository.UserRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void shouldLoadClientByUsername() {
        Client client = new Client();
        client.setEmail("client@test.com");
        client.setPassword("encodedPassword");
        client.setActive(true);

        when(userRepository.findByEmail("client@test.com")).thenReturn(Optional.of(client));

        UserDetails userDetails = customUserDetailsService.loadUserByUsername("client@test.com");

        assertThat(userDetails).isInstanceOf(UserPrincipal.class);
        assertThat(userDetails.getUsername()).isEqualTo("client@test.com");
        assertThat(userDetails.getPassword()).isEqualTo("encodedPassword");
        assertThat(userDetails.isEnabled()).isTrue();

        verify(userRepository).findByEmail("client@test.com");
    }

    @Test
    void shouldLoadFreelanceByUsername() {
        Freelance freelance = new Freelance();
        freelance.setEmail("freelance@test.com");
        freelance.setPassword("encodedPassword");
        freelance.setActive(true);

        when(userRepository.findByEmail("freelance@test.com")).thenReturn(Optional.of(freelance));

        UserDetails userDetails = customUserDetailsService.loadUserByUsername("freelance@test.com");

        assertThat(userDetails).isInstanceOf(UserPrincipal.class);
        assertThat(userDetails.getUsername()).isEqualTo("freelance@test.com");
        assertThat(userDetails.getPassword()).isEqualTo("encodedPassword");
        assertThat(userDetails.isEnabled()).isTrue();

        verify(userRepository).findByEmail("freelance@test.com");
    }

    @Test
    void shouldThrowUsernameNotFoundExceptionWhenUserNotFound() {
        when(userRepository.findByEmail("unknown@test.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> customUserDetailsService.loadUserByUsername("unknown@test.com"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("User not found with email: unknown@test.com");

        verify(userRepository).findByEmail("unknown@test.com");
    }

    @Test
    void shouldReturnDisabledUserWhenUserIsInactive() {
        Client client = new Client();
        client.setEmail("inactive@test.com");
        client.setPassword("encodedPassword");
        client.setActive(false);

        when(userRepository.findByEmail("inactive@test.com")).thenReturn(Optional.of(client));

        UserDetails userDetails = customUserDetailsService.loadUserByUsername("inactive@test.com");

        assertThat(userDetails).isInstanceOf(UserPrincipal.class);
        assertThat(userDetails.isEnabled()).isFalse();
    }

    @Test
    void shouldCallFindByEmailWithCorrectEmail() {
        Freelance freelance = new Freelance();
        freelance.setEmail("test@example.com");
        freelance.setPassword("password");

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(freelance));

        customUserDetailsService.loadUserByUsername("test@example.com");

        verify(userRepository).findByEmail("test@example.com");
        verifyNoMoreInteractions(userRepository);
    }
}
