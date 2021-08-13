package by.bsuir.repository.obsolete;

import by.bsuir.domain.Role;
import by.bsuir.domain.User;

import java.util.List;

/**
 * @deprecated (Use Spring Data Repositories)
 */

@Deprecated(since = "version 0.1.20210731")
public interface IJDBCUserRepository extends ICrudOperations<Long, User> {

    List<User> findUsersByQuery(Integer limit, String query);

    void batchInsert(List<User> users);

    boolean delete(Long id);

    void saveUserRoles(User user, List<Role> roles);

    User findByLoginAndPassword(String login, String password);

    User findUserByLogin(String login);

}
