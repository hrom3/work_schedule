package by.bsuir.repository.springdata;

import by.bsuir.domain.ConfirmationData;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface IConfirmationDataRepository
        extends CrudRepository<ConfirmationData, Long>,
        PagingAndSortingRepository<ConfirmationData, Long>,
        JpaRepository<ConfirmationData, Long> {


}
