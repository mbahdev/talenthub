
package talenthub.talenthub.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.AccessDeniedException;
import talenthub.talenthub.security.UserPrincipal;
import talenthub.talenthub.user.dto.FreelanceRegistrationRequestDto;
import talenthub.talenthub.user.dto.FreelanceRequestDto;
import talenthub.talenthub.user.dto.FreelanceResponseDto;
import talenthub.talenthub.user.service.FreelanceService;

import java.util.List;

@RestController
@RequestMapping("/api/freelances")
@RequiredArgsConstructor
public class FreelanceController {

    private final FreelanceService freelanceService;

    @GetMapping("/{id}")
    public FreelanceResponseDto findById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {

        if (!principal.getUser().getId().equals(id)) {
            throw new AccessDeniedException("You cannot access another freelance profile.");
        }

        return freelanceService.findById(id);
    }

    @PutMapping("/{id}")
    public FreelanceResponseDto update(
            @PathVariable Long id,
            @RequestBody FreelanceRequestDto request,
            @AuthenticationPrincipal UserPrincipal principal) {

        if (!principal.getUser().getId().equals(id)) {
            throw new AccessDeniedException("You cannot update another freelance profile.");
        }

        return freelanceService.update(id, request);
    }

    @GetMapping
    public List<FreelanceResponseDto> findAll() {
        return freelanceService.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FreelanceResponseDto register(
            @RequestBody FreelanceRegistrationRequestDto request) {
        return freelanceService.register(request);
    }
}
