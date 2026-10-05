package talenthub.talenthub.application.entity;

import jakarta.persistence.*;
import talenthub.talenthub.mission.entity.Mission;
import talenthub.talenthub.user.entity.Freelance;

import java.math.BigDecimal;

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

    public Long getId() {
        return id;
    }

    public String getCoverLetter() {
        return coverLetter;
    }

    public BigDecimal getProposedPrice() {
        return proposedPrice;
    }

    public ApplicationStatus getStatus() {
        return status;
    }

    public Freelance getFreelance() {
        return freelance;
    }

    public Mission getMission() {
        return mission;
    }

    public void setCoverLetter(String coverLetter) {
        this.coverLetter = coverLetter;
    }

    public void setProposedPrice(BigDecimal proposedPrice) {
        this.proposedPrice = proposedPrice;
    }

    public void setStatus(ApplicationStatus status) {
        this.status = status;
    }

    public void setFreelance(Freelance freelance) {
        this.freelance = freelance;
    }

    public void setMission(Mission mission) {
        this.mission = mission;
    }
}
