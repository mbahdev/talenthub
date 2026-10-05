package talenthub.talenthub.favorite.entity;

import jakarta.persistence.*;
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

    public Long getId() {
        return id;
    }

    public Freelance getFreelance() {
        return freelance;
    }

    public Mission getMission() {
        return mission;
    }

    public void setFreelance(Freelance freelance) {
        this.freelance = freelance;
    }

    public void setMission(Mission mission) {
        this.mission = mission;
    }
}