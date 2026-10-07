package talenthub.talenthub.mission.entity;

import jakarta.persistence.*;
import talenthub.talenthub.user.entity.Client;
import talenthub.talenthub.skill.entity.Skill;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "missions")
public class Mission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal budget;

    private String location;

    @Column(nullable = false)
    private boolean remote;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MissionStatus status;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @ManyToMany
    @JoinTable(
            name = "mission_skills",
            joinColumns = @JoinColumn(name = "mission_id"),
            inverseJoinColumns = @JoinColumn(name = "skill_id")
    )
    private Set<Skill> requiredSkills = new HashSet<>();

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getBudget() {
        return budget;
    }

    public String getLocation() {
        return location;
    }

    public boolean isRemote() {
        return remote;
    }

    public MissionStatus getStatus() {
        return status;
    }

    public Client getClient() {
        return client;
    }

    public Set<Skill> getRequiredSkills() {
        return requiredSkills;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setBudget(BigDecimal budget) {
        this.budget = budget;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setRemote(boolean remote) {
        this.remote = remote;
    }

    public void setStatus(MissionStatus status) {
        this.status = status;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public void setRequiredSkills(Set<Skill> requiredSkills) {
        this.requiredSkills = requiredSkills;
    }

}
