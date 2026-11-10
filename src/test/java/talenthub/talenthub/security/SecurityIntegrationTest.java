
package talenthub.talenthub.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import talenthub.talenthub.user.entity.Client;
import talenthub.talenthub.user.entity.Freelance;
import talenthub.talenthub.user.repository.UserRepository;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest	//Démarre le contexte complet de Spring Boot : configuration, services, repositories, sécurité, etc.
@AutoConfigureMockMvc	//Configure automatiquement MockMvc pour simuler des requêtes HTTP sans démarrer un vrai serveur web.
@Transactional	//Exécute chaque test dans une transaction qui est annulée à la fin du test.
class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Client client;

    @BeforeEach
    void setUp() {
        client = new Client();
        client.setEmail("client@test.com");
        client.setPassword(passwordEncoder.encode("password"));
        client.setFirstName("John");
        client.setLastName("Doe");

        client = userRepository.save(client);

        Freelance freelance = new Freelance();
        freelance.setEmail("freelance@test.com");
        freelance.setPassword(passwordEncoder.encode("password"));
        freelance.setFirstName("Jane");
        freelance.setLastName("Doe");

        userRepository.save(freelance);
    }

    @Test
    void shouldReturn401WhenUserIsNotAuthenticated() throws Exception {
        mockMvc.perform(get("/api/clients/" + client.getId()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturn403WhenFreelanceAccessesClientRoute() throws Exception {
        mockMvc.perform(get("/api/clients/" + client.getId())
                        .with(httpBasic("freelance@test.com", "password")))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldAllowClientToAccessOwnProfile() throws Exception {
        mockMvc.perform(get("/api/clients/" + client.getId())
                        .with(httpBasic("client@test.com", "password")))
                .andExpect(status().isOk());
    }
}
