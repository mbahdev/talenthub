package talenthub.talenthub.user.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import talenthub.talenthub.skill.entity.Skill;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@Entity
@DiscriminatorValue("Freelance")
public class Freelance extends User{

    private String bio;
    private BigDecimal dailyRate;
    private String location;

    @ManyToMany
    @JoinTable(
            name = "freelance_skills",
            joinColumns = @JoinColumn(name = "freelance_id"),
            inverseJoinColumns = @JoinColumn(name = "skill_id")
    )
    private Set<Skill> skills = new HashSet<Skill>();

}
