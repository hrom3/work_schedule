package by.bsuir.repository.obsolete;

import by.bsuir.domain.Department;

import java.util.List;

@Deprecated
public interface IDepartmentRepository extends ICrudOperations<Integer, Department> {

    List<Department> findDepartmentByQuery(String query);
}
