package by.bsuir.controller.request;

import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class DepartmentCreateRequest {

    @ApiParam(required = true)
    private String departmentName;

}
