package talenthub.talenthub.mission.dto;

import talenthub.talenthub.mission.entity.MissionStatus;

import java.math.BigDecimal;
import java.util.Set;

public class MissionRequestDto {

    private String title;
    private String description;
    private BigDecimal budget;
    private String location;
    private boolean remote;
    private MissionStatus status;
    private Long clientId;
    private Set<Long> skillIds;

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

    public Long getClientId() {
        return clientId;
    }

    public Set<Long> getSkillIds() {
        return skillIds;
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

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public void setSkillIds(Set<Long> skillIds) {
        this.skillIds = skillIds;
    }
}