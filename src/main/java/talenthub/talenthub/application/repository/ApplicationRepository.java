package talenthub.talenthub.application.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import talenthub.talenthub.application.entity.Application;
import talenthub.talenthub.application.entity.ApplicationStatus;

import java.util.List;

public interface ApplicationRepository extends JpaRepository<Application, Long> {
    boolean existsByFreelanceIdAndMissionId(Long freelanceId, Long missionId);
    List<Application> findByMissionIdAndStatus(Long missionId, ApplicationStatus status);

}
