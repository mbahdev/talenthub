package talenthub.talenthub.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import talenthub.talenthub.user.entity.Freelance;

public interface FreelanceRepository extends JpaRepository<Freelance, Long> {
}
