package by.bsuir.controller.rest;

import by.bsuir.controller.request.UserCreateRequest;
import by.bsuir.domain.User;
import by.bsuir.repository.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/hibernate/users")
@RequiredArgsConstructor
public class HibernateUserController {

    private final IUserRepository hibernateUserRepository;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<User> findAll() {
        return hibernateUserRepository.findAll();
    }

    @GetMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public User findUserById(@PathVariable Long userId) {
        return hibernateUserRepository.findOne(userId);
    }

//    @GetMapping("/search")
//    @ResponseStatus(HttpStatus.OK)
//    public List<User> userSearch(@RequestParam Integer limit,
//                                 @RequestParam String query) {
//        return hibernateUserRepository.findUsersByQuery(limit, query);
//    }

    @PutMapping("/update/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public User updateUser(@PathVariable Long userId,
                           @ModelAttribute UserCreateRequest createRequest) {
        User user = hibernateUserRepository.findOne(userId);

        user.setName(createRequest.getName());
        user.setSurname(createRequest.getSurname());
        user.setMiddleName(createRequest.getMiddleName());
        user.setEmail(createRequest.getEmail());
        user.setBirthDay(LocalDate.parse(createRequest.getBirthDay()));
        //user.setDepartmentId(createRequest.getDepartmentId());
        user.setChanged(new Timestamp(System.currentTimeMillis()));
        //user.setRateId(createRequest.getRateId());
        //user.setRoomId(createRequest.getRoomId());

        return  hibernateUserRepository.update(user);
    }

    @DeleteMapping("/delete_hard/{userId}")
    public void deleteUserHard(@PathVariable Long userId) {
        hibernateUserRepository.deleteHard(userId);
    }

//    @DeleteMapping("/delete/{userId}")
//    public List<User> deleteUser(@PathVariable Long userId) {
//        boolean isDeleted = hibernateUserRepository.delete(userId);
//
//        if (isDeleted) {
//            return hibernateUserRepository.findAll();
//        }
//        return Collections.emptyList();
//    }



}
