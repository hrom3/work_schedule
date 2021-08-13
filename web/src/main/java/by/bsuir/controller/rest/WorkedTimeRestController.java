package by.bsuir.controller.rest;

import by.bsuir.controller.request.UserWorkedTimeRequest;
import by.bsuir.controller.request.UserWorkedTimeResponse;
import by.bsuir.domain.ESystemRoles;
import by.bsuir.domain.User;
import by.bsuir.domain.UserWorkedTime;
import by.bsuir.domain.viewhelper.View;
import by.bsuir.exception.DateAndTimeException;
import by.bsuir.exception.NoSuchEntityException;
import by.bsuir.exception.UnauthorizedException;
import by.bsuir.repository.springdata.IUserDataRepository;
import by.bsuir.repository.springdata.IUserWorkedTimeDataRepository;
import by.bsuir.security.utils.PrincipalUtil;
import by.bsuir.util.ConverterDateTimeStamp;
import by.bsuir.util.MyMessages;
import by.bsuir.util.WorkTimeHelper;

import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.security.Principal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;


@RestController
@RequestMapping("/rest/work_time")
@RequiredArgsConstructor
public class WorkedTimeRestController {

    private final IUserWorkedTimeDataRepository userWorkedTimeDataRepository;

    private final IUserDataRepository userDataRepository;

    private final PrincipalUtil principalUtil;

    private final ConverterDateTimeStamp converterDateTimeStamp;

    private final WorkTimeHelper workTimeHelper;


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

        LocalDate dateStart = LocalDate.parse(request.getDate());
        LocalDate dateFinish = LocalDate.parse(request.getDate());
        if (!workTimeHelper.isItTheSameDay(dateStart, dateFinish)) {
            throw new DateAndTimeException("Start and Finish Date must be the same");
        }
        if (request.getStartTime().isAfter(request.getEndTime())) {
            throw new DateAndTimeException("Start time must be before end time");
        }
        Timestamp startTime = converterDateTimeStamp.converterDateAndTimeToTimeStamp(
                dateStart, request.getStartTime());
        Timestamp endTime = converterDateTimeStamp.converterDateAndTimeToTimeStamp(
                dateFinish, request.getEndTime());
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
    @JsonView(View.ExtendedPublicView.class)
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

        LocalDate dateNew = LocalDate.parse(request.getDate());

        if (request.getStartTime().isAfter(request.getEndTime())) {
            throw new DateAndTimeException("Start time must be before end time");
        }

