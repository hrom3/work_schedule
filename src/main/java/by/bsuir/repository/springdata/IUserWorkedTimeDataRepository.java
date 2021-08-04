package by.bsuir.repository.springdata;

import by.bsuir.domain.UserWorkedTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface IUserWorkedTimeDataRepository extends
        CrudRepository<UserWorkedTime, Long>,
        PagingAndSortingRepository<UserWorkedTime, Long>,
        JpaRepository<UserWorkedTime, Long> {

}