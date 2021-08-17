package by.bsuir.controller.rest;

import by.bsuir.controller.request.UserUpdateRequest;
import by.bsuir.domain.Department;
import by.bsuir.domain.ESystemRoles;
import by.bsuir.domain.Rate;
import by.bsuir.domain.Role;
import by.bsuir.domain.Room;
import by.bsuir.domain.User;
import by.bsuir.service.viewhelper.View;
import by.bsuir.exception.NoSuchEntityException;
import by.bsuir.exception.UnauthorizedException;
import by.bsuir.repository.RepositoryUtils;
import by.bsuir.repository.springdata.IDepartmentDataRepository;
import by.bsuir.repository.springdata.IRateDataRepository;
import by.bsuir.repository.springdata.IRoleDataRepository;
import by.bsuir.repository.springdata.IRoomDataRepository;
import by.bsuir.repository.springdata.IUserDataRepository;
import by.bsuir.security.utils.PrincipalUtil;
import by.bsuir.util.MyMessages;
import by.bsuir.util.UserGenerator;

import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.security.Principal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

//TODO: refactor
@Api(value = "Employees controller")
@RestController
@RequestMapping("/rest/users")
@RequiredArgsConstructor
public class UserRestController {

    private final UserGenerator userGenerator;

    private final PrincipalUtil principalUtil;

    private final IUserDataRepository userDataRepository;

    private final IDepartmentDataRepository departmentRepository;

    private final IRateDataRepository rateRepository;

    private final IRoomDataRepository roomRepository;

    private final IRoleDataRepository roleRepository;

    private final RepositoryUtils repositoryUtils;


    @ApiOperation(value = "Find all employees")
    @GetMapping("/findAll")
    @JsonView(View.PublicView.class)
    public ResponseEntity<List<User>> findAll() {
        return ResponseEntity.ok(userDataRepository.findAll());
    }


    @ApiOperation(value = "Find all employees by authenticate user")
    @ApiImplicitParam(name = "X-Auth-Token",
            value = "token", required = true,
            dataType = "string",
            paramType = "header")
    @JsonView(View.InternalView.class)
    @GetMapping
    public ResponseEntity<List<User>> securedFindAll(@ApiIgnore Principal principal) {

        boolean isNoPrincipal = principalUtil.getUsername(principal).isEmpty();

        if (!isNoPrincipal) {
            return ResponseEntity.ok(userDataRepository.findAll());
        } else {
            throw new UnauthorizedException("Unable to authenticate Domain " +
                    "User for provided credentials.");
        }
    }


    @ApiOperation(value = "Find employee by token")
    @ApiImplicitParam(name = "X-Auth-Token",
            value = "token", required = true,
            dataType = "string",
            paramType = "header")
    @JsonView(View.InternalView.class)
    @GetMapping("/user")
    public ResponseEntity<User> securedOneByToken(@ApiIgnore Principal principal) {
        String login = principalUtil.getUsername(principal);

        Optional<User> searchResult =
                userDataRepository.findByCredentialLogin(login);
        if (searchResult.isPresent()) {
            return ResponseEntity.ok(searchResult.get());
        } else {
            throw new NoSuchEntityException(MyMessages.NO_SUCH_USER + login);
        }
    }


    @ApiOperation(value = "Find employee by name with limit")
    @JsonView(View.PublicView.class)
    @GetMapping("/search")
    public ResponseEntity<List<User>> userSearch(@RequestParam Integer limit,
                                                 @RequestParam String name) {
        return ResponseEntity.ok
                (userDataRepository.findUsersByQueryName(name, limit));
    }


    @ApiOperation(value = "Find employee by name query")
    @JsonView(View.PublicView.class)
    @GetMapping("/search_by_name")
    public ResponseEntity<List<User>> userSearchByName(@RequestParam String query) {
        return ResponseEntity.ok
                (userDataRepository.findByNameContainingIgnoreCase(query));
    }


    @ApiOperation(value = "Find employee by surname query")
    @JsonView(View.PublicView.class)
    @GetMapping("/search_by_surname")
    public ResponseEntity<List<User>> userSearchBySurname(@RequestParam String query) {
        return ResponseEntity.ok
                (userDataRepository.findBySurnameContainingIgnoreCase(query));
    }


    @ApiOperation(value = "Find employee by name or surname query")
    @JsonView(View.PublicView.class)
    @GetMapping("/search_by_surname_or_name")
    public ResponseEntity<List<User>> userSearchBySurname(@RequestParam String name,
                                                          @RequestParam String surname) {
        return ResponseEntity.ok(userDataRepository
                .findBySurnameContainingIgnoreCaseOrNameContainingIgnoreCase
                        (name, surname));
    }


