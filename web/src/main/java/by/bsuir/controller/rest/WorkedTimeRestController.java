package by.bsuir.controller.rest;

import by.bsuir.controller.request.UserWorkedTimeRequest;
import by.bsuir.controller.request.UserWorkedTimeResponse;
import by.bsuir.controller.request.UserWorkedTimeSearchRequest;
import by.bsuir.domain.ESystemRoles;
import by.bsuir.domain.User;
import by.bsuir.domain.UserWorkedTime;
import by.bsuir.service.viewhelper.View;
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

    private static final String START_MUST_BE_BEFORE_END =
            "Start time must be before end time";


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
             UserWorkedTimeSearchRequest request) {
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
            throw new DateAndTimeException(START_MUST_BE_BEFORE_END);
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
    @PostMapping()
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
            throw new DateAndTimeException(START_MUST_BE_BEFORE_END);
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
            throw new DateAndTimeException(START_MUST_BE_BEFORE_END);
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
                    paramType = "path", value = "Date to search format YYYY-MM-DD",
                    required = true, defaultValue = "2021-08-01"),
            @ApiImplicitParam(name = "X-Auth-Token",
                    value = "token", required = true,
                    dataType = "string",
                    paramType = "header")
    })
    @JsonView(View.PublicView.class)
    public ResponseEntity<List<UserWorkedTime>> searchWorkInDate(
            @PathVariable(name = "date_to_search") String dateToSearch,
            @ApiIgnore Principal principal) {
        // TODO: Converter
        String login = principalUtil.getUsername(principal);
        Optional<User> searchResult =
                userDataRepository.findByCredentialLogin(login);
        if (searchResult.isEmpty()) {
            throw new NoSuchEntityException(MyMessages.NO_SUCH_USER + login);
        }
        User foundUser = searchResult.get();

        LocalDate startDate = LocalDate.parse(dateToSearch);

        Timestamp startTimeStamp = converterDateTimeStamp.converterDateToTimeStamp(
                LocalDate.parse(dateToSearch));

        Timestamp endTimeStamp = converterDateTimeStamp.converterDateToTimeStamp(
                startDate.plusDays(1L));

        List<UserWorkedTime> searchResultWorkToChange
                = userWorkedTimeDataRepository.findByUserIdAndStartTimeBetween(
                foundUser.getId(), startTimeStamp, endTimeStamp);
        return ResponseEntity.ok(searchResultWorkToChange);
    }


    @GetMapping("/search_work_in_month_with_worked_time/{date_to_search}")
    @ApiOperation(value = "Find all worked time and time of work for user by token and in a month")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "date_to_search", dataType = "string",
                    paramType = "path", value = "Date to search format YYYY-MM-DD",
                    required = true, defaultValue = "2021-08-01"),
            @ApiImplicitParam(name = "X-Auth-Token",
                    value = "token", required = true,
                    dataType = "string",
                    paramType = "header")
    })
    @JsonView(View.PublicView.class)
    public ResponseEntity<UserWorkedTimeResponse> searchWorkInDateWithTime(
            @PathVariable(name = "date_to_search") String dateToSearch,
            @ApiIgnore Principal principal) {
        // TODO: Converter
        String login = principalUtil.getUsername(principal);
        Optional<User> searchResult =
                userDataRepository.findByCredentialLogin(login);
        if (searchResult.isEmpty()) {
            throw new NoSuchEntityException(MyMessages.NO_SUCH_USER + login);
        }
        User foundUser = searchResult.get();

        LocalDate initial = LocalDate.parse(dateToSearch);
        LocalDate startDate = initial.withDayOfMonth(1);
        LocalDate endDate = initial.withDayOfMonth(initial.lengthOfMonth());


        Timestamp startTimeStamp = converterDateTimeStamp.converterDateToTimeStamp(
                startDate);

        Timestamp endTimeStamp = converterDateTimeStamp.converterDateToTimeStamp(
                endDate);

        List<UserWorkedTime> searchResultWork
                = userWorkedTimeDataRepository.findByUserIdAndStartTimeBetween(
                foundUser.getId(), startTimeStamp, endTimeStamp);

        long workedTimeInMonth = 0L;

        for (UserWorkedTime workedTime : searchResultWork) {

            if (workedTime.getEndTime().before(workedTime.getStartTime())) {
                throw new DateAndTimeException(START_MUST_BE_BEFORE_END);
            }
            workedTimeInMonth += workedTime.getEndTime().getTime()
                    - workedTime.getStartTime().getTime();
        }

        long needToWorkInMonth = workTimeHelper.needToWorkInMonthInMilliseconds
                (initial, foundUser.getRate().getWorkHour());

        long  overtimeMillis = workedTimeInMonth - needToWorkInMonth;

        String workedTime = workTimeHelper.workedTimeFromMillisecToStr(workedTimeInMonth);
        String needToWork = workTimeHelper.workedTimeFromMillisecToStr(needToWorkInMonth);
        String overtime = workTimeHelper.workedTimeFromMillisecToStr(overtimeMillis);

        UserWorkedTimeResponse urt = new UserWorkedTimeResponse(
                searchResultWork,  workedTime, needToWork, overtime);

        return ResponseEntity.ok(urt);
    }


    @GetMapping("/search_work_in_day_with_worked_time/{date_to_search}")
    @ApiOperation(value = "Find all worked time and time of work for user by token and in a day")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "date_to_search", dataType = "string",
                    paramType = "path", value = "Date to search format YYYY-MM-DD",
                    required = true, defaultValue = "2021-08-01"),
            @ApiImplicitParam(name = "X-Auth-Token",
                    value = "token", required = true,
                    dataType = "string",
                    paramType = "header")
    })
    @JsonView(View.PublicView.class)
    public ResponseEntity<UserWorkedTimeResponse> searchWorkedInMonthWithTime(
            @PathVariable(name = "date_to_search") String dateToSearch,
            @ApiIgnore Principal principal) {

        // TODO: Converter
        String login = principalUtil.getUsername(principal);
        Optional<User> searchResult =
                userDataRepository.findByCredentialLogin(login);
        if (searchResult.isEmpty()) {
            throw new NoSuchEntityException(MyMessages.NO_SUCH_USER + login);
        }
        User foundUser = searchResult.get();

        LocalDate startDate = LocalDate.parse(dateToSearch);

        Timestamp startTimeStamp = converterDateTimeStamp.converterDateToTimeStamp(
                LocalDate.parse(dateToSearch));

        Timestamp endTimeStamp = converterDateTimeStamp.converterDateToTimeStamp(
                startDate.plusDays(1L));

        List<UserWorkedTime> searchResultWork
                = userWorkedTimeDataRepository.findByUserIdAndStartTimeBetween(
                foundUser.getId(), startTimeStamp, endTimeStamp);

        long workedTimeInDay = 0L;

        for (UserWorkedTime workedTime : searchResultWork) {

            if (workedTime.getEndTime().before(workedTime.getStartTime())) {
                throw new DateAndTimeException(START_MUST_BE_BEFORE_END);
            }
            workedTimeInDay += workedTime.getEndTime().getTime()
                    - workedTime.getStartTime().getTime();
        }
        long needToWorkInDay = TimeUnit.HOURS.toMillis(foundUser.getRate().getWorkHour());

        long  overtimeMillis = workedTimeInDay - needToWorkInDay;

        String workedTime = workTimeHelper.workedTimeFromMillisecToStr(workedTimeInDay);
        String needToWork = workTimeHelper.workedTimeFromMillisecToStr(needToWorkInDay);
        String overtime = workTimeHelper.workedTimeFromMillisecToStr(overtimeMillis);

        UserWorkedTimeResponse urt = new UserWorkedTimeResponse(
                searchResultWork,  workedTime, needToWork, overtime);

        return ResponseEntity.ok(urt);
    }


}
