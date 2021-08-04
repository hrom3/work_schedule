package by.bsuir.controller.rest;

import by.bsuir.controller.exception.NoSuchEntityException;
import by.bsuir.controller.exception.PresentEntityException;
import by.bsuir.controller.exception.UnauthorizedException;
import by.bsuir.controller.request.DepartmentCreateRequest;
import by.bsuir.domain.Department;
import by.bsuir.domain.ESystemRoles;
import by.bsuir.domain.viewhelper.View;
import by.bsuir.repository.springdata.IDepartmentDataRepository;
import by.bsuir.security.utils.PrincipalUtil;
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
@RequestMapping("/rest/department")
@RequiredArgsConstructor
public class DepartmentRestController {

    private final IDepartmentDataRepository departmentRepository;

    private final PrincipalUtil principalUtil;

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

    @GetMapping("/search/{id}")
    @ApiOperation(value = "Find department by id")
    @JsonView(View.PublicView.class)
    public ResponseEntity<Department> findOneById(@PathVariable Integer id) {

        Optional<Department> foundDepartment = departmentRepository.findById(id);

        if (foundDepartment.isPresent()) {
            return ResponseEntity.ok(foundDepartment.get());
        } else {
            throw new NoSuchEntityException("No such department with id:" + id);
        }
    }

    @ApiOperation(value = "Create department. Role_Admin only")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-Auth-Token",
                    value = "token", required = true,
                    dataType = "string",
                    paramType = "header")
    })
    @PostMapping("/create")
    public ResponseEntity<Department> createDepartment(
            DepartmentCreateRequest createDepartmentRequest,
            @ApiIgnore Principal principal) {

        boolean isAdmin = principalUtil.getAuthorities(principal)
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

            return ResponseEntity.ok(departmentRepository.save(newDepartment));
        } else {
            throw new UnauthorizedException
                    ("Insufficient permissions to perform the operation");
        }
    }

    @ApiOperation(value = "Update department. Role_Admin only")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-Auth-Token",
                    value = "token", required = true,
                    dataType = "string",
                    paramType = "header")
    })
    @PostMapping("/update/{id}")
    public ResponseEntity<Department> updateDepartment(
            DepartmentCreateRequest createDepartmentRequest,
            @PathVariable Integer id,
            @ApiIgnore Principal principal) {

        boolean isAdmin = principalUtil.getAuthorities(principal)
                .stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals(ESystemRoles.ROLE_ADMIN.toString()));

        if (isAdmin) {

            Optional<Department> foundDepartment = departmentRepository.findById(id);

            if (foundDepartment.isEmpty()) {
                throw new NoSuchEntityException("No such department with id:" + id);
            }

            Department updatedDepartment = foundDepartment.get();

            updatedDepartment.setDepartmentName(createDepartmentRequest.getDepartmentName());

            return ResponseEntity.ok(departmentRepository.save(updatedDepartment));
        } else {
            throw new UnauthorizedException
                    ("Insufficient permissions to perform the operation");
        }
    }

    @ApiOperation(value = "Hard delete departmentby Id")
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
            throw new UnauthorizedException
                    ("Insufficient permissions to perform the operation");
        }
        departmentRepository.deleteById(id);
    }


}