    @ApiOperation(value = "Find employee by email")
    @GetMapping("/search_by_email")
    @JsonView(View.PublicView.class)
    public ResponseEntity<List<User>> userSearchByEmail(@RequestParam String query) {
        return ResponseEntity.ok(userDataRepository
                .findByEmailContainingIgnoreCase(query));
    }


    @ApiOperation(value = "Create autogenerate users")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "usersCount", dataType = "integer",
                    paramType = "path", value = "Count of users for generate",
                    required = true, defaultValue = "10"),
            @ApiImplicitParam(name = "X-Auth-Token",
                    value = "token", required = true,
                    dataType = "string",
                    paramType = "header")
    })
    @PostMapping("/generate/{usersCount}")
    public ResponseEntity<List<User>> generateUsers
            (@PathVariable Integer usersCount,
             @ApiIgnore Principal principal) {

        boolean isAdmin;
        isAdmin = principalUtil.getAuthorities(principal)
                .stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals(ESystemRoles.ROLE_ADMIN.toString()));

        if (!isAdmin) {
            throw new UnauthorizedException(MyMessages.BAD_PERMISSIONS);
        }

        List<User> generateUsers = userGenerator.generate(usersCount);
        Set<Role> roles = new HashSet<>();
        roles.add(roleRepository.findById(3).get());

        for (User user : generateUsers) {
            user.setDepartment(departmentRepository.findById(RandomUtils
                    .nextInt(1, 3)).get());
            user.setRate(rateRepository.findById(RandomUtils.nextInt(1, 7)).get());
            user.setRoom(roomRepository.findById(RandomUtils.nextInt(1, 6)).get());
            user.setRoles(roles);
            userDataRepository.save(user);
            for (Role role : roles) {
                userDataRepository.saveUserRole(user.getId(), role.getId());
            }
        }
        return ResponseEntity.ok(userDataRepository.findAll());
    }

    @ApiOperation(value = "Update employee")
    @ApiImplicitParam(name = "X-Auth-Token",
            value = "token", required = true,
            dataType = "string",
            paramType = "header")
    @PutMapping("/update/{userId}")
    public ResponseEntity<User> updateUser(@PathVariable Long userId,
                                           @ModelAttribute UserUpdateRequest createRequest,
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
            throw new NoSuchEntityException(MyMessages.NO_SUCH_USER_ID + userId);
        }

        Department department = repositoryUtils.findDepartmentById(
                departmentRepository, createRequest.getDepartmentId());

        Rate rate = repositoryUtils.findRateById(rateRepository,
                createRequest.getRateId());

        Room room = repositoryUtils.findRoomById(roomRepository,
                createRequest.getRoomId());

        Role foundRole = roleRepository.findById(createRequest.getRoleId()).get();
        Set<Role> userRoles = user.getRoles();
        //Set<Role> roles = Set.copyOf(roleRepository.findAllById(createRequest.getRoleIds()));

        user.setName(createRequest.getName());
        user.setSurname(createRequest.getSurname());
        user.setMiddleName(createRequest.getMiddleName());
        user.setEmail(createRequest.getEmail());
        user.setBirthDay(LocalDate.parse(createRequest.getBirthDay()));
        user.setDepartment(department);
        user.setChanged(new Timestamp(System.currentTimeMillis()));
        user.setRate(rate);
        user.setRoom(room);
        //user.setRoles(roles);

        userDataRepository.save(user);

        if (!userRoles.contains(foundRole)) {
            userDataRepository.saveUserRole(user.getId(), foundRole.getId());
        }
        return ResponseEntity.ok(user);
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
            throw new UnauthorizedException(MyMessages.BAD_PERMISSIONS);
        }
        userDataRepository.deleteById(userId);
    }

    @ApiOperation(value = "Soft delete employee by Id")
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
            throw new UnauthorizedException(MyMessages.BAD_PERMISSIONS);
        }

        Optional<User> searchResult =
                userDataRepository.findById(userId);
        User user;
        if (searchResult.isPresent()) {
            user = searchResult.get();
        } else {
            throw new NoSuchEntityException(MyMessages.NO_SUCH_USER_ID + userId);
        }
        userDataRepository.softDelete(userId);

        return ResponseEntity.ok(user);
    }

    @ApiOperation(value = "Soft delete employee by token")
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
            throw new NoSuchEntityException(MyMessages.NO_SUCH_USER + login);
        }
        userDataRepository.softDelete(searchResult.get().getId());
        return ResponseEntity.ok("User has deleted");
    }
}
