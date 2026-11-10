
package talenthub.talenthub.application.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import talenthub.talenthub.application.entity.Application;
import talenthub.talenthub.application.entity.ApplicationStatus;
import talenthub.talenthub.application.repository.ApplicationRepository;
import talenthub.talenthub.mission.entity.Mission;
import talenthub.talenthub.mission.entity.MissionStatus;
import talenthub.talenthub.mission.repository.MissionRepository;
import talenthub.talenthub.user.entity.Client;
import talenthub.talenthub.user.entity.Freelance;
import talenthub.talenthub.user.repository.UserRepository;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ApplicationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MissionRepository missionRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Client client;
    private Freelance freelance;
    private Freelance anotherFreelance;
    private Mission mission;

    @BeforeEach
    void setUp() {
        client = createClient("client@test.com");
        freelance = createFreelance("freelance@test.com");
        anotherFreelance = createFreelance("another@test.com");

        mission = createMission(client, MissionStatus.OPEN);
    }

    @Test
    void shouldCreateApplicationSuccessfully() throws Exception {
        long applicationsBefore = applicationRepository.count();

        String request = """
                {
                    "coverLetter": "Je suis intéressé par cette mission.",
                    "proposedPrice": 450,
                    "missionId": %d
                }
                """.formatted(mission.getId());

        mockMvc.perform(post("/api/applications")
                        .with(httpBasic(freelance.getEmail(), "password"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.missionId").value(mission.getId()))
                .andExpect(jsonPath("$.freelanceId").value(freelance.getId()));

        assertThat(applicationRepository.count()).isEqualTo(applicationsBefore + 1);
    }

    @Test
    void shouldReturn403WhenClientCreatesApplication() throws Exception {

        long before = applicationRepository.count();
        String request = """
                {
                    "coverLetter": "Je suis intéressé par cette mission.",
                    "proposedPrice": 450,
                    "missionId": %d
                }
                """.formatted(mission.getId());

        mockMvc.perform(post("/api/applications")
                        .with(httpBasic(client.getEmail(), "password"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isForbidden());

        assertThat(applicationRepository.count()).isEqualTo(before);
    }

    @Test
    void shouldWithdrawApplicationSuccessfully() throws Exception {
        Application application = createApplication(freelance, mission);

        mockMvc.perform(patch("/api/applications/{id}/withdraw", application.getId())
                        .with(httpBasic(freelance.getEmail(), "password")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("WITHDRAWN"));

        Application updated = applicationRepository.findById(application.getId())
                .orElseThrow();

        assertThat(updated.getStatus()).isEqualTo(ApplicationStatus.WITHDRAWN);
    }

    @Test
    void shouldAcceptApplicationAndRejectOthers() throws Exception {
        Application application = createApplication(freelance, mission);
        Application anotherApplication = createApplication(anotherFreelance, mission);

        mockMvc.perform(patch("/api/applications/{id}/accept", application.getId())
                        .with(httpBasic(client.getEmail(), "password")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACCEPTED"));

        Application accepted = applicationRepository.findById(application.getId())
                .orElseThrow();

        Application rejected = applicationRepository.findById(anotherApplication.getId())
                .orElseThrow();

        Mission updatedMission = missionRepository.findById(mission.getId())
                .orElseThrow();

        assertThat(accepted.getStatus()).isEqualTo(ApplicationStatus.ACCEPTED);
        assertThat(rejected.getStatus()).isEqualTo(ApplicationStatus.REJECTED);
        assertThat(updatedMission.getStatus()).isEqualTo(MissionStatus.CLOSED);
    }

    @Test
    void shouldRejectApplicationSuccessfully() throws Exception {
        Application application = createApplication(freelance, mission);

        mockMvc.perform(patch("/api/applications/{id}/reject", application.getId())
                        .with(httpBasic(client.getEmail(), "password")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("REJECTED"));

        Application updated = applicationRepository.findById(application.getId())
                .orElseThrow();

        assertThat(updated.getStatus()).isEqualTo(ApplicationStatus.REJECTED);
    }

    @Test
    void shouldReturn403WhenFreelanceAcceptsApplication() throws Exception {
        Application application = createApplication(anotherFreelance, mission);

        mockMvc.perform(patch("/api/applications/{id}/accept", application.getId())
                        .with(httpBasic(freelance.getEmail(), "password")))
                .andExpect(status().isForbidden());

        Application unchanged = applicationRepository.findById(application.getId())
                .orElseThrow();

        assertThat(unchanged.getStatus()).isEqualTo(ApplicationStatus.PENDING);
    }

    @Test
    void shouldReturnApplicationForItsFreelance() throws Exception {
        Application application = createApplication(freelance, mission);

        mockMvc.perform(get("/api/applications/{id}", application.getId())
                        .with(httpBasic(freelance.getEmail(), "password")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(application.getId()))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    private Client createClient(String email) {
        Client client = new Client();
        client.setEmail(email);
        client.setPassword(passwordEncoder.encode("password"));
        client.setFirstName("Test");
        client.setLastName("Client");
        client.setCompanyName("Test Company");

        return (Client) userRepository.save(client);
    }

    private Freelance createFreelance(String email) {
        Freelance freelance = new Freelance();
        freelance.setEmail(email);
        freelance.setPassword(passwordEncoder.encode("password"));
        freelance.setFirstName("Test");
        freelance.setLastName("Freelance");
        freelance.setDailyRate(new BigDecimal("300"));

        return (Freelance) userRepository.save(freelance);
    }

    private Mission createMission(Client client, MissionStatus status) {
        Mission mission = new Mission();
        mission.setTitle("Développement API");
        mission.setDescription("Développement d'une API REST.");
        mission.setBudget(new BigDecimal("5000"));
        mission.setLocation("Paris");
        mission.setRemote(true);
        mission.setStatus(status);
        mission.setClient(client);

        return missionRepository.save(mission);
    }

    private Application createApplication(Freelance freelance, Mission mission) {
        Application application = new Application();
        application.setCoverLetter("Je suis intéressé par cette mission.");
        application.setProposedPrice(new BigDecimal("450"));
        application.setStatus(ApplicationStatus.PENDING);
        application.setFreelance(freelance);
        application.setMission(mission);

        return applicationRepository.save(application);
    }
}
