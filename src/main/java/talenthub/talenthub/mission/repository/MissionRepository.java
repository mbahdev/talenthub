package talenthub.talenthub.mission.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import talenthub.talenthub.mission.entity.Mission;
import talenthub.talenthub.mission.entity.MissionStatus;

import java.util.List;

public interface MissionRepository extends JpaRepository<Mission, Long> {

    List<Mission> findByStatus(MissionStatus status);
}
