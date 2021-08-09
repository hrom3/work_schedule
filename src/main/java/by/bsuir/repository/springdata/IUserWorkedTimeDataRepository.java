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

    List<UserWorkedTime> findByUserId(Long userid);

    List<UserWorkedTime> findByUserIdAndStartTimeAndEndTime
            (Long userId, Timestamp startTime, Timestamp endTime);

    List<UserWorkedTime> findByUserIdAndStartTimeAfterOrderByStartTime
            (Long userId, Timestamp startTime);

    List<UserWorkedTime> findByUserIdAndStartTimeAfter
            (Long userId, Timestamp startTime);

    List<UserWorkedTime> findByUserIdAndStartTimeBetween
            (Long userId, Timestamp time1, Timestamp time2);

    List<UserWorkedTime> findByUserIdAndStartTimeBefore
            (Long userId, Timestamp startTime);

    List<UserWorkedTime> findByIssueFromJira
            (IssueFromJira issue);


}