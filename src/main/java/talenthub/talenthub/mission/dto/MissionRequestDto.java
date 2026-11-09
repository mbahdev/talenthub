package talenthub.talenthub.mission.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import talenthub.talenthub.mission.entity.MissionStatus;

import java.math.BigDecimal;
import java.util.Set;

@Data
public class MissionRequestDto {

    @NotBlank
    @Size(max = 150)
    private String title;

    @NotBlank
    @Size(max = 5000)
    private String description;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal budget;

    private String location;

    private boolean remote;

    private MissionStatus status;

    @NotNull
    @Positive
    private Long clientId;

    private Set<@NotNull @Positive Long> skillIds;
}