package talenthub.talenthub.application.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ApplicationRequestDto {

    private String coverLetter;
    private BigDecimal proposedPrice;
    private Long freelanceId, missionId;
}
