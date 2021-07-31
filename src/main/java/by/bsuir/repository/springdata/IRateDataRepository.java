package by.bsuir.repository.springdata;

import by.bsuir.domain.Rate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface IRateDataRepository extends
        CrudRepository<Rate, Integer>,
        JpaRepository<Rate, Integer> {
}
