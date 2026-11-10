
package talenthub.talenthub.favorite.integration;

import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import talenthub.talenthub.favorite.entity.Favorite;
import talenthub.talenthub.favorite.repository.FavoriteRepository;
import talenthub.talenthub.mission.entity.Mission;
import talenthub.talenthub.mission.entity.MissionStatus;
import talenthub.talenthub.mission.repository.MissionRepository;
import talenthub.talenthub.user.entity.Client;
import talenthub.talenthub.user.entity.Freelance;
import talenthub.talenthub.user.repository.ClientRepository;
import talenthub.talenthub.user.repository.FreelanceRepository;

import java.math.BigDecimal;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class FavoriteIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private FreelanceRepository freelanceRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private MissionRepository missionRepository;

    @Autowired
    private FavoriteRepository favoriteRepository;

    private Freelance freelance;
    private Freelance anotherFreelance;
    private Mission mission;

    @BeforeEach
    void setUp() {
        freelance = createFreelance("freelance@test.com");
        anotherFreelance = createFreelance("another@test.com");

        Client client = createClient();
        mission = createMission(client);
    }

    @Test
    void shouldCreateFavoriteSuccessfully() throws Exception {
        String request = """
                {
                    "freelanceId": %d,
                    "missionId": %d
                }
                """.formatted(freelance.getId(), mission.getId());

        long favoritesBefore = favoriteRepository.count();

        mockMvc.perform(post("/api/favorites")
                        .with(httpBasic("freelance@test.com", "password"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.freelanceId").value(freelance.getId()))
                .andExpect(jsonPath("$.missionId").value(mission.getId()));

        assertThat(favoriteRepository.count()).isEqualTo(favoritesBefore + 1);
    }

    @Test
    void shouldRejectCreatingFavoriteForAnotherFreelance() throws Exception {
        String request = """
                {
                    "freelanceId": %d,
                    "missionId": %d
                }
                """.formatted(anotherFreelance.getId(), mission.getId());

        long favoritesBefore = favoriteRepository.count();

        mockMvc.perform(post("/api/favorites")
                        .with(httpBasic("freelance@test.com", "password"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isForbidden());

        assertThat(favoriteRepository.count()).isEqualTo(favoritesBefore);
    }

    @Test
    void shouldFindFavoritesByFreelanceId() throws Exception {
        createFavorite(freelance, mission);

        mockMvc.perform(get("/api/favorites/freelance/" + freelance.getId())
                        .with(httpBasic("freelance@test.com", "password")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].missionId")
                        .value(org.hamcrest.Matchers.hasItem(
                                mission.getId().intValue()
                        )));
    }

    @Test
    void shouldRejectAccessingAnotherFreelancesFavorites() throws Exception {
        mockMvc.perform(get("/api/favorites/freelance/" + anotherFreelance.getId())
                        .with(httpBasic("freelance@test.com", "password")))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldDeleteFavoriteSuccessfully() throws Exception {
        createFavorite(freelance, mission);
        long favoritesBefore = favoriteRepository.count();

        mockMvc.perform(delete("/api/favorites/freelance/"
                        + freelance.getId()
                        + "/mission/"
                        + mission.getId())
                        .with(httpBasic("freelance@test.com", "password")))
                .andExpect(status().isOk());

        assertThat(favoriteRepository.count()).isEqualTo(favoritesBefore - 1);
    }

    @Test
    void shouldRejectDeletingAnotherFreelancesFavorite() throws Exception {
        createFavorite(anotherFreelance, mission);

        long favoritesBefore = favoriteRepository.count();

        mockMvc.perform(delete("/api/favorites/freelance/"
                        + anotherFreelance.getId()
                        + "/mission/"
                        + mission.getId())
                        .with(httpBasic("freelance@test.com", "password")))
                .andExpect(status().isForbidden());

        assertThat(favoriteRepository.count()).isEqualTo(favoritesBefore);
    }

    private Freelance createFreelance(String email) {
        Freelance freelance = new Freelance();
        freelance.setEmail(email);
        freelance.setPassword(passwordEncoder.encode("password"));
        freelance.setFirstName("Test");
        freelance.setLastName("Freelance");
        freelance.setDailyRate(new BigDecimal("400.00"));
        freelance.setActive(true);

        return freelanceRepository.save(freelance);
    }

    private Client createClient() {
        Client client = new Client();
        client.setEmail("client@test.com");
        client.setPassword(passwordEncoder.encode("password"));
        client.setFirstName("Test");
        client.setLastName("Client");
        client.setActive(true);

        return clientRepository.save(client);
    }

    private Mission createMission(Client client) {
        Mission mission = new Mission();
        mission.setTitle("Développeur Java");
        mission.setDescription("Mission de développement backend");
        mission.setBudget(new BigDecimal("500.00"));
        mission.setRemote(true);
        mission.setStatus(MissionStatus.OPEN);
        mission.setClient(client);

        return missionRepository.save(mission);
    }

    private Favorite createFavorite(Freelance freelance, Mission mission) {
        Favorite favorite = new Favorite();
        favorite.setFreelance(freelance);
        favorite.setMission(mission);

        return favoriteRepository.save(favorite);
    }
}
