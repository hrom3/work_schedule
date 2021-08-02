package by.bsuir.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonInclude;
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
    @JsonBackReference
    private User user;

    @Column
    private String work;

    @Column(name = "start_time")
    private Timestamp startTime;

    @Column(name = "end_time")
    private Timestamp endTime;

    @ManyToOne
    @JoinColumn(name = "id_issue")
    @JsonBackReference
    private IssueFromJira issueFromJira;
}
