package talenthub.talenthub.application.controller;

import org.springframework.web.bind.annotation.*;
import talenthub.talenthub.application.dto.*;
import talenthub.talenthub.application.service.ApplicationService;

import java.util.List;

@RestController
@RequestMapping("/api/applications")
public class ApplicationController {

    ApplicationService applicationService;

    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @GetMapping("/{id}")
    public ApplicationResponseDto findById(
            @PathVariable Long id) {
        return applicationService.findById(id);
    }

    @GetMapping
    public List<ApplicationResponseDto> findAll(){
        return applicationService.findAll();
    }

    @PostMapping
    public ApplicationResponseDto create(@RequestBody ApplicationRequestDto request) {
        return  applicationService.create(request);
    }

    @PatchMapping("/{id}/withdraw")
    public ApplicationResponseDto withdraw(
            @PathVariable Long id) {

        return applicationService.withdraw(id);
    }

    @PatchMapping("/{id}/accept")
    public ApplicationResponseDto accept(@PathVariable Long id) {
        return applicationService.accept(id) ;
    }

    @PatchMapping("/{id}/reject")
    public ApplicationResponseDto reject(@PathVariable Long id) {
        return applicationService.reject(id) ;
    }

    @GetMapping("/freelance/{freelanceId}")
    public List<ApplicationResponseDto> findByFreelanceId(
            @PathVariable Long freelanceId) {

        return applicationService.findByFreelanceId(freelanceId);
    }

    @GetMapping("/mission/{missionId}")
    public List<ApplicationResponseDto> findByMissionId(
            @PathVariable Long missionId) {

        return applicationService.findByMissionId(missionId);
    }


}
