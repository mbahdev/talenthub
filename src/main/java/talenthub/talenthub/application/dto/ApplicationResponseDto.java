package talenthub.talenthub.application.dto;

import lombok.Data;
import talenthub.talenthub.application.entity.ApplicationStatus;

import java.math.BigDecimal;

@Data
public class ApplicationResponseDto {

    private Long id;
    private String coverLetter;
    private BigDecimal proposedPrice;
    private ApplicationStatus status;
    private Long freelanceId;
    private Long missionId;
}
