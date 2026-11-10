package talenthub.talenthub.user.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
@Entity
@DiscriminatorValue("Client")
public class Client extends User{

    private String companyName;
    private String companyDescription, location;

}
