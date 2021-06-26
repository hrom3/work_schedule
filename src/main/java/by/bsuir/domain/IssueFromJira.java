package by.bsuir.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "issues_from_jira")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class IssueFromJira {

    @Id
    private Long id;

    @Column(name = "name_of_project")
    private String nameOfProject;

    @Column(name = "short_name_of_project")
    private String shortNameOfProject;

    @Column(name = "jira_issues_id")
    private Long jiraIssuesId;


    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }
}
