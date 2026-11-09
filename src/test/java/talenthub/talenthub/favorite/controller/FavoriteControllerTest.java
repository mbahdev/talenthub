package talenthub.talenthub.favorite.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import talenthub.talenthub.favorite.dto.FavoriteRequestDto;
import talenthub.talenthub.favorite.dto.FavoriteResponseDto;
import talenthub.talenthub.favorite.service.FavoriteService;

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
        FavoriteRequestDto request = new FavoriteRequestDto();
        FavoriteResponseDto response = new FavoriteResponseDto();

        when(favoriteService.create(request)).thenReturn(response);

        FavoriteResponseDto result = favoriteController.create(request);

        assertThat(result).isSameAs(response);

        verify(favoriteService).create(request);
    }

    @Test
    void findByFreelanceIdShouldReturnFavorites() {
        FavoriteResponseDto response = new FavoriteResponseDto();
        List<FavoriteResponseDto> favorites = List.of(response);

        when(favoriteService.findByFreelanceId(1L))
                .thenReturn(favorites);

        List<FavoriteResponseDto> result =
                favoriteController.findByFreelanceId(1L);

        assertThat(result).isEqualTo(favorites);

        verify(favoriteService).findByFreelanceId(1L);
    }

    @Test
    void deleteShouldCallService() {
        favoriteController.delete(1L, 10L);

        verify(favoriteService).delete(1L, 10L);
    }
}