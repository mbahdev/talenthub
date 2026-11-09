package talenthub.talenthub.mission.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import talenthub.talenthub.mission.entity.MissionStatus;

import java.math.BigDecimal;
import java.util.Set;

@Data
public class MissionResponseDto {

    private Long id;
    private String title;
    private String description;
    private BigDecimal budget;
    private String location;
    private boolean remote;
    private MissionStatus status;
    private Long clientId;
    private Set<Long> skillIds;

    //RequestDto : les IDs servent à retrouver les entités.
    //ResponseDto : les IDs servent à référencer les entités sans les exposer complètement.

}