package by.bsuir.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Collections;
import java.util.Set;

@Entity
@Table(name = "role")
@Data
@NoArgsConstructor
@EqualsAndHashCode(exclude = {
        "users"
})
public class Role {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator =  "role_id"
    )
    @SequenceGenerator(
            name = "role_id",
            sequenceName = "role_id_seq",
            initialValue = 4,
            allocationSize = 1
    )
    private Integer id;

    @Column(name = "role_name")
    @Enumerated(EnumType.STRING)
    private ESystemRoles roleName;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "users_role",
            joinColumns = @JoinColumn(name = "id_role"),
            inverseJoinColumns = @JoinColumn(name = "id_user")
    )
    @JsonIgnoreProperties("roles")
    private Set<User> users = Collections.emptySet();
}
