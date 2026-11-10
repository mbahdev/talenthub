
package talenthub.talenthub.favorite.service;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import talenthub.talenthub.favorite.dto.FavoriteRequestDto;
import talenthub.talenthub.favorite.dto.FavoriteResponseDto;
import talenthub.talenthub.favorite.entity.Favorite;
import talenthub.talenthub.favorite.repository.FavoriteRepository;
import talenthub.talenthub.mission.entity.Mission;
import talenthub.talenthub.mission.exception.MissionNotFoundException;
import talenthub.talenthub.mission.repository.MissionRepository;
import talenthub.talenthub.user.entity.Freelance;
import talenthub.talenthub.user.repository.FreelanceRepository;

import java.util.List;

@Service
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final FreelanceRepository freelanceRepository;
    private final MissionRepository missionRepository;

    public FavoriteService(
            FavoriteRepository favoriteRepository,
            FreelanceRepository freelanceRepository,
            MissionRepository missionRepository) {

        this.favoriteRepository = favoriteRepository;
        this.freelanceRepository = freelanceRepository;
        this.missionRepository = missionRepository;
    }

    @Transactional
    public FavoriteResponseDto create(
            FavoriteRequestDto request,
            Long authenticatedFreelanceId) {

        Long freelanceId = request.getFreelanceId();
        Long missionId = request.getMissionId();

        if (!freelanceId.equals(authenticatedFreelanceId)) {
            throw new AccessDeniedException(
                    "You cannot create a favorite for another freelance.");
        }

        Freelance freelance = freelanceRepository.findById(authenticatedFreelanceId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Freelance not found with id: " + authenticatedFreelanceId));

        Mission mission = missionRepository.findById(missionId)
                .orElseThrow(() -> new MissionNotFoundException(missionId));

        if (favoriteRepository.existsByFreelanceIdAndMissionId(
                authenticatedFreelanceId, missionId)) {
            throw new IllegalStateException(
                    "This mission is already in favorites");
        }

        Favorite favorite = new Favorite();
        favorite.setFreelance(freelance);
        favorite.setMission(mission);

        return toResponseDto(favoriteRepository.save(favorite));
    }

    public List<FavoriteResponseDto> findByFreelanceId(
            Long freelanceId,
            Long authenticatedFreelanceId) {

        if (!freelanceId.equals(authenticatedFreelanceId)) {
            throw new AccessDeniedException(
                    "You cannot access another freelance's favorites.");
        }

        freelanceRepository.findById(authenticatedFreelanceId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Freelance not found with id: " + authenticatedFreelanceId));

        return favoriteRepository.findByFreelanceId(authenticatedFreelanceId)
                .stream()
                .map(this::toResponseDto)
                .toList();
    }

    @Transactional
    public void delete(
            Long freelanceId,
            Long missionId,
            Long authenticatedFreelanceId) {

        if (!freelanceId.equals(authenticatedFreelanceId)) {
            throw new AccessDeniedException(
                    "You cannot delete another freelance's favorites.");
        }

        Favorite favorite = favoriteRepository
                .findByFreelanceIdAndMissionId(authenticatedFreelanceId, missionId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Favorite not found for this freelance and mission"));

        favoriteRepository.delete(favorite);
    }

    private FavoriteResponseDto toResponseDto(Favorite favorite) {

        FavoriteResponseDto dto = new FavoriteResponseDto();
        dto.setId(favorite.getId());
        dto.setFreelanceId(favorite.getFreelance().getId());
        dto.setMissionId(favorite.getMission().getId());

        return dto;
    }
}
