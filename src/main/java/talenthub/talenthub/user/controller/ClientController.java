
package talenthub.talenthub.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import talenthub.talenthub.security.UserPrincipal;
import talenthub.talenthub.user.dto.ClientRegistrationRequestDto;
import talenthub.talenthub.user.dto.ClientRequestDto;
import talenthub.talenthub.user.dto.ClientResponseDto;
import talenthub.talenthub.user.service.ClientService;

import java.util.List;

@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;

    @GetMapping("/{id}")
    public ClientResponseDto findById(@PathVariable Long id, @AuthenticationPrincipal UserPrincipal principal) {
        if (!principal.getUser().getId().equals(id)) {
            throw new AccessDeniedException("You cannot access another client profile.");
        }
        return clientService.findById(id);
    }

    @PutMapping("/{id}")
    public ClientResponseDto update(
            @PathVariable Long id,
            @Valid @RequestBody ClientRequestDto request,
            @AuthenticationPrincipal UserPrincipal principal) {
        if (!principal.getUser().getId().equals(id)) {
            throw new AccessDeniedException("You cannot modify another client profile.");
        }
        return clientService.update(id, request);
    }


    @GetMapping
    public List<ClientResponseDto> findAll() {
        return clientService.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ClientResponseDto register(
            @Valid @RequestBody ClientRegistrationRequestDto request) {
        return clientService.register(request);
    }

}
