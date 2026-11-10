package talenthub.talenthub.application.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import talenthub.talenthub.application.dto.*;
import talenthub.talenthub.application.service.ApplicationService;
import talenthub.talenthub.security.UserPrincipal;

import java.util.List;

@RestController
@RequestMapping("/api/applications")
@SecurityRequirement(name = "basicAuth")
public class ApplicationController {

    private final ApplicationService applicationService;

    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @GetMapping("/{id}")
    public ApplicationResponseDto findById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {

        return applicationService.findById(id, principal.getUser());
    }

    @GetMapping
    public List<ApplicationResponseDto> findAll(
            @AuthenticationPrincipal UserPrincipal principal) {

        return applicationService.findByFreelanceId(
                principal.getUser().getId(),
                principal.getUser().getId()
        );
    }

    @PostMapping
    public ApplicationResponseDto create(
            @Valid @RequestBody ApplicationRequestDto request,
            @AuthenticationPrincipal UserPrincipal principal) {

        return applicationService.create(
                request,
                principal.getUser().getId()
        );
    }

    @PatchMapping("/{id}/withdraw")
    public ApplicationResponseDto withdraw(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {

        return applicationService.withdraw(
                id,
                principal.getUser().getId()
        );
    }

    @PatchMapping("/{id}/accept")
    public ApplicationResponseDto accept(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {

        return applicationService.accept(
                id,
                principal.getUser().getId()
        );
    }

    @PatchMapping("/{id}/reject")
    public ApplicationResponseDto reject(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {

        return applicationService.reject(
                id,
                principal.getUser().getId()
        );
    }

    @GetMapping("/freelance/{freelanceId}")
    public List<ApplicationResponseDto> findByFreelanceId(
            @PathVariable Long freelanceId,
            @AuthenticationPrincipal UserPrincipal principal) {

        return applicationService.findByFreelanceId(
                freelanceId,
                principal.getUser().getId()
        );
    }

    @GetMapping("/mission/{missionId}")
    public List<ApplicationResponseDto> findByMissionId(
            @PathVariable Long missionId,
            @AuthenticationPrincipal UserPrincipal principal) {

        return applicationService.findByMissionId(
                missionId,
                principal.getUser().getId()
        );
    }
}