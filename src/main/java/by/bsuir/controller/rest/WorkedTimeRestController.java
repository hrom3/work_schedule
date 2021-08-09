package by.bsuir.controller.rest;

import by.bsuir.controller.exception.NoSuchEntityException;
import by.bsuir.controller.exception.PresentEntityException;
import by.bsuir.controller.exception.UnauthorizedException;
import by.bsuir.controller.request.UserWorkedTimeRequest;
import by.bsuir.domain.ESystemRoles;

import by.bsuir.domain.User;
import by.bsuir.domain.UserWorkedTime;
import by.bsuir.domain.viewhelper.View;
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
        List<UserWorkedTime> result = userWorkedTimeDataRepository.findAll();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/search/{user_id}")
    @ApiOperation(value = "Find all worked time for user")
    @JsonView(View.PublicView.class)
    public ResponseEntity<List<UserWorkedTime>> findAllForUser
            (@PathVariable("user_id") Long userId) {
        return ResponseEntity.ok(userWorkedTimeDataRepository.findByUserId(userId));
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
                (userWorkedTimeDataRepository.findByUserId(foundUserId));
    }

    @GetMapping("/user_start_end_time")
    @ApiOperation(value = "Find all worked time for user by token and between time1 and time 2")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-Auth-Token",
                    value = "token", required = true,
                    dataType = "string",
                    paramType = "header")
    })
    @JsonView(View.InternalView.class)
    public ResponseEntity<List<UserWorkedTime>> findWorkedTimeForUserBetweenTime
            (@ApiIgnore Principal principal,
             UserWorkedTimeRequest request) {
        String login = principalUtil.getUsername(principal);
        Optional<User> searchResult =
                userDataRepository.findByCredentialLogin(login);
        if (searchResult.isEmpty()) {
            throw new NoSuchEntityException(MyMessages.NO_SUCH_USER + login);
        }
        Long foundUserId = searchResult.get().getId();

        Timestamp startTime = request.getStartTime();
        Timestamp endTime = request.getEndTime();
        return ResponseEntity.ok
                (userWorkedTimeDataRepository.findByUserIdAndStartTimeBetween
                        (foundUserId, startTime, endTime));
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

        String login = principalUtil.getUsername(principal);
        Optional<User> searchResult =
                userDataRepository.findByCredentialLogin(login);
        if (searchResult.isEmpty()) {
            throw new NoSuchEntityException(MyMessages.NO_SUCH_USER + login);
        }
        User foundUser = searchResult.get();

        UserWorkedTime newWork = new UserWorkedTime();

        String workDescription = request.getWork();
        Timestamp startTime = request.getStartTime();
        Timestamp endTime = request.getEndTime();

        newWork.setUser(foundUser);
        newWork.setWork(workDescription);
        newWork.setStartTime(startTime);
        newWork.setEndTime(endTime);

        return ResponseEntity.ok(userWorkedTimeDataRepository.save(newWork));

    }

    @ApiOperation(value = "Update worked time for user by token and Id")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-Auth-Token",
                    value = "token", required = true,
                    dataType = "string",
                    paramType = "header")
    })
    @PostMapping("/update/{id}")
    public ResponseEntity<UserWorkedTime> updateUserWorkedTime(
            UserWorkedTimeRequest request,
            @PathVariable Long id,
            @ApiIgnore Principal principal) {

        String login = principalUtil.getUsername(principal);
        Optional<User> searchResult =
                userDataRepository.findByCredentialLogin(login);
        if (searchResult.isEmpty()) {
            throw new NoSuchEntityException(MyMessages.NO_SUCH_USER + login);
        }
        User foundUser = searchResult.get();

        Optional<UserWorkedTime> searchResultWorkToChange
                = userWorkedTimeDataRepository.findById(id);
        if (searchResultWorkToChange.isEmpty()) {
            throw new NoSuchEntityException(MyMessages.NO_SUCH_WORK + id);
        }

        if (!searchResultWorkToChange.get().getUser().equals(foundUser)) {
            throw new NoSuchEntityException("Bad user for work");
        }

        UserWorkedTime workToUpdate = new UserWorkedTime();

        String workDescription = request.getWork();
        Timestamp startTime = request.getStartTime();
        Timestamp endTime = request.getEndTime();

        workToUpdate.setUser(foundUser);
        workToUpdate.setWork(workDescription);
        workToUpdate.setStartTime(startTime);
        workToUpdate.setEndTime(endTime);

        return ResponseEntity.ok(userWorkedTimeDataRepository.save(workToUpdate));
    }

    @ApiOperation(value = "Hard delete work by Id")
    @DeleteMapping("/delete_hard/{id}")
    @ApiImplicitParam(name = "X-Auth-Token",
            value = "token", required = true,
            dataType = "string",
            paramType = "header")
    public void deleteUserHard(@PathVariable Long id,
                               @ApiIgnore Principal principal) {

        boolean isAdmin = principalUtil.getAuthorities(principal)
                .stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals(ESystemRoles.ROLE_ADMIN.toString()));

        if (!isAdmin) {
            throw new UnauthorizedException(MyMessages.BAD_PERMISSIONS);
        }
        userWorkedTimeDataRepository.deleteById(id);
    }
}
