package by.bsuir.repository;

import by.bsuir.domain.User;

import java.util.List;

public interface IUserRepository extends ICrudOperations<Long, User> {

    List<User> findUsersByQuery(Integer limit, String query);

    void batchInsert(List<User> users);
}
