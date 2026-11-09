
package talenthub.talenthub.skill.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SkillRequestDto {

    @NotBlank(message = "Skill name is required")
    @Size(max = 100)
    private String name;
}
