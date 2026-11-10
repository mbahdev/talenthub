
package talenthub.talenthub.favorite.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import talenthub.talenthub.favorite.dto.FavoriteRequestDto;
import talenthub.talenthub.favorite.dto.FavoriteResponseDto;
import talenthub.talenthub.favorite.service.FavoriteService;
import talenthub.talenthub.security.UserPrincipal;
import talenthub.talenthub.user.entity.User;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FavoriteControllerTest {

    @Mock
    private FavoriteService favoriteService;

    @InjectMocks
    private FavoriteController favoriteController;

    @Test
    void createShouldReturnCreatedFavorite() {
        Long freelanceId = 1L;

        FavoriteRequestDto request = new FavoriteRequestDto();
        FavoriteResponseDto response = new FavoriteResponseDto();

        UserPrincipal principal = createPrincipal(freelanceId);

        when(favoriteService.create(request, freelanceId)).thenReturn(response);

        FavoriteResponseDto result = favoriteController.create(request, principal);

        assertThat(result).isSameAs(response);

        verify(favoriteService).create(request, freelanceId);
    }

    @Test
    void findByFreelanceIdShouldReturnFavorites() {
        Long freelanceId = 1L;

        FavoriteResponseDto response = new FavoriteResponseDto();
        List<FavoriteResponseDto> favorites = List.of(response);

        UserPrincipal principal = createPrincipal(freelanceId);

        when(favoriteService.findByFreelanceId(freelanceId, freelanceId))
                .thenReturn(favorites);

        List<FavoriteResponseDto> result =
                favoriteController.findByFreelanceId(freelanceId, principal);

        assertThat(result).isEqualTo(favorites);

        verify(favoriteService).findByFreelanceId(freelanceId, freelanceId);
    }

    @Test
    void deleteShouldCallService() {
        Long freelanceId = 1L;
        Long missionId = 10L;

        UserPrincipal principal = createPrincipal(freelanceId);

        favoriteController.delete(freelanceId, missionId, principal);

        verify(favoriteService).delete(freelanceId, missionId, freelanceId);
    }

    private UserPrincipal createPrincipal(Long freelanceId) {
        UserPrincipal principal = mock(UserPrincipal.class);
        User user = mock(User.class);

        when(principal.getUser()).thenReturn(user);
        when(user.getId()).thenReturn(freelanceId);

        return principal;
    }
}
