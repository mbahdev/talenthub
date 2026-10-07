package talenthub.talenthub.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import talenthub.talenthub.user.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
