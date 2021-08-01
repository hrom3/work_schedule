package by.bsuir.repository.springdata;

import by.bsuir.domain.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface IDepartmentDataRepository extends
        CrudRepository<Department, Integer>,
        JpaRepository<Department, Integer> {

    Optional<Department> findDepartmentByDepartmentName(String departmentName);
}
