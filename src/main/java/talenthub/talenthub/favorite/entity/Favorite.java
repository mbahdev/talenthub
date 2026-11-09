package talenthub.talenthub.favorite.entity;

import jakarta.persistence.*;
import lombok.Data;
import talenthub.talenthub.mission.entity.Mission;
import talenthub.talenthub.user.entity.Freelance;

@Entity
@Table(
        name = "favorites",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_favorite_freelance_mission",
                        columnNames = {"freelance_id", "mission_id"}
                )
        }
)
@Data
public class Favorite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "freelance_id", nullable = false)
    private Freelance freelance;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "mission_id", nullable = false)
    private Mission mission;
}