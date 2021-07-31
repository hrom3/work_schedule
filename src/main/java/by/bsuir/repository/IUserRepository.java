package by.bsuir.repository;

import by.bsuir.domain.User;

import java.util.List;
import java.util.Optional;

@Deprecated
public interface IUserRepository extends ICrudOperations<Long, User> {

    Optional<User> findById(Long id);

    public void delete(Long id);

    List<User> findUsersByQuery(Integer limit, String name);

    User findUserByLogin(String login);

}
