package talenthub.talenthub.user.entity;

import jakarta.persistence.*;
import talenthub.talenthub.skill.entity.Skill;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

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

    public String getBio() {
        return bio;
    }

    public BigDecimal getDailyRate() {
        return dailyRate;
    }

    public String getLocation() {
        return location;
    }

    public Set<Skill> getSkills() {
        return skills;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public void setDailyRate(BigDecimal dailyRate) {
        this.dailyRate = dailyRate;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setSkills(Set<Skill> skills) {
        this.skills = skills;
    }
}
