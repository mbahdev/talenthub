package talenthub.talenthub.user.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ClientRegistrationRequestDto {

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min = 8, max = 100)
    private String password;

    @NotBlank
    @Size(max = 100)
    private String firstName;

    @NotBlank
    @Size(max = 100)
    private String lastName;

    private String phone;

    @NotBlank
    @Size(max = 150)
    private String companyName;

    private String companyDescription;

    private String location;
}