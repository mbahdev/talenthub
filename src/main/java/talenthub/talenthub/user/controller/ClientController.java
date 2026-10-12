
package talenthub.talenthub.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
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
    public ClientResponseDto findById(@PathVariable Long id) {
        return clientService.findById(id);
    }

    @PutMapping("/{id}")
    public ClientResponseDto update(
            @PathVariable Long id,
            @RequestBody ClientRequestDto request) {
        return clientService.update(id, request);
    }


    @GetMapping
    public List<ClientResponseDto> findAll() {
        return clientService.findAll();
    }

}
