package by.bsuir.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "issues_from_jira")
@Data
@NoArgsConstructor
public class IssueFromJira {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "issues_from_jira_id"
    )
    @SequenceGenerator(
            name = "issues_from_jira_id",
            sequenceName = "issues_from_jira_id_seq",
            initialValue = 5,
            allocationSize = 1
    )
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
