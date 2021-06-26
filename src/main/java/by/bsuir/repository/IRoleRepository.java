package by.bsuir.repository;

import by.bsuir.domain.Role;
import by.bsuir.domain.User;

import java.util.List;

public interface IRoleRepository extends ICrudOperations<Integer, Role> {


    List<Role> getUserRoles(User user);
}
