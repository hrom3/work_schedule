package by.bsuir.controller.rest;

import by.bsuir.controller.exception.UnauthorizedException;
import by.bsuir.controller.request.UserCreateRequest;
import by.bsuir.domain.*;
import by.bsuir.domain.viewhelper.View;
import by.bsuir.controller.exception.NoSuchEntityException;
import by.bsuir.repository.springdata.*;
import by.bsuir.security.utils.PrincipalUtil;
import by.bsuir.util.UserGenerator;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import by.bsuir.beans.SecurityConfig;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.*;

//TODO: refactor
@RestController
@RequestMapping("/rest/users")
@RequiredArgsConstructor
public class UserRestController {

    private final UserGenerator userGenerator;

    private final SecurityConfig config;

    private final PrincipalUtil principalUtil;

    private final IUserDataRepository userDataRepository;

    private final IDepartmentDataRepository departmentRepository;

    private final IRateDataRepository rateRepository;

    private final IRoomDataRepository roomRepository;

    private final IRoleDataRepository roleRepository;


    @ApiOperation(value = "Find all users")
    @GetMapping("/findAll")
    @JsonView(View.PublicView.class)
    public ResponseEntity<List<User>> findAll() {
        return ResponseEntity.ok(userDataRepository.findAll());
    }

    @ApiOperation(value = "Find all users by authenticate user")
    @ApiImplicitParam(name = "X-Auth-Token",
            value = "token", required = true,
            dataType = "string",
            paramType = "header")
    @GetMapping
    public ResponseEntity<List<User>> securedFindAll(HttpServletRequest request,
                                                     @ApiIgnore Principal principal) {

        boolean isNoPrincipal = principalUtil.getUsername(principal).isEmpty();

        if (!isNoPrincipal) {
            return ResponseEntity.ok(userDataRepository.findAll());
        } else {
            throw new UnauthorizedException("Unable to authenticate Domain " +
                    "User for provided credentials.");
        }
    }

    @ApiOperation(value = "Find user by token")
    @ApiImplicitParam(name = "X-Auth-Token",
            value = "token", required = true,
            dataType = "string",
            paramType = "header")
    @GetMapping("/user")
    public ResponseEntity<User> securedOneByToken(@ApiIgnore Principal principal) {
        String login = principalUtil.getUsername(principal);

        Optional<User> searchResult =
                userDataRepository.findByCredentialLogin(login);
        if (searchResult.isPresent()) {
            return ResponseEntity.ok(searchResult.get());
        } else {
            throw new NoSuchEntityException("No such user with login:" + login);
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<User>> userSearch(@RequestParam Integer limit,
                                                 @RequestParam String name) {
        return ResponseEntity.ok
                (userDataRepository.findUsersByQueryName(name, limit));
    }

    @GetMapping("/search_by_name")
    public ResponseEntity<List<User>> userSearchByName(@RequestParam String query) {
        return ResponseEntity.ok
                (userDataRepository.findByNameContainingIgnoreCase(query));
    }

    @GetMapping("/search_by_surname")
    public ResponseEntity<List<User>> userSearchBySurname(@RequestParam String query) {
        return ResponseEntity.ok
                (userDataRepository.findBySurnameContainingIgnoreCase(query));
    }

    @GetMapping("/search_by_surname_or_name")
    public ResponseEntity<List<User>> userSearchBySurname(@RequestParam String name,
                                                          @RequestParam String surname) {
        return ResponseEntity.ok(userDataRepository
                .findBySurnameContainingIgnoreCaseOrNameContainingIgnoreCase
                        (name, surname));
    }

    @GetMapping("/search_by_email")
    @JsonView(View.PublicView.class)
    public ResponseEntity<List<User>> userSearchByEmail(@RequestParam String query) {
        return ResponseEntity.ok(userDataRepository
                .findByEmailContainingIgnoreCase(query));
    }

    @ApiOperation(value = "Create autogenerate users")
    @ApiImplicitParams({
//            @ApiImplicitParam(name = "usersCount", dataType = "integer",
//                    paramType = "path", value = "Count of users for generate",
//                    required = true, defaultValue = "10"),
            @ApiImplicitParam(name = "X-Auth-Token",
                    value = "token", required = true,
                    dataType = "string",
                    paramType = "header")
    })
    @PostMapping("/generate/{usersCount}")
    public ResponseEntity<List<User>> generateUsers
            (@PathVariable Integer usersCount,
             @ApiIgnore Principal principal) {

        boolean isAdmin = principalUtil.getAuthorities(principal)
                .stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals(ESystemRoles.ROLE_ADMIN.toString()));

        if (!isAdmin) {
            throw new UnauthorizedException
                    ("Insufficient permissions to perform the operation");
        }

        //TODO: refactor userGenerator.class
        List<User> generateUsers = userGenerator.generate(usersCount);

        userDataRepository.saveAll(generateUsers);
        return new ResponseEntity<>(userDataRepository.findAll(), HttpStatus.CREATED);
    }

    @ApiOperation(value = "Update user")
    @ApiImplicitParam(name = "X-Auth-Token",
            value = "token", required = true,
            dataType = "string",
            paramType = "header")
    @PutMapping("/update/{userId}")
    public ResponseEntity<User> updateUser(@PathVariable Long userId,
                                           @ModelAttribute UserCreateRequest createRequest,
                                           @ApiIgnore Principal principal) {

        boolean isAdmin = principalUtil.getAuthorities(principal)
                .stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals(ESystemRoles.ROLE_ADMIN.toString()));

        if (!isAdmin) {
            throw new UnauthorizedException("Unable to authenticate Domain " +
                    "User for provided credentials.");
        }


        Optional<User> searchResult =
                userDataRepository.findById(userId);
        User user;
        if (searchResult.isPresent()) {
            user = searchResult.get();
        } else {
            throw new NoSuchEntityException("No such user with login:" + userId);
        }

        Department department;
        Optional<Department> searchDepResult = departmentRepository
                .findById(createRequest.getDepartmentId());
        if (searchDepResult.isPresent()) {
            department = searchDepResult.get();
        } else {
            throw new NoSuchEntityException("No such room with id:"
                    + createRequest.getDepartmentId());
        }

        Rate rate;
        Optional<Rate> searchRateResult = rateRepository
                .findById(createRequest.getRateId());
        if (searchRateResult.isPresent()) {
            rate = searchRateResult.get();
        } else {
            throw new NoSuchEntityException("No such rate with id:"
                    + createRequest.getRateId());
        }

        Room room;
        Optional<Room> searchRoomResult = roomRepository
                .findById(createRequest.getRoomId());
        if (searchRoomResult.isPresent()) {
            room = searchRoomResult.get();
        } else {
            throw new NoSuchEntityException("No such room with id:"
                    + createRequest.getRoomId());
        }

        Role foundRole = roleRepository.findById(createRequest.getRoleId()).get();
        Set<Role> roles = new HashSet<>();
        roles.add(foundRole);

        user.setName(createRequest.getName());
        user.setSurname(createRequest.getSurname());
        user.setMiddleName(createRequest.getMiddleName());
        user.setEmail(createRequest.getEmail());
        user.setBirthDay(LocalDate.parse(createRequest.getBirthDay()));
        user.setDepartment(department);
        user.setChanged(new Timestamp(System.currentTimeMillis()));
        user.setRate(rate);
        user.setRoom(room);
        user.setRoles(roles);

        return ResponseEntity.ok(userDataRepository.save(user));
    }

