package by.bsuir.controller.request;

import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class UserCreateRequest {

    private String name;

    private String surname;
    @ApiParam()
    private String middleName;

    @ApiParam(value = "not null", required = true, allowEmptyValue = false)
    private String email;

    @ApiParam(value = "format yyyy-MM-dd (2019-04-03)")
    private String birthDay;

    @ApiParam(value = "from 1 to 3", allowableValues = "range[1, 3]")
    private Integer departmentId;

    @ApiParam(value = "from 1 to 6", allowableValues = "range[1, 6]")
    private Integer rateId;

    @ApiParam(value = "from 1 to 7",allowableValues = "range[1, 7]")
    private Integer roomId;

    @ApiParam(value = "not null", required = true, allowEmptyValue = false)
    private String login;

    @ApiParam(value = "not null", required = true, allowEmptyValue = false)
    private String password;
}
