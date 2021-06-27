package by.bsuir.repository;

import by.bsuir.domain.User;

import java.util.Optional;

public interface IHibernateUserRepository extends ICrudOperations<Long, User> {

    Optional<User> findById(Long id);

}
