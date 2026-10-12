
package talenthub.talenthub.user.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Set;

@Data
public class FreelanceResponseDto {

    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private String bio;
    private BigDecimal dailyRate;
    private String location;
    private Set<Long> skillIds;
}
