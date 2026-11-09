
package talenthub.talenthub.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ClientRequestDto {

    @NotBlank(message = "First name is required")
    @Size(max = 100)
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 100)
    private String lastName;

    @Size(max = 20)
    private String phone;

    @NotBlank(message = "Company name is required")
    @Size(max = 150)
    private String companyName;

    @Size(max = 2000)
    private String companyDescription;

    @Size(max = 150)
    private String location;
}
