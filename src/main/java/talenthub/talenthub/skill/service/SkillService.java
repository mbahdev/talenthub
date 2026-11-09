
package talenthub.talenthub.skill.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import talenthub.talenthub.skill.dto.SkillRequestDto;
import talenthub.talenthub.skill.entity.Skill;
import talenthub.talenthub.skill.repository.SkillRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SkillService {

    private final SkillRepository skillRepository;

    public List<Skill> findActiveSkills() {
        return skillRepository.findByActiveTrue();
    }

    public Skill create(SkillRequestDto request) {

        String name = request.getName().trim();

        if (skillRepository.existsByNameIgnoreCase(name)) {
            throw new IllegalArgumentException("Skill already exists");
        }

        Skill skill = new Skill();
        skill.setName(name);
        skill.setActive(true);

        return skillRepository.save(skill);
    }
}
