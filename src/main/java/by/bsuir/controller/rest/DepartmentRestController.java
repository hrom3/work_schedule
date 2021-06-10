package by.bsuir.controller.rest;


import by.bsuir.domain.Department;
import by.bsuir.repository.IDepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/rest/department")
@RequiredArgsConstructor
public class DepartmentRestController {

    private final IDepartmentRepository departmentRepository;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Department> findAll() {
    return departmentRepository.findAll();
    }
}
