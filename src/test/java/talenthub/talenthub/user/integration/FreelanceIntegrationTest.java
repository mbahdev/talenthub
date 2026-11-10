
package talenthub.talenthub.user.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import talenthub.talenthub.user.entity.Freelance;
import talenthub.talenthub.user.repository.UserRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class FreelanceIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldRegisterFreelanceSuccessfully() throws Exception {

        String request = """
                {
                    "email": "freelance@test.com",
                    "password": "password123",
                    "firstName": "Jane",
                    "lastName": "Doe",
                    "phone": "0612345678",
                    "bio": "Java developer",
                    "dailyRate": 350.00,
                    "location": "Paris",
                    "skillIds": []
                }
                """;

        mockMvc.perform(post("/api/freelances")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isCreated());

        assertThat(userRepository.findByEmail("freelance@test.com"))
                .isPresent()
                .get()
                .isInstanceOf(Freelance.class);
    }

    @Test
    void shouldReturn400WhenEmailIsInvalid() throws Exception {

        String request = """
                {
                    "email": "invalid-email",
                    "password": "password123",
                    "firstName": "Jane",
                    "lastName": "Doe",
                    "dailyRate": 350.00
                }
                """;

        mockMvc.perform(post("/api/freelances")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400WhenPasswordIsTooShort() throws Exception {

        String request = """
                {
                    "email": "freelance@test.com",
                    "password": "123",
                    "firstName": "Jane",
                    "lastName": "Doe",
                    "dailyRate": 350.00
                }
                """;

        mockMvc.perform(post("/api/freelances")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400WhenDailyRateIsZero() throws Exception {

        String request = """
                {
                    "email": "freelance@test.com",
                    "password": "password123",
                    "firstName": "Jane",
                    "lastName": "Doe",
                    "dailyRate": 0
                }
                """;

        mockMvc.perform(post("/api/freelances")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest());
    }
}
