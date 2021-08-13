package by.bsuir.domain;

import by.bsuir.domain.viewhelper.View;
import com.fasterxml.jackson.annotation.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "user_worked_time")
@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserWorkedTime {

    @Id
    @JsonView(View.PublicView.class)
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator =  "user_worked_time_id"
    )
    @SequenceGenerator(
            name = "user_worked_time_id",
            sequenceName = "user_worked_time_id_seq",
            initialValue = 5,
            allocationSize = 1
    )
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_users")
    @JsonView(View.ExtendedPublicView.class)
    @JsonIgnoreProperties("user")
    private User user;

    @Column
    @JsonView(View.PublicView.class)
    private String work;

    @Column(name = "start_time")
    @JsonView(View.PublicView.class)
    private Timestamp startTime;

    @Column(name = "end_time")
    @JsonView(View.PublicView.class)
    private Timestamp endTime;

    @ManyToOne
    @JoinColumn(name = "id_issue")
    @JsonBackReference
    private IssueFromJira issueFromJira;
}
