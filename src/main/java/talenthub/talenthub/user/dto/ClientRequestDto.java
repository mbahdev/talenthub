package talenthub.talenthub.user.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Set;

@Data
public class ClientRequestDto {

    private String firstName;
    private String lastName;
    private String phone;
    private String companyName;
    private String companyDescription;
    private String location;
}
