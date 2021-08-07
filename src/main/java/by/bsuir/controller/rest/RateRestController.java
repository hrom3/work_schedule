package by.bsuir.controller.rest;

import by.bsuir.controller.exception.NoSuchEntityException;
import by.bsuir.controller.exception.UnauthorizedException;
import by.bsuir.controller.request.RateCreateRequest;
import by.bsuir.domain.ESystemRoles;
import by.bsuir.domain.Rate;
import by.bsuir.domain.viewhelper.View;
import by.bsuir.repository.springdata.IRateDataRepository;
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

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/rest/rate")
@RequiredArgsConstructor
public class RateRestController {

    private final IRateDataRepository rateRepository;

    private final PrincipalUtil principalUtil;

    @GetMapping
    @ApiOperation(value = "Find all rate")
    @JsonView(View.PublicView.class)
    public ResponseEntity<List<Rate>> findAll() {
        return ResponseEntity.ok(rateRepository.findAll());
    }

    @GetMapping("/search/{id}")
    @ApiOperation(value = "Find rate by id")
    @JsonView(View.PublicView.class)
    public ResponseEntity<Rate> findById(@PathVariable Integer id) {

        Optional<Rate> foundRate = rateRepository.findById(id);

        if (foundRate.isPresent()) {
            return ResponseEntity.ok(foundRate.get());
        } else {
            throw new NoSuchEntityException("No such rate with id:" + id);
        }
    }

    @ApiOperation(value = "Create rate. Role_Admin only")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-Auth-Token",
                    value = "token", required = true,
                    dataType = "string",
                    paramType = "header")
    })
    @PostMapping("/create")
    public ResponseEntity<Rate> createRate(
            RateCreateRequest createRateRequest,
            @ApiIgnore Principal principal) {

        boolean isAdmin = principalUtil.getAuthorities(principal)
                .stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals(ESystemRoles.ROLE_ADMIN.toString()));

        if (isAdmin) {
            Rate newRate = new Rate();

            newRate.setSalaryRate(createRateRequest.getSalaryRate());
            newRate.setWorkHour(createRateRequest.getWorkHour());
            newRate.setWorkHourShortDay(createRateRequest.getWorkHourShortDay());
            return ResponseEntity.ok(rateRepository.save(newRate));
        } else {
            throw new UnauthorizedException(MyMessages.BAD_PERMISSIONS);
        }
    }

    @ApiOperation(value = "Update rate by ID. Role_Admin only")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-Auth-Token",
                    value = "token", required = true,
                    dataType = "string",
                    paramType = "header")
    })
    @PostMapping("/update/{id}")
    public ResponseEntity<Rate> updateRate(
            RateCreateRequest createRateRequest,
            @PathVariable Integer id,
            @ApiIgnore Principal principal) {

        boolean isAdmin = principalUtil.getAuthorities(principal)
                .stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals(ESystemRoles.ROLE_ADMIN.toString()));

        if (isAdmin) {

            Optional<Rate> foundRate = rateRepository.findById(id);

            if (foundRate.isEmpty()) {
                throw new NoSuchEntityException("No such rate with id:" + id);
            }

            Rate updatedRate = foundRate.get();

            updatedRate.setSalaryRate(createRateRequest.getSalaryRate());
            updatedRate.setWorkHour(createRateRequest.getWorkHour());
            updatedRate.setWorkHourShortDay(createRateRequest.getWorkHourShortDay());
            return ResponseEntity.ok(rateRepository.save(updatedRate));
        } else {
            throw new UnauthorizedException(MyMessages.BAD_PERMISSIONS);
        }
    }

    @ApiOperation(value = "Hard delete rate by Id")
    @DeleteMapping("/delete_hard/{id}")
    @ApiImplicitParam(name = "X-Auth-Token",
            value = "token", required = true,
            dataType = "string",
            paramType = "header")
    public void deleteRateHard(@PathVariable Integer id,
                               @ApiIgnore Principal principal) {

        boolean isAdmin = principalUtil.getAuthorities(principal)
                .stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals(ESystemRoles.ROLE_ADMIN.toString()));

        if (!isAdmin) {
            throw new UnauthorizedException(MyMessages.BAD_PERMISSIONS);
        }
        rateRepository.deleteById(id);
    }
}
