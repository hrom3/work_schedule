package by.bsuir.repository;

import by.bsuir.domain.Department;

import java.util.List;

public interface IDepartmentRepository extends ICrudOperations<Integer, Department> {

    List<Department> findDepartmentByQuery(String query);
}
