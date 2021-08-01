package by.bsuir.controller.rest;


import by.bsuir.controller.exception.PresentEntityException;
import by.bsuir.controller.exception.UnauthorizedException;
import by.bsuir.controller.request.DepartmentCreateRequest;
import by.bsuir.domain.Department;
import by.bsuir.domain.ESystemRoles;
import by.bsuir.domain.Role;
import by.bsuir.domain.User;
import by.bsuir.repository.springdata.IDepartmentDataRepository;
import by.bsuir.repository.springdata.IUserDataRepository;
import by.bsuir.security.utils.PrincipalUtils;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/rest/department")
@RequiredArgsConstructor
public class DepartmentRestController {

    private final IDepartmentDataRepository departmentRepository;

    private final PrincipalUtils principalUtils;

    private final IUserDataRepository userRepository;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Department> findAll() {
        return departmentRepository.findAll();
    }

    @ApiOperation(value = "Create department and authenticate")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-Auth-Token",
                    value = "token", required = true,
                    dataType = "string",
                    paramType = "header")
    })
    @GetMapping("/create")
    public Department createDepartment(DepartmentCreateRequest createDepartmentRequest,
                                       @ApiIgnore Principal principal) {

        String login = principalUtils.getUsername(principal);
        User user = userRepository.findUserByCredentialLogin(login).get();

        boolean isAdmin = user
                .getRoles()
                .stream()
                .map(Role::getRoleName)
                .anyMatch(str -> str.equals(ESystemRoles.ROLE_ADMIN));

        if (isAdmin) {

            Optional<Department> departmentAllReadyPresent = departmentRepository
                    .findDepartmentByDepartmentName(createDepartmentRequest.getDepartmentName());
            if (departmentAllReadyPresent.isPresent()) {
                throw new PresentEntityException("The entity " +
                        departmentAllReadyPresent.get().getClass().getSimpleName() +
                        " \"" + departmentAllReadyPresent.get().getDepartmentName() +
                        "\" " +" is all ready created");
            }
            Department newDepartment = new Department();

            newDepartment.setDepartmentName(createDepartmentRequest.getDepartmentName());

            return departmentRepository.save(newDepartment);
        } else {
            throw new UnauthorizedException("Unable to authenticate Domain " +
                    "User for provided credentials.");
        }
    }
}
