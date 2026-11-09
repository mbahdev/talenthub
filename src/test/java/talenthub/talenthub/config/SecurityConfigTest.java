
package talenthub.talenthub.config;

import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import talenthub.talenthub.security.CustomUserDetailsService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class SecurityConfigTest {

    private final SecurityConfig securityConfig = new SecurityConfig();

    @Test
    void shouldCreateBCryptPasswordEncoderBean() {
        PasswordEncoder passwordEncoder = securityConfig.passwordEncoder();

        assertThat(passwordEncoder).isNotNull();
        assertThat(passwordEncoder).isInstanceOf(BCryptPasswordEncoder.class);
    }

    @Test
    void passwordEncoderShouldEncodeAndVerifyPassword() {
        PasswordEncoder passwordEncoder = securityConfig.passwordEncoder();
        String rawPassword = "securePassword123";
        String encoded = passwordEncoder.encode(rawPassword);

        assertThat(encoded).isNotEqualTo(rawPassword);
        assertThat(passwordEncoder.matches(rawPassword, encoded)).isTrue();
        assertThat(passwordEncoder.matches("wrongPassword", encoded)).isFalse();
    }

    @Test
    void shouldCreateAuthenticationProviderWithCorrectConfig() {
        CustomUserDetailsService userDetailsService = mock(CustomUserDetailsService.class);
        PasswordEncoder passwordEncoder = securityConfig.passwordEncoder();

        DaoAuthenticationProvider provider = securityConfig.authenticationProvider(userDetailsService, passwordEncoder);

        assertThat(provider).isNotNull();
        assertThat(provider).isInstanceOf(DaoAuthenticationProvider.class);
    }
}
