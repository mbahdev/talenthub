package talenthub.talenthub.application.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import talenthub.talenthub.application.entity.Application;

public interface ApplicationRepository extends JpaRepository<Application, Long> {
}
