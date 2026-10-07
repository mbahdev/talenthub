package talenthub.talenthub.mission.controller;

import org.springframework.web.bind.annotation.*;
import talenthub.talenthub.mission.dto.MissionRequestDto;
import talenthub.talenthub.mission.dto.MissionResponseDto;
import talenthub.talenthub.mission.entity.MissionStatus;
import talenthub.talenthub.mission.service.MissionService;

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

    @GetMapping("/{id}")
    public MissionResponseDto findById(@PathVariable Long id) {
        return missionService.findById(id);
    }

    @PostMapping
    public MissionResponseDto create(@RequestBody MissionRequestDto request) {
        return  missionService.create(request);
    }
    
    @PutMapping("/{id}")
    public MissionResponseDto update(
            @PathVariable Long id, 
            @RequestBody MissionRequestDto request) {
        return missionService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        missionService.delete(id);
    }
}
