package talenthub.talenthub.skill.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import talenthub.talenthub.skill.entity.Skill;
import talenthub.talenthub.skill.service.SkillService;

import java.util.List;

@RestController
@RequestMapping("/api/skills")
public class SkillController {

    private final SkillService skillService;

    public SkillController(SkillService skillService) {
        this.skillService = skillService;
    }

    @GetMapping
    public List<Skill> findActiveSkills() {
        return skillService.findActiveSkills();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Skill create(@RequestBody Skill skill) {
        return skillService.create(skill);
    }
}
