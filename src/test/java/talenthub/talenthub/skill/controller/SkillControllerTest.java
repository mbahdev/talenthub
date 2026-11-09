package talenthub.talenthub.skill.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import talenthub.talenthub.skill.dto.SkillRequestDto;
import talenthub.talenthub.skill.entity.Skill;
import talenthub.talenthub.skill.service.SkillService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SkillControllerTest {

    @Mock
    private SkillService skillService;

    @InjectMocks
    private SkillController skillController;

    @Test
    void findActiveSkillsShouldReturnSkills() {
        Skill skill = new Skill();
        List<Skill> skills = List.of(skill);

        when(skillService.findActiveSkills()).thenReturn(skills);

        List<Skill> result = skillController.findActiveSkills();

        assertThat(result).isEqualTo(skills);

        verify(skillService).findActiveSkills();
    }

    @Test
    void createShouldReturnCreatedSkill() {
        SkillRequestDto request = new SkillRequestDto();
        Skill skill = new Skill();

        when(skillService.create(request)).thenReturn(skill);

        Skill result = skillController.create(request);

        assertThat(result).isSameAs(skill);

        verify(skillService).create(request);
    }
}