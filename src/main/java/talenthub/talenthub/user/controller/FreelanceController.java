
package talenthub.talenthub.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
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
    public FreelanceResponseDto findById(@PathVariable Long id) {
        return freelanceService.findById(id);
    }

    /*
        @RequestBody convert the given JSON into FreelanceRequestDto.
        Service apply the update and send back a FreelanceResponseDto.
     */
    @PutMapping("/{id}")
    public FreelanceResponseDto update(
            @PathVariable Long id,
            @RequestBody FreelanceRequestDto request) {
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
