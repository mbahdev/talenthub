package talenthub.talenthub.application.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ApplicationRequestDto {

    @NotBlank
    @Size(max = 5000)
    private String coverLetter;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal proposedPrice;

    @NotNull
    @Positive
    private Long missionId;
}