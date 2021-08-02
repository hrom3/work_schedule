package by.bsuir.domain;

import by.bsuir.domain.viewhelper.View;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.*;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Set;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@EqualsAndHashCode(exclude = {"roles", "userWorkedTimes"})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class User {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator =  "users_id"
    )
    @SequenceGenerator(
            name = "users_id",
            sequenceName = "users_id_seq",
            initialValue = 5,
            allocationSize = 1
    )
    @JsonView(View.PublicView.class)
    private Long id;

    @Column
    @JsonView(View.PublicView.class)
    private String surname;

    @Column
    @JsonView(View.PublicView.class)
    private String name;

    @Column(name = "middle_name")
    @JsonView(View.PublicView.class)
    private String middleName;

    @Column
    @JsonView(View.PublicView.class)
    private String email;

    @Column(name = "birth_day")
    @JsonView(View.PublicView.class)
    private LocalDate birthDay;

    @ManyToOne
    @JoinColumn(name = "department_id")
    @JsonIgnoreProperties("users")
    @JsonView(View.PublicView.class)
    private Department department;

    @Column
    @JsonView(View.PublicView.class)
    private Timestamp created;

    @Column
    @JsonView(View.PublicView.class)
    private Timestamp changed;

    @Column(name = "is_deleted")
    @JsonView(View.PublicView.class)
    private Boolean isDeleted;

    @Column(name = "is_confirmed")
    @JsonView(View.PublicView.class)
    private Boolean isConfirmed;

    @ManyToOne
    @JoinColumn(name = "rate_id")
    @JsonManagedReference
    @JsonView(View.InternalView.class)
    private Rate rate;

    @ManyToOne
    @JoinColumn(name = "room_id")
    @JsonManagedReference
    @JsonView(View.PublicView.class)
    private Room room;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonManagedReference
    @JsonView(View.PublicView.class)
    //@JsonIgnoreProperties("user")
    //@JsonIdentityInfo(generator= ObjectIdGenerators.UUIDGenerator.class, property="@id")
    private Credential credential;

    @ManyToMany(mappedBy = "users", fetch = FetchType.EAGER)
    @JsonIgnoreProperties("users")
    @JsonView(View.PublicView.class)
    private Set<Role> roles = Collections.emptySet();

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    @JsonIgnoreProperties("user")
    @JsonView(View.PublicView.class)
    private Set<UserWorkedTime> userWorkedTimes = Collections.emptySet();
}
