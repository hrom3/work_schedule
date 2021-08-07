package by.bsuir.repository;

import by.bsuir.controller.exception.NoSuchEntityException;
import by.bsuir.domain.Department;
import by.bsuir.domain.Rate;
import by.bsuir.domain.Room;
import by.bsuir.repository.springdata.IDepartmentDataRepository;
import by.bsuir.repository.springdata.IRateDataRepository;
import by.bsuir.repository.springdata.IRoomDataRepository;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor
public class RepositoryUtils {



    public Department findDepartmentById(
            IDepartmentDataRepository departmentRepository, Integer id) {
        Optional<Department> searchDepResult = departmentRepository.findById(id);
        if (searchDepResult.isPresent()) {
            return searchDepResult.get();
        } else {
            throw new NoSuchEntityException("No such room with id: " + id);
        }
    }

    public Rate findRateById(IRateDataRepository rateRepository, Integer id) {
        Optional<Rate> searchRateResult = rateRepository.findById(id);
        if (searchRateResult.isPresent()) {
            return searchRateResult.get();
        } else {
            throw new NoSuchEntityException("No such rate with id:" + id);
        }
    }

    public Room findRoomById(IRoomDataRepository roomRepository, Integer id) {
        Optional<Room> searchRoomResult = roomRepository.findById(id);
        if (searchRoomResult.isPresent()) {
            return searchRoomResult.get();
        } else {
            throw new NoSuchEntityException("No such room with id:" + id);
        }
    }
}