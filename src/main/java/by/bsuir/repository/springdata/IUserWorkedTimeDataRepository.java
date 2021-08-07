package by.bsuir.repository.springdata;

import by.bsuir.domain.IssueFromJira;
import by.bsuir.domain.UserWorkedTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.sql.Timestamp;
import java.util.List;

public interface IUserWorkedTimeDataRepository extends
        CrudRepository<UserWorkedTime, Long>,
        PagingAndSortingRepository<UserWorkedTime, Long>,
        JpaRepository<UserWorkedTime, Long> {

    List<UserWorkedTime> findAllByUserId(Long userid);

    List<UserWorkedTime> findAllByStartTimeAndEndTimeOrderByStartTime(Timestamp startTime, Timestamp endTime);

    List<UserWorkedTime> findAllByStartTimeAfterOrderByStartTime(Timestamp startTime);

    List<UserWorkedTime> findAllByStartTimeAfter(Timestamp startTime);

    List<UserWorkedTime> findAllByStartTimeBetween(Timestamp time1, Timestamp time2);

    List<UserWorkedTime> findAllByStartTimeBefore(Timestamp startTime);

    List<UserWorkedTime> findAllByIssueFromJira(IssueFromJira issue);


}