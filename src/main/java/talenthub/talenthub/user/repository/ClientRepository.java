package talenthub.talenthub.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import talenthub.talenthub.user.entity.Client;

public interface ClientRepository extends JpaRepository<Client, Long> {
}
