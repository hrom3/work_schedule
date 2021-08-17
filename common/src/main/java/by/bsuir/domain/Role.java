package by.bsuir.domain;

import by.bsuir.service.viewhelper.View;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
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
            generator = "role_id"
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
    @JsonView(View.PublicView.class)
    private ESystemRoles roleName;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "users_role",
            joinColumns = @JoinColumn(name = "id_role"),
            inverseJoinColumns = @JoinColumn(name = "id_user")
    )
    @JsonIgnoreProperties("roles")
    private Set<User> users = Collections.emptySet();
}
