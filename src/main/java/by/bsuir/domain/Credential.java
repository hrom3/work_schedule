package by.bsuir.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;

@Entity
@Table(name = "credential")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Credential {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @NaturalId
    @Column(name = "id_users")
    private Long idUser;

    @OneToOne(optional = false, mappedBy = "credential")
    private User user;

    @Column
    private String login;

    @Column
    private String password;

    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }
}