        Timestamp startTime = converterDateTimeStamp.converterDateAndTimeToTimeStamp(
                dateNew, request.getStartTime());
        Timestamp endTime = converterDateTimeStamp.converterDateAndTimeToTimeStamp(
                dateNew, request.getEndTime());

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
    @JsonView(View.InternalView.class)
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
            throw new NoSuchEntityException("Bad user for work update");
        }

        UserWorkedTime workToUpdate = searchResultWorkToChange.get();
        LocalDate dateToUpdate = LocalDate.parse(request.getDate());

        String workDescription = request.getWork();

        if (request.getStartTime().isAfter(request.getEndTime())) {
            throw new DateAndTimeException("Start time must be before end time");
        }

        Timestamp startTime = converterDateTimeStamp.converterDateAndTimeToTimeStamp(
                dateToUpdate, request.getStartTime());
        Timestamp endTime = converterDateTimeStamp.converterDateAndTimeToTimeStamp(
                dateToUpdate, request.getEndTime());

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
    public ResponseEntity<String> deleteUserHard(@PathVariable Long id,
                                                 @ApiIgnore Principal principal) {

        boolean isAdmin = principalUtil.getAuthorities(principal)
                .stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals(ESystemRoles.ROLE_ADMIN.toString()));

        if (!isAdmin) {
            throw new UnauthorizedException(MyMessages.BAD_PERMISSIONS);
        }
        userWorkedTimeDataRepository.deleteById(id);

        return ResponseEntity.ok(
                String.format("The work description id %d has deleted", id));
    }


    @GetMapping("/search_work_in_day/{date_to_search}")
    @ApiOperation(value = "Find all worked time for user by token and in a day")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "date_to_search", dataType = "string",
                    paramType = "path", value = "Date to search format 2021-12-23",
                    required = true, defaultValue = "2021-08-01"),
            @ApiImplicitParam(name = "X-Auth-Token",
                    value = "token", required = true,
                    dataType = "string",
                    paramType = "header")
    })
    @JsonView(View.PublicView.class)
    public ResponseEntity<List<UserWorkedTime>> searchWorkInDate(
            @PathVariable String date_to_search,
            @ApiIgnore Principal principal) {

        String login = principalUtil.getUsername(principal);
        Optional<User> searchResult =
                userDataRepository.findByCredentialLogin(login);
        if (searchResult.isEmpty()) {
            throw new NoSuchEntityException(MyMessages.NO_SUCH_USER + login);
        }
        User foundUser = searchResult.get();

        LocalDate startDate = LocalDate.parse(date_to_search);

        Timestamp startTimeStamp = converterDateTimeStamp.converterDateToTimeStamp(
                LocalDate.parse(date_to_search));

        Timestamp endTimeStamp = converterDateTimeStamp.converterDateToTimeStamp(
                startDate.plusDays(1l));

        List<UserWorkedTime> searchResultWorkToChange
                = userWorkedTimeDataRepository.findByUserIdAndStartTimeBetween(
                foundUser.getId(), startTimeStamp, endTimeStamp);
        return ResponseEntity.ok(searchResultWorkToChange);
    }


    @GetMapping("/search_work_in_day_with_wirtime/{date_to_search}")
    @ApiOperation(value = "Find all worked time and time of work for user by token and in a day")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "date_to_search", dataType = "string",
                    paramType = "path", value = "Date to search format 2021-12-23",
                    required = true, defaultValue = "2021-08-01"),
            @ApiImplicitParam(name = "X-Auth-Token",
                    value = "token", required = true,
                    dataType = "string",
                    paramType = "header")
    })
    @JsonView(View.PublicView.class)
    public ResponseEntity<UserWorkedTimeResponse> searchWorkInDateWithTime(
            @PathVariable String date_to_search,
            @ApiIgnore Principal principal) {

        String login = principalUtil.getUsername(principal);
        Optional<User> searchResult =
                userDataRepository.findByCredentialLogin(login);
        if (searchResult.isEmpty()) {
            throw new NoSuchEntityException(MyMessages.NO_SUCH_USER + login);
        }
        User foundUser = searchResult.get();

        LocalDate startDate = LocalDate.parse(date_to_search);

        Timestamp startTimeStamp = converterDateTimeStamp.converterDateToTimeStamp(
                LocalDate.parse(date_to_search));

        Timestamp endTimeStamp = converterDateTimeStamp.converterDateToTimeStamp(
                startDate.plusDays(1l));

        List<UserWorkedTime> searchResultWorkToChange
                = userWorkedTimeDataRepository.findByUserIdAndStartTimeBetween(
                foundUser.getId(), startTimeStamp, endTimeStamp);

        Long workedTimeInDay = 0L;

        for (UserWorkedTime workedTime : searchResultWorkToChange) {

            if (workedTime.getEndTime().before(workedTime.getStartTime())) {
                throw new DateAndTimeException("Start time must be before end time");
            }
            workedTimeInDay += workedTime.getEndTime().getTime()
                    - workedTime.getStartTime().getTime();

        }

        long hours = TimeUnit.MILLISECONDS.toHours(workedTimeInDay);
        workedTimeInDay -= TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(workedTimeInDay);
        workedTimeInDay -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(workedTimeInDay);

        StringBuilder sb = new StringBuilder();
        sb.append(hours);
        sb.append(" : ");
        sb.append(minutes);
        sb.append(" : ");
        sb.append(seconds);

        UserWorkedTimeResponse urt = new UserWorkedTimeResponse(
                searchResultWorkToChange, sb.toString());

        return ResponseEntity.ok(urt);
    }

}
