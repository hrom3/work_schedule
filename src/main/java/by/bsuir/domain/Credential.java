package by.bsuir.domain;

import by.bsuir.domain.viewhelper.View;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "credential")
@Data
@NoArgsConstructor
@EqualsAndHashCode(exclude = {"user"})
public class Credential {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator =  "credential_id"
    )
    @SequenceGenerator(
            name = "credential_id",
            sequenceName = "credential_id_seq",
            initialValue = 5,
            allocationSize = 1
    )
    private Long id;

    @OneToOne
    @JoinColumn(name = "id_users")
    @JsonBackReference
    private User user;

    @Column
    @JsonView(View.PublicView.class)
    private String login;

    @Column
    @ToString.Exclude
    @JsonView(View.InternalView.class)
    private String password;
}


