package talenthub.talenthub.favorite.service;

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
    public FavoriteResponseDto create(FavoriteRequestDto request) {

        Long freelanceId = request.getFreelanceId();
        Long missionId = request.getMissionId();

        Freelance freelance = freelanceRepository.findById(freelanceId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Freelance not found with id: " + freelanceId));

        Mission mission = missionRepository.findById(missionId)
                .orElseThrow(() -> new MissionNotFoundException(missionId));

        if (favoriteRepository.existsByFreelanceIdAndMissionId(
                freelanceId, missionId)) {
            throw new IllegalStateException(
                    "This mission is already in favorites");
        }

        Favorite favorite = new Favorite();
        favorite.setFreelance(freelance);
        favorite.setMission(mission);

        return toResponseDto(favoriteRepository.save(favorite));
    }

    public List<FavoriteResponseDto> findByFreelanceId(Long freelanceId) {

        freelanceRepository.findById(freelanceId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Freelance not found with id: " + freelanceId));

        return favoriteRepository.findByFreelanceId(freelanceId)
                .stream()
                .map(this::toResponseDto)
                .toList();
    }

    @Transactional
    public void delete(Long freelanceId, Long missionId) {

        Favorite favorite = favoriteRepository
                .findByFreelanceIdAndMissionId(freelanceId, missionId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
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
