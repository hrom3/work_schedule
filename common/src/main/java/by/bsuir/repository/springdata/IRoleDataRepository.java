package by.bsuir.repository.springdata;

import by.bsuir.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;


public interface IRoleDataRepository extends
        CrudRepository<Role, Integer>,
        JpaRepository<Role, Integer> {

}
