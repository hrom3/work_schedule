package by.bsuir.controller.rest;

import by.bsuir.controller.exception.UnauthorizedException;
import by.bsuir.controller.request.UserCreateRequest;
import by.bsuir.domain.User;
import by.bsuir.exception.NoSuchEntityException;
import by.bsuir.repository.*;
import by.bsuir.repository.springdata.IUserDataRepository;
import by.bsuir.security.utils.PrincipalUtil;
import by.bsuir.util.UserGenerator;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import by.bsuir.beans.SecurityConfig;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

//TODO: refactor
@RestController
@RequestMapping("/rest/users")
@RequiredArgsConstructor
public class UserRestController {

    private final UserGenerator userGenerator;

    private final SecurityConfig config;

    private final PrincipalUtil principalUtil;

    private final IUserDataRepository userDataRepository;

    private final IDepartmentRepository departmentRepository;

    private final IRateRepository rateRepository;

    private final IRoomsRepository roomRepository;

    @ApiOperation(value = "Find all users")
    @GetMapping("/hello")
    @ResponseStatus(HttpStatus.OK)
    public List<User> findAll() {
        return userDataRepository.findAll();
    }

    @ApiOperation(value = "Find all users with Secret key and authenticate")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Secret-Key",
                    dataType = "string",
                    defaultValue = "Secret-Key!",
                    paramType = "header",
                    value = "Secret key for secret functionality"),
            @ApiImplicitParam(name = "X-Auth-Token",
                    value = "token", required = true,
                    dataType = "string",
                    paramType = "header")
    })
    @GetMapping
    public List<User> securedFindAll(HttpServletRequest request,
                                     @ApiIgnore Principal principal) {
        String login = principalUtil.getUsername(principal);
        String secretKey = request.getHeader("Secret-Key");


        if (StringUtils.isNotBlank(secretKey) && secretKey.
                equals(config.getSecretKey())) {
            return userDataRepository.findAll();
        } else {
            throw new UnauthorizedException("Unable to authenticate Domain " +
                    "User for provided credentials.");
        }
    }

    @ApiOperation(value = "Find user by token with Secret key")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Secret-Key",
                    dataType = "string",
                    defaultValue = "Secret-Key!",
                    paramType = "header",
                    value = "Secret key for secret functionality"),
            @ApiImplicitParam(name = "X-Auth-Token",
                    value = "token",
                    required = true,
                    dataType = "string",
                    paramType = "header")
    })
    @GetMapping("/user")
    public User securedOneByToken(HttpServletRequest request,
                                  @ApiIgnore Principal principal) {
        String login = principalUtil.getUsername(principal);
        String secretKey = request.getHeader("Secret-Key");


        if (StringUtils.isNotBlank(secretKey) && secretKey.
                equals(config.getSecretKey())) {

            Optional<User> searchResult =
                    userDataRepository.findUserByCredentialLogin(login);
            if (searchResult.isPresent()) {
                return searchResult.get();
            } else {
                throw new NoSuchEntityException("No such user with login:" + login);
            }
        } else {
            throw new UnauthorizedException("Unable to authenticate Domain " +
                    "User for provided credentials.");
        }
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public List<User> userSearch(@RequestParam Integer limit,
                                 @RequestParam String query) {
        return userDataRepository.findUsersByQuery(query, limit);
    }


    @ApiOperation(value = "Create autogenerate users")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "usersCount", dataType = "integer",
                    paramType = "path", value = "Count of users for generate",
                    required = true, defaultValue = "10")
    })
    @PostMapping("/generate/{usersCount}")
    @ResponseStatus(HttpStatus.CREATED)
    public List<User> generateUsers(@PathVariable("usersCount") Integer count) {
//        throw new RuntimeException("Haha!");
        List<User> generateUsers = userGenerator.generate(count);
        userDataRepository.saveAll(generateUsers);

        return userDataRepository.findAll();
    }

    @PutMapping("/update/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public User updateUser(@PathVariable Long userId,
                           @ModelAttribute UserCreateRequest createRequest) {
        Optional<User> searchResult =
                userDataRepository.findById(userId);
        User user;
        if (searchResult.isPresent()) {
            user = searchResult.get();
        } else {
            throw new NoSuchEntityException("No such user with login:" + userId);
        }

        user.setName(createRequest.getName());
        user.setSurname(createRequest.getSurname());
        user.setMiddleName(createRequest.getMiddleName());
        user.setEmail(createRequest.getEmail());
        user.setBirthDay(LocalDate.parse(createRequest.getBirthDay()));
        user.setDepartment(departmentRepository.findOne(createRequest.getDepartmentId()));
        user.setChanged(new Timestamp(System.currentTimeMillis()));
        user.setRate(rateRepository.findOne(createRequest.getRateId()));
        user.setRoom(roomRepository.findOne(createRequest.getRoomId()));
        //user.setRoles(roleRepository.findOne(createRequest.getRoleId()));

        return userDataRepository.save(user);
    }

    @DeleteMapping("/delete_hard/{userId}")
    public void deleteUserHard(@PathVariable Long userId) {
        userDataRepository.deleteById(userId);
    }

    @DeleteMapping("/delete/{userId}")
    public User deleteUser(@PathVariable Long userId) {

        Optional<User> searchResult =
                userDataRepository.findById(userId);
        User user;
        if (searchResult.isPresent()) {
            user = searchResult.get();
        } else {
            throw new NoSuchEntityException("No such user with login:" + userId);
        }

        userDataRepository.softDelete(userId);

        return user;
    }

}
