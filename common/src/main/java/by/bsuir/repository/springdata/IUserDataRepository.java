package by.bsuir.repository.springdata;

import by.bsuir.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface IUserDataRepository extends CrudRepository<User, Long>,
        PagingAndSortingRepository<User, Long>,
        JpaRepository<User, Long> {


    Optional<User> findByCredentialLogin(String login);


    @Transactional(propagation = Propagation.REQUIRED,
            isolation = Isolation.DEFAULT,
            rollbackFor = SQLException.class)
    @Modifying
    @Query(value = "insert into users_role(id_user, id_role) values " +
            "(:user_id, :role_id)", nativeQuery = true)
    int saveUserRole(@Param("user_id") Long userId,
                     @Param("role_id") Integer roleId);


    @Transactional(propagation = Propagation.REQUIRED,
            isolation = Isolation.DEFAULT,
            rollbackFor = SQLException.class)
    @Modifying
    @Query("update User u set u.isDeleted = false where u.id = :user_id")
    int softDelete(@Param("user_id") Long userId);


    @Query(value = "select * from users u where u.name like :name limit :limit",
            nativeQuery = true)
    List<User> findUsersByQueryName(String name, Integer limit);


    List<User> findByNameContainingIgnoreCase(String query);


    List<User> findBySurnameContainingIgnoreCase(String query);


    List<User> findBySurnameContainingIgnoreCaseOrNameContainingIgnoreCase
            (String surname, String name);


    List<User> findByEmailContainingIgnoreCase(String query);


}