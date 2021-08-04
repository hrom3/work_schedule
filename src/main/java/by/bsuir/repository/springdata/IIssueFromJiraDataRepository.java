package by.bsuir.repository.springdata;

import by.bsuir.domain.IssueFromJira;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface IIssueFromJiraDataRepository extends
        CrudRepository<IssueFromJira, Long>,
        PagingAndSortingRepository<IssueFromJira, Long>,
        JpaRepository<IssueFromJira, Long> {

}