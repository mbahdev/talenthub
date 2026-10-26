package talenthub.talenthub.skill.service;

import org.springframework.stereotype.Service;
import talenthub.talenthub.skill.entity.Skill;
import talenthub.talenthub.skill.repository.SkillRepository;

import java.util.List;

@Service
public class SkillService {

    private final SkillRepository skillRepository;

    public SkillService(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }

    public List<Skill> findActiveSkills() {
        return skillRepository.findByActiveTrue();
    }

    public Skill create(Skill skill) {
        skill.setId(null);
        skill.setActive(true);
        return skillRepository.save(skill);
    }
}