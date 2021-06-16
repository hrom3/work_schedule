package by.bsuir.repository;

import by.bsuir.domain.Role;
import by.bsuir.domain.User;

import java.util.List;

public interface IUserRepository extends ICrudOperations<Long, User> {

    List<User> findUsersByQuery(Integer limit, String query);

    void batchInsert(List<User> users);

    boolean delete(Long id);

    void saveUserRoles(User user, List<Role> roles);

    User findByLoginAndPassword(String login, String password);
}
