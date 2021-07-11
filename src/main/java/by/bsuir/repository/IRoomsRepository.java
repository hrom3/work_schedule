package by.bsuir.repository;

import by.bsuir.domain.Room;

import java.util.List;

public interface IRoomsRepository extends ICrudOperations<Integer, Room> {

    List<Room> findDepartmentByQuery(String query);
}
