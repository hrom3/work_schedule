package by.bsuir.controller.rest;

import by.bsuir.domain.User;
import by.bsuir.repository.IHibernateUserRepository;
import by.bsuir.repository.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/hibernate/users")
@RequiredArgsConstructor
public class HibernateUserController {

    private final IHibernateUserRepository hibernateUserRepository;

    @GetMapping
    public List<User> findAll() {
        return hibernateUserRepository.findAll();
    }

    @GetMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public User findUserById(@PathVariable Long userId) {
        return hibernateUserRepository.findOne(userId);
    }



}
