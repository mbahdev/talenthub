
package talenthub.talenthub.favorite.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import talenthub.talenthub.favorite.dto.FavoriteRequestDto;
import talenthub.talenthub.favorite.dto.FavoriteResponseDto;
import talenthub.talenthub.favorite.entity.Favorite;
import talenthub.talenthub.favorite.repository.FavoriteRepository;
import talenthub.talenthub.mission.entity.Mission;
import talenthub.talenthub.mission.entity.MissionStatus;
import talenthub.talenthub.mission.exception.MissionNotFoundException;
import talenthub.talenthub.mission.repository.MissionRepository;
import talenthub.talenthub.user.entity.Freelance;
import talenthub.talenthub.user.repository.FreelanceRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FavoriteServiceTest {

    @Mock
    private FavoriteRepository favoriteRepository;

    @Mock
    private FreelanceRepository freelanceRepository;

    @Mock
    private MissionRepository missionRepository;

    @InjectMocks
    private FavoriteService favoriteService;

    // --------------------------------------------------
    // CREATE
    // --------------------------------------------------

    @Test
    void shouldCreateFavoriteSuccessfully() {
        Long freelanceId = 1L;
        Long missionId = 10L;

        Freelance freelance = createFreelance(freelanceId);
        Mission mission = createMission(missionId);

        FavoriteRequestDto request = createFavoriteRequest(
                freelanceId, missionId
        );

        when(freelanceRepository.findById(freelanceId))
                .thenReturn(Optional.of(freelance));
        when(missionRepository.findById(missionId))
                .thenReturn(Optional.of(mission));
        when(favoriteRepository.existsByFreelanceIdAndMissionId(
                freelanceId, missionId))
                .thenReturn(false);

        when(favoriteRepository.save(any(Favorite.class)))
                .thenAnswer(invocation -> {
                    Favorite favorite = invocation.getArgument(0);
                    favorite.setId(100L);
                    return favorite;
                });

        FavoriteResponseDto result = favoriteService.create(request);

        assertThat(result.getId()).isEqualTo(100L);
        assertThat(result.getFreelanceId()).isEqualTo(freelanceId);
        assertThat(result.getMissionId()).isEqualTo(missionId);

        verify(favoriteRepository).save(any(Favorite.class));
    }

    @Test
    void shouldThrowExceptionWhenFreelanceDoesNotExistDuringCreation() {
        Long freelanceId = 1L;
        Long missionId = 10L;

        FavoriteRequestDto request = createFavoriteRequest(
                freelanceId, missionId
        );

        when(freelanceRepository.findById(freelanceId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> favoriteService.create(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Freelance not found with id: " + freelanceId);

        verify(missionRepository, never()).findById(any());
        verify(favoriteRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenMissionDoesNotExistDuringCreation() {
        Long freelanceId = 1L;
        Long missionId = 10L;

        Freelance freelance = createFreelance(freelanceId);

        FavoriteRequestDto request = createFavoriteRequest(
                freelanceId, missionId
        );

        when(freelanceRepository.findById(freelanceId))
                .thenReturn(Optional.of(freelance));
        when(missionRepository.findById(missionId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> favoriteService.create(request))
                .isInstanceOf(MissionNotFoundException.class);

        verify(favoriteRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenFavoriteAlreadyExists() {
        Long freelanceId = 1L;
        Long missionId = 10L;

        Freelance freelance = createFreelance(freelanceId);
        Mission mission = createMission(missionId);

        FavoriteRequestDto request = createFavoriteRequest(
                freelanceId, missionId
        );

        when(freelanceRepository.findById(freelanceId))
                .thenReturn(Optional.of(freelance));
        when(missionRepository.findById(missionId))
                .thenReturn(Optional.of(mission));
        when(favoriteRepository.existsByFreelanceIdAndMissionId(
                freelanceId, missionId))
                .thenReturn(true);

        assertThatThrownBy(() -> favoriteService.create(request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("This mission is already in favorites");

        verify(favoriteRepository, never()).save(any());
    }

    // --------------------------------------------------
    // FIND BY FREELANCE ID
    // --------------------------------------------------

    @Test
    void shouldFindFavoritesByFreelanceId() {
        Long freelanceId = 1L;

        Freelance freelance = createFreelance(freelanceId);

        Favorite favorite1 = createFavorite(
                100L, freelance, createMission(10L)
        );

        Favorite favorite2 = createFavorite(
                101L, freelance, createMission(20L)
        );

        when(freelanceRepository.findById(freelanceId))
                .thenReturn(Optional.of(freelance));
        when(favoriteRepository.findByFreelanceId(freelanceId))
                .thenReturn(List.of(favorite1, favorite2));

        List<FavoriteResponseDto> result =
                favoriteService.findByFreelanceId(freelanceId);

        assertThat(result).hasSize(2);
        assertThat(result)
                .extracting(FavoriteResponseDto::getId)
                .containsExactly(100L, 101L);

        assertThat(result)
                .extracting(FavoriteResponseDto::getMissionId)
                .containsExactly(10L, 20L);

        verify(favoriteRepository).findByFreelanceId(freelanceId);
    }

    @Test
    void shouldReturnEmptyListWhenFreelanceHasNoFavorites() {
        Long freelanceId = 1L;

        Freelance freelance = createFreelance(freelanceId);

        when(freelanceRepository.findById(freelanceId))
                .thenReturn(Optional.of(freelance));
        when(favoriteRepository.findByFreelanceId(freelanceId))
                .thenReturn(List.of());

        List<FavoriteResponseDto> result =
                favoriteService.findByFreelanceId(freelanceId);

        assertThat(result).isEmpty();
    }

    @Test
    void shouldThrowExceptionWhenFreelanceDoesNotExistDuringSearch() {
        Long freelanceId = 1L;

        when(freelanceRepository.findById(freelanceId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                favoriteService.findByFreelanceId(freelanceId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Freelance not found with id: " + freelanceId);

        verify(favoriteRepository, never()).findByFreelanceId(any());
    }

    // --------------------------------------------------
    // DELETE
    // --------------------------------------------------

    @Test
    void shouldDeleteFavoriteSuccessfully() {
        Long freelanceId = 1L;
        Long missionId = 10L;

        Freelance freelance = createFreelance(freelanceId);
        Mission mission = createMission(missionId);

        Favorite favorite = createFavorite(
                100L, freelance, mission
        );

        when(favoriteRepository.findByFreelanceIdAndMissionId(
                freelanceId, missionId))
                .thenReturn(Optional.of(favorite));

        favoriteService.delete(freelanceId, missionId);

        verify(favoriteRepository).delete(favorite);
    }

    @Test
    void shouldThrowExceptionWhenFavoriteDoesNotExistDuringDeletion() {
        Long freelanceId = 1L;
        Long missionId = 10L;

        when(favoriteRepository.findByFreelanceIdAndMissionId(
                freelanceId, missionId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                favoriteService.delete(freelanceId, missionId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(
                        "Favorite not found for this freelance and mission"
                );

        verify(favoriteRepository, never()).delete(any());
    }

    // --------------------------------------------------
    // TEST HELPERS
    // --------------------------------------------------

    private Freelance createFreelance(Long id) {
        Freelance freelance = new Freelance();
        freelance.setId(id);
        return freelance;
    }

    private Mission createMission(Long id) {
        Mission mission = new Mission();
        mission.setId(id);
        mission.setTitle("Backend Developer");
        mission.setStatus(MissionStatus.OPEN);
        return mission;
    }

    private Favorite createFavorite(
            Long id,
            Freelance freelance,
            Mission mission) {

        Favorite favorite = new Favorite();
        favorite.setId(id);
        favorite.setFreelance(freelance);
        favorite.setMission(mission);
        return favorite;
    }

    private FavoriteRequestDto createFavoriteRequest(
            Long freelanceId,
            Long missionId) {

        FavoriteRequestDto request = new FavoriteRequestDto();
        request.setFreelanceId(freelanceId);
        request.setMissionId(missionId);
        return request;
    }
}
