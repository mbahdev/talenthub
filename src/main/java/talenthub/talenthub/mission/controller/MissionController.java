    package talenthub.talenthub.mission.controller;

    import jakarta.validation.Valid;
    import org.springframework.http.HttpStatus;
    import org.springframework.security.core.annotation.AuthenticationPrincipal;
    import org.springframework.web.bind.annotation.*;
    import talenthub.talenthub.mission.dto.MissionRequestDto;
    import talenthub.talenthub.mission.dto.MissionResponseDto;
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
        public List<MissionResponseDto> findByStatus(
                @RequestParam MissionStatus status) {

            return missionService.findByStatus(status);
        }

        @PostMapping
        @ResponseStatus(HttpStatus.CREATED)
        public MissionResponseDto create(
                @Valid @RequestBody MissionRequestDto request,
                @AuthenticationPrincipal UserPrincipal principal) {

            return missionService.create(request, principal.getUser().getId());
        }

        @GetMapping("/{id}")
        public MissionResponseDto findById(@PathVariable Long id) {
            return missionService.findById(id);
        }

        @PutMapping("/{id}")
        public MissionResponseDto update(
                @PathVariable Long id,
                @Valid @RequestBody MissionRequestDto request,
                @AuthenticationPrincipal UserPrincipal principal) {

            return missionService.update(id, request, principal.getUser().getId());
        }

        @DeleteMapping("/{id}")
        public void delete(
                @PathVariable Long id,
                @AuthenticationPrincipal UserPrincipal principal) {

            missionService.delete(id, principal.getUser().getId());
        }
    }
