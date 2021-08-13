package by.bsuir.controller.request;

import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserUpdateRequest {

    @ApiParam(required = true)
    private String name;

    @ApiParam(required = true)
    private String surname;

    private String middleName;

    @ApiParam(value = "example@gmail.com", required = true)
    private String email;

    @ApiParam(value = "format yyyy-MM-dd (2019-04-03)")
    private String birthDay;

    @ApiParam(value = "1 - QA engineer; 2 - Software engineer, 3 - Algorithmist",
            defaultValue = "1", allowableValues = "range[1, 3]")
    private Integer departmentId;

    @ApiParam(value = "1 - 0.25; 2 - 0.5; 3 - 0.75; 4 - 1; 5 - 1.25; 6 - 1.5",
            defaultValue = "1", allowableValues = "range[1, 6]")
    private Integer rateId;

    @ApiParam(value = "1 - 207a; 2 - 209; 3 - 210a; 4 - 213a; 5 - 203a; " +
            "6 - 328; 7 - 205", defaultValue = "1",
            allowableValues = "range[1, 7]")
    private Integer roomId;

    @ApiParam(value = "1 - ADMIN; 2 - USER; 3 - MODERATOR",
            defaultValue = "2", allowableValues = "range[1, 3]")
    private Integer roleId;
    //private ArrayList<Integer> roleIds;

    @ApiParam(value = "must be unique", required = true, allowEmptyValue = false)
    private String login;

    @ApiParam(value = "not null", required = true, allowEmptyValue = false)
    private String password;
}
