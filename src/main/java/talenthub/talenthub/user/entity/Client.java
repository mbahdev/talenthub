package talenthub.talenthub.user.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.math.BigDecimal;

@Entity
@DiscriminatorValue("Client")
public class Client extends User{

    private String companyName;
    private String companyDescription, location;

    public String getCompanyName() {
        return companyName;
    }

    public String getCompanyDescription() {
        return companyDescription;
    }

    public String getLocation() {
        return location;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public void setCompanyDescription(String companyDescription) {
        this.companyDescription = companyDescription;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
