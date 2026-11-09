package talenthub.talenthub.application.entity;

import jakarta.persistence.*;
import lombok.Data;
import talenthub.talenthub.mission.entity.Mission;
import talenthub.talenthub.user.entity.Freelance;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "applications")
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String coverLetter;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal proposedPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplicationStatus status;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "freelance_id", nullable = false)
    private Freelance freelance;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "mission_id", nullable = false)
    private Mission mission;
}
