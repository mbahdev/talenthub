
package talenthub.talenthub.mission.dto;

import lombok.Data;

@Data
public class PublicMissionResponseDto {

    private Long id;
    private String title;
    private String description;
    private String location;
    private boolean remote;
}
