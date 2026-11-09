
package talenthub.talenthub.skill.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import talenthub.talenthub.skill.dto.SkillRequestDto;
import talenthub.talenthub.skill.entity.Skill;
import talenthub.talenthub.skill.repository.SkillRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SkillServiceTest {

    @Mock
    private SkillRepository skillRepository;

    @InjectMocks
    private SkillService skillService;

    // --------------------------------------------------
    // FIND ACTIVE SKILLS
    // --------------------------------------------------

    @Test
    void shouldFindActiveSkills() {
        Skill java = createSkill(1L, "Java", true);
        Skill spring = createSkill(2L, "Spring Boot", true);

        when(skillRepository.findByActiveTrue())
                .thenReturn(List.of(java, spring));

        List<Skill> result = skillService.findActiveSkills();

        assertThat(result).hasSize(2);
        assertThat(result)
                .extracting(Skill::getName)
                .containsExactly("Java", "Spring Boot");

        verify(skillRepository).findByActiveTrue();
    }

    @Test
    void shouldReturnEmptyListWhenNoActiveSkillsExist() {
        when(skillRepository.findByActiveTrue())
                .thenReturn(List.of());

        List<Skill> result = skillService.findActiveSkills();

        assertThat(result).isEmpty();

        verify(skillRepository).findByActiveTrue();
    }

    // --------------------------------------------------
    // CREATE
    // --------------------------------------------------

    @Test
    void shouldCreateSkillSuccessfully() {
        SkillRequestDto request = new SkillRequestDto();
        request.setName(" Java ");

        when(skillRepository.existsByNameIgnoreCase("Java"))
                .thenReturn(false);

        when(skillRepository.save(any(Skill.class)))
                .thenAnswer(invocation -> {
                    Skill skill = invocation.getArgument(0);
                    skill.setId(1L);
                    return skill;
                });

        Skill result = skillService.create(request);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Java");
        assertThat(result.isActive()).isTrue();

        verify(skillRepository).existsByNameIgnoreCase("Java");
        verify(skillRepository).save(any(Skill.class));
    }

    @Test
    void shouldThrowExceptionWhenSkillAlreadyExists() {
        SkillRequestDto request = new SkillRequestDto();
        request.setName(" java ");

        when(skillRepository.existsByNameIgnoreCase("java"))
                .thenReturn(true);

        assertThatThrownBy(() -> skillService.create(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Skill already exists");

        verify(skillRepository).existsByNameIgnoreCase("java");
        verify(skillRepository, never()).save(any());
    }

    @Test
    void shouldCreateSkillWithActiveStatus() {
        SkillRequestDto request = new SkillRequestDto();
        request.setName("Spring Boot");

        when(skillRepository.existsByNameIgnoreCase("Spring Boot"))
                .thenReturn(false);

        when(skillRepository.save(any(Skill.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Skill result = skillService.create(request);

        assertThat(result.isActive()).isTrue();

        verify(skillRepository).save(any(Skill.class));
    }

    // --------------------------------------------------
    // TEST HELPERS
    // --------------------------------------------------

    private Skill createSkill(Long id, String name, boolean active) {
        Skill skill = new Skill();
        skill.setId(id);
        skill.setName(name);
        skill.setActive(active);
        return skill;
    }
}
