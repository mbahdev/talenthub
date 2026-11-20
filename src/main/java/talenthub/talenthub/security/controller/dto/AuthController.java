
package talenthub.talenthub.security.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import talenthub.talenthub.security.UserPrincipal;
import talenthub.talenthub.security.dto.CurrentUserResponseDto;
import talenthub.talenthub.user.entity.Client;
import talenthub.talenthub.user.entity.Freelance;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    @GetMapping("/me")
    public CurrentUserResponseDto getCurrentUser(
            @AuthenticationPrincipal UserPrincipal principal) {

        String role = principal.getUser() instanceof Freelance
                ? "FREELANCE"
                : principal.getUser() instanceof Client
                ? "CLIENT"
                : "UNKNOWN";

        return new CurrentUserResponseDto(
                principal.getUser().getId(),
                principal.getUser().getEmail(),
                role
        );
    }
}
