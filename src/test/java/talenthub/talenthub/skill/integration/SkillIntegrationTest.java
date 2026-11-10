
package talenthub.talenthub.skill.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import talenthub.talenthub.skill.entity.Skill;
import talenthub.talenthub.skill.repository.SkillRepository;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@WithMockUser(username = "freelance@test.com", roles = "FREELANCE")
class SkillIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SkillRepository skillRepository;

    // --------------------------------------------------
    // FIND ACTIVE SKILLS
    // --------------------------------------------------

    @Test
    void shouldReturnOnlyActiveSkills() throws Exception {
        String activeName = uniqueSkillName("Active");
        String inactiveName = uniqueSkillName("Inactive");

        createSkill(activeName, true);
        createSkill(inactiveName, false);

        mockMvc.perform(get("/api/skills"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].name")
                        .value(org.hamcrest.Matchers.hasItem(activeName)))
                .andExpect(jsonPath("$[*].name")
                        .value(org.hamcrest.Matchers.not(
                                org.hamcrest.Matchers.hasItem(inactiveName))));
    }

    // --------------------------------------------------
    // CREATE SKILL
    // --------------------------------------------------

    @Test
    void shouldCreateSkillSuccessfully() throws Exception {
        long skillsBefore = skillRepository.count();
        String name = uniqueSkillName("Create");

        String request = """
                {
                    "name": "%s"
                }
                """.formatted(name);

        mockMvc.perform(post("/api/skills")
                        .contentType(APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.active").value(true));

        assertThat(skillRepository.count()).isEqualTo(skillsBefore + 1);
    }

    @Test
    void shouldTrimSkillNameBeforeSaving() throws Exception {
        long skillsBefore = skillRepository.count();
        String name = uniqueSkillName("Trim");

        String request = """
                {
                    "name": " %s "
                }
                """.formatted(name);

        mockMvc.perform(post("/api/skills")
                        .contentType(APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(name));

        assertThat(skillRepository.count()).isEqualTo(skillsBefore + 1);
    }

    @Test
    void shouldRejectDuplicateSkillIgnoringCase() throws Exception {
        String name = uniqueSkillName("Duplicate");
        createSkill(name, true);

        long skillsBefore = skillRepository.count();

        String request = """
            {
                "name": "%s"
            }
            """.formatted(name.toUpperCase());

        mockMvc.perform(post("/api/skills")
                        .contentType(APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Skill already exists"));

        assertThat(skillRepository.count()).isEqualTo(skillsBefore);
    }

    // --------------------------------------------------
    // VALIDATION
    // --------------------------------------------------

    @Test
    void shouldRejectBlankSkillName() throws Exception {
        long skillsBefore = skillRepository.count();

        String request = """
                {
                    "name": ""
                }
                """;

        mockMvc.perform(post("/api/skills")
                        .contentType(APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest());

        assertThat(skillRepository.count()).isEqualTo(skillsBefore);
    }

    // --------------------------------------------------
    // TEST HELPERS
    // --------------------------------------------------

    private Skill createSkill(String name, boolean active) {
        Skill skill = new Skill();
        skill.setName(name);
        skill.setActive(active);

        return skillRepository.save(skill);
    }

    private String uniqueSkillName(String prefix) {
        return prefix + "-" + UUID.randomUUID();
    }
}
