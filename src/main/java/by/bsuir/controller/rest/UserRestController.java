package by.bsuir.controller.rest;

import by.bsuir.controller.exception.UnauthorizedException;
import by.bsuir.controller.request.UserCreateRequest;
import by.bsuir.domain.User;
import by.bsuir.repository.IUserRepository;
import by.bsuir.security.utils.PrincipalUtil;
import by.bsuir.util.UserGenerator;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import by.bsuir.beans.SecurityConfig;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;


@RestController
@RequestMapping("/rest/users")
@RequiredArgsConstructor
public class UserRestController {
    private final IUserRepository userRepository;
    private final UserGenerator userGenerator;
    private final SecurityConfig config;
    private final PrincipalUtil principalUtil;

    @ApiOperation(value = "Find all users")
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @ApiOperation(value = "Find all users with Secret key")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Secret-Key", dataType = "string",
                    paramType = "header", value = "Secret key for secret functionality"),
            @ApiImplicitParam(name = "X-Auth-Token", value = "token", required = true,
                    dataType = "string", paramType = "header")
    })
    @GetMapping("/hello")
    public List<User> securedFindAll(HttpServletRequest request,
                                     @ApiIgnore Principal principal) {
        String login = principalUtil.getUsername(principal);
        String secretKey = request.getHeader("Secret-Key");


        if (StringUtils.isNotBlank(secretKey) && secretKey.equals(config.getSecretKey())) {
           // return userRepository.findAll();
            return Collections.singletonList(userRepository.findUserByLogin(login));

        } else {
          throw new UnauthorizedException("Unable to authenticate Domain " +
                  "User for provided credentials.");
        }
    }

    @GetMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public User findUserById(@PathVariable Long userId) {
        return userRepository.findOne(userId);
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public List<User> userSearch(@RequestParam Integer limit,
                                 @RequestParam String query) {
        return userRepository.findUsersByQuery(limit, query);
    }


    @ApiOperation(value = "Create autogenerate users")
    @ApiImplicitParams({
            @ApiImplicitParam( name = "usersCount", dataType = "integer",
                    paramType = "path", value = "Count of users for generate",
            required = true, defaultValue = "10")
    })
    @PostMapping("/generate/{usersCount}")
    @ResponseStatus(HttpStatus.CREATED)
    public List<User> generateUsers(@PathVariable("usersCount") Integer count) {
//        throw new RuntimeException("Haha!");
       List<User> generateUsers = userGenerator.generate(count);
       userRepository.batchInsert(generateUsers);

       return userRepository.findAll();
    }

    @PutMapping("/update/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public User updateUser(@PathVariable Long userId,
                           @ModelAttribute UserCreateRequest createRequest) {
        User user = userRepository.findOne(userId);

        user.setName(createRequest.getName());
        user.setSurname(createRequest.getSurname());
        user.setMiddleName(createRequest.getMiddleName());
        user.setEmail(createRequest.getEmail());
        user.setBirthDay(LocalDate.parse(createRequest.getBirthDay()));
        user.setDepartmentId(createRequest.getDepartmentId());
        user.setChanged(new Timestamp(System.currentTimeMillis()));
        user.setRateId(createRequest.getRateId());
        user.setRoomId(createRequest.getRoomId());

        return  userRepository.update(user);
    }

    @DeleteMapping("/delete_hard/{userId}")
    public void deleteUserHard(@PathVariable Long userId) {
        userRepository.deleteHard(userId);
    }

    @DeleteMapping("/delete/{userId}")
    public List<User> deleteUser(@PathVariable Long userId) {
       boolean isDeleted = userRepository.delete(userId);

       if (isDeleted) {
           return userRepository.findAll();
       }
       return Collections.emptyList();
    }
}
