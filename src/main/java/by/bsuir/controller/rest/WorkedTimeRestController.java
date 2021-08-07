package by.bsuir.controller.rest;

import by.bsuir.controller.exception.NoSuchEntityException;
import by.bsuir.controller.exception.PresentEntityException;
import by.bsuir.controller.exception.UnauthorizedException;
import by.bsuir.controller.request.UserWorkedTimeRequest;
import by.bsuir.domain.ESystemRoles;
import by.bsuir.domain.Room;
import by.bsuir.domain.User;
import by.bsuir.domain.UserWorkedTime;
import by.bsuir.domain.viewhelper.View;
import by.bsuir.repository.RepositoryUtils;
import by.bsuir.repository.springdata.IRoomDataRepository;
import by.bsuir.repository.springdata.IUserDataRepository;
import by.bsuir.repository.springdata.IUserWorkedTimeDataRepository;
import by.bsuir.security.utils.PrincipalUtil;
import by.bsuir.util.MyMessages;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/rest/work_time")
@RequiredArgsConstructor
public class WorkedTimeRestController {

    private final IUserWorkedTimeDataRepository userWorkedTimeDataRepository;

    private final IUserDataRepository userDataRepository;

    private final PrincipalUtil principalUtil;

    @GetMapping
    @ApiOperation(value = "Find all worked time")
    @JsonView(View.PublicView.class)
    public ResponseEntity<List<UserWorkedTime>> findAll() {
        return ResponseEntity.ok(userWorkedTimeDataRepository.findAll());
    }

    @GetMapping("/search/{user_id}")
    @ApiOperation(value = "Find all worked time for user")
    @JsonView(View.PublicView.class)
    public ResponseEntity<List<UserWorkedTime>> findAllForUser
            (@PathVariable("user_id") Long userId) {
        return ResponseEntity.ok(userWorkedTimeDataRepository.findAllByUserId(userId));
    }

    @GetMapping("/user")
    @ApiOperation(value = "Find all worked time for user by token")
    @ApiImplicitParam(name = "X-Auth-Token",
            value = "token", required = true,
            dataType = "string",
            paramType = "header")
    @JsonView(View.InternalView.class)
    public ResponseEntity<List<UserWorkedTime>> findAllForUserByCreds
            (@ApiIgnore Principal principal) {
        String login = principalUtil.getUsername(principal);
        Optional<User> searchResult =
                userDataRepository.findByCredentialLogin(login);
        if (searchResult.isEmpty()) {
            throw new NoSuchEntityException(MyMessages.NO_SUCH_USER + login);
        }
        Long foundUserId = searchResult.get().getId();
        return ResponseEntity.ok
                (userWorkedTimeDataRepository.findAllByUserId(foundUserId));

    }


        UserWorkedTime foundUserWorkedTime = repositoryUtils.findUserWorkedTimeById(roomRepository, id);
        return ResponseEntity.ok(foundUserWorkedTime);
    }

    @ApiOperation(value = "Create worked time for user by token")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-Auth-Token",
                    value = "token", required = true,
                    dataType = "string",
                    paramType = "header")
    })
    @PostMapping("/create")
    public ResponseEntity<UserWorkedTime> createUserWorkedTime(
            UserWorkedTimeRequest request,
            @ApiIgnore Principal principal) {

        boolean isAdmin = principalUtil.getAuthorities(principal)
                .stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals(ESystemRoles.ROLE_ADMIN.toString()));

        if (isAdmin) {
            String newUserWorkedTimeNumber = request.getHeader("Room_number");

            Optional<UserWorkedTime> searchRoomResult = roomRepository.findByUserWorkedTimeNumberIgnoreCase
                    (newUserWorkedTimeNumber);
            if (searchUserWorkedTimeResult.isPresent()) {
                throw new PresentEntityException("The entity " +
                        searchUserWorkedTimeResult.get().getClass().getSimpleName() +
                        " \"" + searchUserWorkedTimeResult.get().getUserWorkedTimeNumber() +
                        "\" " + " is all ready created");
            }
            UserWorkedTime newUserWorkedTime = new UserWorkedTime();
            newUserWorkedTime.setUserWorkedTimeNumber(newRoomNumber);
            return ResponseEntity.ok(roomRepository.save(newRoom));
        } else {
            throw new UnauthorizedException(MyMessages.BAD_PERMISSIONS);
        }
    }

    @ApiOperation(value = "Update room by ID. Role_Admin only")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-Auth-Token",
                    value = "token", required = true,
                    dataType = "string",
                    paramType = "header"),
            @ApiImplicitParam(name = "Room_number",
                    value = "New room number", required = true,
                    dataType = "string",
                    paramType = "header")
    })
    @PostMapping("/update/{id}")
    public ResponseEntity<UserWorkedTime> updateUserWorkedTime(
            HttpServletRequest request,
            @PathVariable Integer id,
            @ApiIgnore Principal principal) {

        boolean isAdmin = principalUtil.getAuthorities(principal)
                .stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals(ESystemRoles.ROLE_ADMIN.toString()));

        if (isAdmin) {
            Optional<UserWorkedTime> foundRoom = roomRepository.findById(id);

            if (foundUserWorkedTime.isEmpty()) {
                throw new NoSuchEntityException("No such room with id:" + id);
            }
            UserWorkedTime updatedRoom = foundRoom.get();
            updatedRoom.setRoomNumber(request.getHeader("Room_number"));

            return ResponseEntity.ok(roomRepository.save(updatedRoom));
        } else {
            throw new UnauthorizedException(MyMessages.BAD_PERMISSIONS);
        }
    }

    @ApiOperation(value = "Hard delete room by Id")
    @DeleteMapping("/delete_hard/{id}")
    @ApiImplicitParam(name = "X-Auth-Token",
            value = "token", required = true,
            dataType = "string",
            paramType = "header")
    public void deleteUserHard(@PathVariable Integer id,
                               @ApiIgnore Principal principal) {

        boolean isAdmin = principalUtil.getAuthorities(principal)
                .stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals(ESystemRoles.ROLE_ADMIN.toString()));

        if (!isAdmin) {
            throw new UnauthorizedException(MyMessages.BAD_PERMISSIONS);
        }
        roomRepository.deleteById(id);
    }
}
