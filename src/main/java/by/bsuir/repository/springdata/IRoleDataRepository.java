package by.bsuir.repository.springdata;

import by.bsuir.domain.Rate;
import by.bsuir.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface IRoleDataRepository extends
        CrudRepository<Role, Integer>,
        JpaRepository<Role, Integer> {

}
