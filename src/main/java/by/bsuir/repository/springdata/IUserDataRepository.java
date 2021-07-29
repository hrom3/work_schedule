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

public interface IUserDataRepository extends CrudRepository<User, Long>,
        PagingAndSortingRepository<User, Long>,
        JpaRepository<User, Long> {

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT,
            rollbackFor = SQLException.class)
    @Modifying
    @Query(value = "insert into users_role(id_user, id_role) values " +
            "(:user_id, :role_id)", nativeQuery = true)
    int saveUserRole(@Param("user_id") Long userId, @Param("role_id") Integer roleId);

}
