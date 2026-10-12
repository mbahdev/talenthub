
package talenthub.talenthub.favorite.controller;

import org.springframework.web.bind.annotation.*;

import talenthub.talenthub.favorite.dto.FavoriteRequestDto;
import talenthub.talenthub.favorite.dto.FavoriteResponseDto;
import talenthub.talenthub.favorite.service.FavoriteService;

import java.util.List;

@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {

    private final FavoriteService favoriteService;

    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    @PostMapping
    public FavoriteResponseDto create(
            @RequestBody FavoriteRequestDto request) {

        return favoriteService.create(request);
    }

    @GetMapping("/freelance/{freelanceId}")
    public List<FavoriteResponseDto> findByFreelanceId(
            @PathVariable Long freelanceId) {

        return favoriteService.findByFreelanceId(freelanceId);
    }

    @DeleteMapping("/freelance/{freelanceId}/mission/{missionId}")
    public void delete(
            @PathVariable Long freelanceId,
            @PathVariable Long missionId) {

        favoriteService.delete(freelanceId, missionId);
    }
}
