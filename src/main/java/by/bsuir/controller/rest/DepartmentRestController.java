package by.bsuir.controller.rest;


import by.bsuir.controller.exception.PresentEntityException;
import by.bsuir.controller.exception.UnauthorizedException;
import by.bsuir.controller.request.DepartmentCreateRequest;
import by.bsuir.domain.Department;
import by.bsuir.domain.ESystemRoles;
import by.bsuir.domain.Role;
import by.bsuir.domain.User;
import by.bsuir.domain.viewhelper.View;
import by.bsuir.repository.springdata.IDepartmentDataRepository;
import by.bsuir.repository.springdata.IUserDataRepository;
import by.bsuir.security.utils.PrincipalUtils;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.websocket.server.PathParam;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/rest/department")
@RequiredArgsConstructor
public class DepartmentRestController {

    private final IDepartmentDataRepository departmentRepository;

    private final PrincipalUtils principalUtils;

    private final IUserDataRepository userRepository;

    @GetMapping
    @ApiOperation(value = "Find all departments")
    @JsonView(View.PublicView.class)
    public ResponseEntity<List<Department>> findAll() {
        return ResponseEntity.ok(departmentRepository.findAll());
    }

    @GetMapping("/search/{query}")
    @ApiOperation(value = "Find departments by query")
    @JsonView(View.PublicView.class)
    public ResponseEntity<List<Department>> userSearch(@PathVariable String query) {
        return ResponseEntity.ok(departmentRepository.
                findByDepartmentNameContainingIgnoreCase(query));
    }


    @ApiOperation(value = "Create department and authenticate")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-Auth-Token",
                    value = "token", required = true,
                    dataType = "string",
                    paramType = "header")
    })
    @PostMapping("/create")
    public Department createDepartment(DepartmentCreateRequest createDepartmentRequest,
                                       @ApiIgnore Principal principal) {

//            String login = principalUtils.getUsername(principal);
//            User user = userRepository.findByCredentialLogin(login).get();
//
//            boolean isAdmin = user
//                    .getRoles()
//                    .stream()
//                    .map(Role::getRoleName)
//                    .anyMatch(role -> role.equals(ESystemRoles.ROLE_ADMIN));
        boolean isAdmin = principalUtils.getAuthorities(principal)
                .stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals(ESystemRoles.ROLE_ADMIN.toString()));

        if (isAdmin) {

            Optional<Department> departmentAllReadyPresent = departmentRepository
                    .findByDepartmentNameIgnoreCase(createDepartmentRequest.getDepartmentName());
            if (departmentAllReadyPresent.isPresent()) {
                throw new PresentEntityException("The entity " +
                        departmentAllReadyPresent.get().getClass().getSimpleName() +
                        " \"" + departmentAllReadyPresent.get().getDepartmentName() +
                        "\" " + " is all ready created");
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
