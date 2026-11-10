
package talenthub.talenthub.user.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import talenthub.talenthub.user.entity.Client;
import talenthub.talenthub.user.repository.UserRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ClientIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldRegisterClientSuccessfully() throws Exception {

        String request = """
                {
                    "email": "client@test.com",
                    "password": "password123",
                    "firstName": "John",
                    "lastName": "Doe",
                    "phone": "0612345678",
                    "companyName": "Test Company",
                    "companyDescription": "A test company",
                    "location": "Paris"
                }
                """;

        mockMvc.perform(post("/api/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isCreated());

        assertThat(userRepository.findByEmail("client@test.com"))
                .isPresent()
                .get()
                .isInstanceOf(Client.class);
    }

    @Test
    void shouldReturn400WhenEmailIsInvalid() throws Exception {

        String request = """
                {
                    "email": "invalid-email",
                    "password": "password123",
                    "firstName": "John",
                    "lastName": "Doe",
                    "companyName": "Test Company"
                }
                """;

        mockMvc.perform(post("/api/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400WhenPasswordIsTooShort() throws Exception {

        String request = """
                {
                    "email": "client@test.com",
                    "password": "123",
                    "firstName": "John",
                    "lastName": "Doe",
                    "companyName": "Test Company"
                }
                """;

        mockMvc.perform(post("/api/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest());
    }
}
