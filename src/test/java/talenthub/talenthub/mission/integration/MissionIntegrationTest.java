
package talenthub.talenthub.mission.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import talenthub.talenthub.mission.repository.MissionRepository;
import talenthub.talenthub.user.entity.Client;
import talenthub.talenthub.user.entity.Freelance;
import talenthub.talenthub.user.repository.UserRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class MissionIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MissionRepository missionRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Client client;
    private Client anotherClient;

    @BeforeEach
    void setUp() {

        client = createClient("client@test.com");
        anotherClient = createClient("another@test.com");

        Freelance freelance = new Freelance();
        freelance.setEmail("freelance@test.com");
        freelance.setPassword(passwordEncoder.encode("password"));
        freelance.setFirstName("Jane");
        freelance.setLastName("Doe");

        freelance = userRepository.save(freelance);
    }

    private Client createClient(String email) {
        Client client = new Client();
        client.setEmail(email);
        client.setPassword(passwordEncoder.encode("password"));
        client.setFirstName("John");
        client.setLastName("Doe");
        client.setCompanyName("Test Company");

        return userRepository.save(client);
    }

    @Test
    void shouldCreateMissionSuccessfully() throws Exception {
        long missionsBefore = missionRepository.count();
        String request = """
                {
                    "title": "Développeur Java",
                    "description": "Développement d'une API REST",
                    "budget": 500,
                    "location": "Paris",
                    "remote": true,
                    "clientId": %d,
                    "skillIds": []
                }
                """.formatted(client.getId());

        mockMvc.perform(post("/api/missions")
                        .with(httpBasic("client@test.com", "password"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isCreated());

        assertThat(missionRepository.findAll())
                .hasSize((int) missionsBefore+1)
                .anySatisfy(mission -> {
                    assertThat(mission.getTitle()).isEqualTo("Développeur Java");
                    assertThat(mission.getClient().getId()).isEqualTo(client.getId());
                });
    }

    @Test
    void shouldReturn403WhenClientCreatesMissionForAnotherClient() throws Exception {
        long missionsBefore = missionRepository.count();

        String request = """
            {
                "title": "Mission interdite",
                "description": "Test de sécurité",
                "budget": 1000,
                "location": "Paris",
                "remote": true,
                "clientId": %d,
                "skillIds": []
            }
            """.formatted(anotherClient.getId());

        mockMvc.perform(post("/api/missions")
                        .with(httpBasic(client.getEmail(), "password"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isForbidden());

        assertThat(missionRepository.count()).isEqualTo(missionsBefore);
    }

    @Test
    void shouldReturn403WhenFreelanceCreatesMission() throws Exception {

        long missionsBefore = missionRepository.count();
        String request = """
                {
                    "title": "Développeur Java",
                    "description": "Développement d'une API REST",
                    "budget": 500,
                    "clientId": %d,
                    "skillIds": []
                }
                """.formatted(client.getId());

        mockMvc.perform(post("/api/missions")
                        .with(httpBasic("freelance@test.com", "password"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isForbidden());

        assertThat(missionRepository.count()).isEqualTo(missionsBefore);
    }

    @Test
    void shouldReturn401WhenUserIsNotAuthenticated() throws Exception {

        mockMvc.perform(post("/api/missions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturn400WhenMissionTitleIsMissing() throws Exception {

        String request = """
                {
                    "description": "Développement d'une API REST",
                    "budget": 500,
                    "clientId": %d,
                    "skillIds": []
                }
                """.formatted(client.getId());

        mockMvc.perform(post("/api/missions")
                        .with(httpBasic("client@test.com", "password"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest());
    }
}
