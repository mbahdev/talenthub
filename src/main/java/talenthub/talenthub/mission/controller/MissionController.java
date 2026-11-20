    package talenthub.talenthub.mission.controller;

    import io.swagger.v3.oas.annotations.security.SecurityRequirement;
    import jakarta.validation.Valid;
    import org.springframework.http.HttpStatus;
    import org.springframework.security.core.annotation.AuthenticationPrincipal;
    import org.springframework.web.bind.annotation.*;
    import talenthub.talenthub.mission.dto.MissionRequestDto;
    import talenthub.talenthub.mission.dto.MissionResponseDto;
    import talenthub.talenthub.mission.dto.PublicMissionResponseDto;
    import talenthub.talenthub.mission.entity.MissionStatus;
    import talenthub.talenthub.mission.service.MissionService;
    import talenthub.talenthub.security.UserPrincipal;

    import java.util.List;

    @RestController
    @RequestMapping("/api/missions")
    public class MissionController {

        private final MissionService missionService;

        public MissionController(MissionService missionService) {
            this.missionService = missionService;
        }

        @GetMapping
        @SecurityRequirement(name = "basicAuth")
        public List<MissionResponseDto> findByStatus(
                @RequestParam MissionStatus status) {

            return missionService.findByStatus(status);
        }

        @PostMapping
        @ResponseStatus(HttpStatus.CREATED)
        @SecurityRequirement(name = "basicAuth")
        public MissionResponseDto create(
                @Valid @RequestBody MissionRequestDto request,
                @AuthenticationPrincipal UserPrincipal principal) {

            return missionService.create(request, principal.getUser().getId());
        }

        @GetMapping("/{id}")
        @SecurityRequirement(name = "basicAuth")
        public MissionResponseDto findById(@PathVariable Long id) {
            return missionService.findById(id);
        }

        @PutMapping("/{id}")
        @SecurityRequirement(name = "basicAuth")
        public MissionResponseDto update(
                @PathVariable Long id,
                @Valid @RequestBody MissionRequestDto request,
                @AuthenticationPrincipal UserPrincipal principal) {

            return missionService.update(id, request, principal.getUser().getId());
        }

        @DeleteMapping("/{id}")
        @SecurityRequirement(name = "basicAuth")
        public void delete(
                @PathVariable Long id,
                @AuthenticationPrincipal UserPrincipal principal) {

            missionService.delete(id, principal.getUser().getId());
        }

        @GetMapping("/public")
        public List<PublicMissionResponseDto> findPublicMissions() {
            return missionService.findAll();
        }
    }
