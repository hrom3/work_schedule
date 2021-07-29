package by.bsuir.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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
    private Long id;

    @Column
    private String surname;

    @Column
    private String name;

    @Column(name = "middle_name")
    private String middleName;

    @Column
    private String email;

    @Column(name = "birth_day")
    private LocalDate birthDay;

    @ManyToOne
    @JoinColumn(name = "department_id")
    @JsonIgnoreProperties("users")
    private Department department;

    @Column
    private Timestamp created;

    @Column
    private Timestamp changed;

    @Column(name = "is_deleted")
    private Boolean isDeleted;

    @Column(name = "is_confirmed")
    private Boolean isConfirmed;

    @ManyToOne
    @JoinColumn(name = "rate_id")
    @JsonManagedReference
    private Rate rate;

    @ManyToOne
    @JoinColumn(name = "room_id")
    @JsonManagedReference
    private Room room;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonManagedReference
    //@JsonIgnoreProperties("user")
    //@JsonIdentityInfo(generator= ObjectIdGenerators.UUIDGenerator.class, property="@id")
    private Credential credential;

    @ManyToMany(mappedBy = "users", fetch = FetchType.EAGER)
    @JsonIgnoreProperties("users")
    private Set<Role> roles = Collections.emptySet();

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    @JsonIgnoreProperties("user")
    private Set<UserWorkedTime> userWorkedTimes = Collections.emptySet();
}
