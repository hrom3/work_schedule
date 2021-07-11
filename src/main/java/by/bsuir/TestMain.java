package by.bsuir;

import by.bsuir.domain.User;
import by.bsuir.exception.NoSuchEntityException;
import by.bsuir.repository.IJDBCUserRepository;
import by.bsuir.util.UserGenerator;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.dao.IncorrectResultSizeDataAccessException;

import java.util.List;

public class TestMain  {
    public static void main(String[] args) {

        AnnotationConfigApplicationContext annotationConfigApplicationContext =
                new AnnotationConfigApplicationContext("by.bsuir");

        IJDBCUserRepository userRepository =
                annotationConfigApplicationContext.getBean(IJDBCUserRepository.class);

        List<User> users = userRepository.findAll();

        UserGenerator userGenerator =
                annotationConfigApplicationContext.getBean(UserGenerator.class);

        for (User user : users) {
            System.out.println(user);
        }

        try {
            System.out.println(userRepository.findOne(1L));

        } catch (NoSuchEntityException | IncorrectResultSizeDataAccessException e) {
            System.err.println(e.getMessage());
        }

        System.out.println(userRepository.findOne(2L));

        User newUser = userGenerator.generate();
        User newSavedUser = userRepository.save(newUser);

        boolean isOk = userRepository.delete(newSavedUser.getId());

        System.out.println(isOk);

        User userToUpdate = userRepository.findOne(4L);




    }
}
