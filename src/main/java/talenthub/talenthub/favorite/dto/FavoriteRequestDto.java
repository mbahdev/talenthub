
package talenthub.talenthub.favorite.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class FavoriteRequestDto {

    @NotNull(message = "Freelance ID is required")
    @Positive
    private Long freelanceId;

    @NotNull(message = "Mission ID is required")
    @Positive
    private Long missionId;
}