    @ApiOperation(value = "Hard delete user by Id")
    @DeleteMapping("/delete_hard/{user_id}")
    @ApiImplicitParam(name = "X-Auth-Token",
            value = "token", required = true,
            dataType = "string",
            paramType = "header")
    public void deleteUserHard(@PathVariable("user_id") Long userId,
                               @ApiIgnore Principal principal) {

        boolean isAdmin = principalUtil.getAuthorities(principal)
                .stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals(ESystemRoles.ROLE_ADMIN.toString()));

        if (!isAdmin) {
            throw new UnauthorizedException
                    ("Insufficient permissions to perform the operation");
        }
        userDataRepository.deleteById(userId);
    }

    @ApiOperation(value = "Soft delete user by Id")
    @DeleteMapping("/delete_soft/{user_id}")
    @ApiImplicitParam(name = "X-Auth-Token",
            value = "token", required = true,
            dataType = "string",
            paramType = "header")
    public ResponseEntity<User> deleteUser(@PathVariable("user_id") Long userId,
                                           @ApiIgnore Principal principal) {

        boolean isAdmin = principalUtil.getAuthorities(principal)
                .stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals(ESystemRoles.ROLE_ADMIN.toString()));

        if (!isAdmin) {
            throw new UnauthorizedException
                    ("Insufficient permissions to perform the operation");
        }

        Optional<User> searchResult =
                userDataRepository.findById(userId);
        User user;
        if (searchResult.isPresent()) {
            user = searchResult.get();
        } else {
            throw new NoSuchEntityException("No such user with login:" + userId);
        }
        userDataRepository.softDelete(userId);

        return ResponseEntity.ok(user);
    }

    @ApiOperation(value = "Soft delete user by token")
    @DeleteMapping("/delete_soft")
    @ApiImplicitParam(name = "X-Auth-Token",
            value = "token", required = true,
            dataType = "string",
            paramType = "header")
    public ResponseEntity<String> deleteCurrentUser(@ApiIgnore Principal principal) {

        String login = principalUtil.getUsername(principal);

        Optional<User> searchResult =
                userDataRepository.findByCredentialLogin(login);

        if (searchResult.isEmpty()) {
            throw new NoSuchEntityException("No such user with login:" + login);
        }
        userDataRepository.softDelete(searchResult.get().getId());
        return ResponseEntity.ok("User has deleted");
    }
}
