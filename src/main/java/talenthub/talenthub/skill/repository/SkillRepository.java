package talenthub.talenthub.skill.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import talenthub.talenthub.skill.entity.Skill;

public interface SkillRepository extends JpaRepository<Skill, Long> {
}
