
package talenthub.talenthub.skill.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import talenthub.talenthub.skill.dto.SkillRequestDto;
import talenthub.talenthub.skill.entity.Skill;
import talenthub.talenthub.skill.service.SkillService;

import java.util.List;

@RestController
@RequestMapping("/api/skills")
@RequiredArgsConstructor
@SecurityRequirement(name = "basicAuth")
public class SkillController {

    private final SkillService skillService;

    @GetMapping
    public List<Skill> findActiveSkills() {
        return skillService.findActiveSkills();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Skill create(@Valid @RequestBody SkillRequestDto request) {
        return skillService.create(request);
    }
}
