
package talenthub.talenthub.user.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Set;

@Data
public class ClientResponseDto {

    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private String companyName;
    private String companyDescription;
    private String location;
}
