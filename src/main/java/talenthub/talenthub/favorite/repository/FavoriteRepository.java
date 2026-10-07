package talenthub.talenthub.favorite.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import talenthub.talenthub.favorite.entity.Favorite;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
}