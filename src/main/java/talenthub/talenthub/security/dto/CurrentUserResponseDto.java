
package talenthub.talenthub.security.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CurrentUserResponseDto {

    private Long id;
    private String email;
    private String role;
}
