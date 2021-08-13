package by.bsuir.repository.springdata;

import by.bsuir.domain.Room;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface IRoomDataRepository extends
        CrudRepository<Room, Integer>,
        JpaRepository<Room, Integer> {

    Optional<Room> findByRoomNumberIgnoreCase(String number);
}