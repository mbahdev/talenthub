package talenthub.talenthub.favorite.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import talenthub.talenthub.favorite.entity.Favorite;

import java.util.List;
import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    boolean existsByFreelanceIdAndMissionId(
            Long freelanceId,
            Long missionId
    );

    List<Favorite> findByFreelanceId(Long freelanceId);

    Optional<Favorite> findByFreelanceIdAndMissionId(
            Long freelanceId,
            Long missionId
    );
}