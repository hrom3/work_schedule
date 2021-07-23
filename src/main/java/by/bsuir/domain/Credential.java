package by.bsuir.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "credential")
@Data
@NoArgsConstructor
@EqualsAndHashCode(exclude = {"user"})
public class Credential {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "id_users")
    @JsonBackReference
    //@JsonIgnoreProperties("credential")
    //@JsonIdentityInfo(generator= ObjectIdGenerators.UUIDGenerator.class, property="@id")
    private User user;

    @Column
    private String login;

    @Column
    @ToString.Exclude
    private String password;
}


