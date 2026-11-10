
package talenthub.talenthub.favorite.controller;

import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import talenthub.talenthub.favorite.dto.FavoriteRequestDto;
import talenthub.talenthub.favorite.dto.FavoriteResponseDto;
import talenthub.talenthub.favorite.service.FavoriteService;
import talenthub.talenthub.security.UserPrincipal;

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
            @Valid @RequestBody FavoriteRequestDto request,
            @AuthenticationPrincipal UserPrincipal principal) {

        return favoriteService.create(
                request,
                principal.getUser().getId()
        );
    }

    @GetMapping("/freelance/{freelanceId}")
    public List<FavoriteResponseDto> findByFreelanceId(
            @PathVariable Long freelanceId,
            @AuthenticationPrincipal UserPrincipal principal) {

        return favoriteService.findByFreelanceId(
                freelanceId,
                principal.getUser().getId()
        );
    }

    @DeleteMapping("/freelance/{freelanceId}/mission/{missionId}")
    public void delete(
            @PathVariable Long freelanceId,
            @PathVariable Long missionId,
            @AuthenticationPrincipal UserPrincipal principal) {

        favoriteService.delete(
                freelanceId,
                missionId,
                principal.getUser().getId()
        );
    }
}
