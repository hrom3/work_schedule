package by.bsuir.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Collections;
import java.util.Set;

@Entity
@Table(name = "issues_from_jira")
@Data
@NoArgsConstructor
//@EqualsAndHashCode(exclude = {"userWorkedTimes"})
public class IssueFromJira {

    @Id
    private Long id;

    @Column(name = "name_of_project")
    private String nameOfProject;

    @Column(name = "short_name_of_project")
    private String shortNameOfProject;

    @Column(name = "jira_issues_id")
    private Long jiraIssuesId;

    //@OneToMany
    //@JsonManagedReference
    //private Set<UserWorkedTime> userWorkedTimes = Collections.emptySet();
}
