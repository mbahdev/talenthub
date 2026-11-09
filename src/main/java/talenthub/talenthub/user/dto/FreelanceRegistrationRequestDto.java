package talenthub.talenthub.user.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Set;

@Data
public class FreelanceRegistrationRequestDto {

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min = 8, max = 100)
    private String password;

    @NotBlank
    @Size(max = 100)
    private String firstName;

    @NotBlank
    @Size(max = 100)
    private String lastName;

    private String phone;

    private String bio;

    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal dailyRate;

    private String location;

    private Set<Long> skillIds;
}