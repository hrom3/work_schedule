package by.bsuir.repository.springdata;

import by.bsuir.domain.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface IRoomDataRepository extends
        CrudRepository<Room, Integer>,
        JpaRepository<Room, Integer> {
}
