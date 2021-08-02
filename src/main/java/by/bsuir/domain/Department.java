package by.bsuir.domain;

import by.bsuir.domain.viewhelper.View;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Collections;
import java.util.Set;

@Entity
@Table(name = "department")
@Data
@NoArgsConstructor
@EqualsAndHashCode(exclude = {"users"})
public class Department {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator =  "department_id"
    )
    @SequenceGenerator(
            name = "department_id",
            sequenceName = "department_id_seq",
            initialValue = 4,
            allocationSize = 1
    )
    @JsonView(View.PublicView.class)
    private Integer id;

    @Column(name = "department_name")
    @JsonView(View.PublicView.class)
    private String departmentName;

    @OneToMany(mappedBy = "department", fetch = FetchType.EAGER)
    @JsonManagedReference
    @JsonView(View.InternalView.class)
    private Set<User> users = Collections.emptySet();
}