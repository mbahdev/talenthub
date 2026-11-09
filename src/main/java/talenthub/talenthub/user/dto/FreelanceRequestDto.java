
package talenthub.talenthub.user.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Set;

@Data
public class FreelanceRequestDto {

    @NotBlank(message = "First name is required")
    @Size(max = 100)
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 100)
    private String lastName;

    @Size(max = 20)
    private String phone;

    @Size(max = 2000)
    private String bio;

    @NotNull(message = "Daily rate is required")
    @DecimalMin(value = "0.01")
    private BigDecimal dailyRate;

    @Size(max = 150)
    private String location;

    @NotNull(message = "Skill IDs are required")
    @Size(max = 50)
    private Set<@NotNull @Positive Long> skillIds;
}
