package by.bsuir.repository;

import by.bsuir.domain.Rate;

import java.util.List;

public interface IRateRepository extends ICrudOperations<Integer, Rate> {

    List<Rate> findDepartmentByQuery(String query);
}
