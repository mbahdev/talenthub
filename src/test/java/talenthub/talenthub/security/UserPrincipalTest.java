
package talenthub.talenthub.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import talenthub.talenthub.user.entity.Client;
import talenthub.talenthub.user.entity.Freelance;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

class UserPrincipalTest {

    @Test
    void shouldReturnRoleClientForClientUser() {
        Client client = new Client();
        client.setEmail("client@test.com");
        client.setPassword("password");
        client.setActive(true);

        UserPrincipal principal = new UserPrincipal(client);

        Collection<? extends GrantedAuthority> authorities = principal.getAuthorities();

        assertThat(authorities).hasSize(1);
        assertThat(authorities.iterator().next().getAuthority()).isEqualTo("ROLE_CLIENT");
    }

    @Test
    void shouldReturnRoleFreelanceForFreelanceUser() {
        Freelance freelance = new Freelance();
        freelance.setEmail("freelance@test.com");
        freelance.setPassword("password");
        freelance.setActive(true);

        UserPrincipal principal = new UserPrincipal(freelance);

        Collection<? extends GrantedAuthority> authorities = principal.getAuthorities();

        assertThat(authorities).hasSize(1);
        assertThat(authorities.iterator().next().getAuthority()).isEqualTo("ROLE_FREELANCE");
    }

    @Test
    void shouldReturnEmptyAuthoritiesForUnknownUserType() {
        UserPrincipal principal = new UserPrincipal(new AbstractUser());

        Collection<? extends GrantedAuthority> authorities = principal.getAuthorities();

        assertThat(authorities).isEmpty();
    }

    @Test
    void shouldReturnPasswordFromUser() {
        Client client = new Client();
        client.setPassword("encodedPassword");

        UserPrincipal principal = new UserPrincipal(client);

        assertThat(principal.getPassword()).isEqualTo("encodedPassword");
    }

    @Test
    void shouldReturnEmailAsUsername() {
        Freelance freelance = new Freelance();
        freelance.setEmail("freelance@example.com");

        UserPrincipal principal = new UserPrincipal(freelance);

        assertThat(principal.getUsername()).isEqualTo("freelance@example.com");
    }

    @Test
    void shouldReturnTrueWhenUserIsActive() {
        Client client = new Client();
        client.setActive(true);

        UserPrincipal principal = new UserPrincipal(client);

        assertThat(principal.isEnabled()).isTrue();
    }

    @Test
    void shouldReturnFalseWhenUserIsInactive() {
        Freelance freelance = new Freelance();
        freelance.setActive(false);

        UserPrincipal principal = new UserPrincipal(freelance);

        assertThat(principal.isEnabled()).isFalse();
    }

    @Test
    void shouldReturnUserObject() {
        Client client = new Client();
        client.setEmail("client@test.com");

        UserPrincipal principal = new UserPrincipal(client);

        assertThat(principal.getUser()).isSameAs(client);
        assertThat(principal.getUser().getEmail()).isEqualTo("client@test.com");
    }

    // Classe anonyme pour simuler un User de type inconnu
    private static class AbstractUser extends talenthub.talenthub.user.entity.User {
        // Pas de @Entity — juste pour les tests
    }
}
