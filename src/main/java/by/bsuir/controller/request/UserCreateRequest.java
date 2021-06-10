package by.bsuir.controller.request;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Date;

@Data
@AllArgsConstructor
public class UserCreateRequest {
    private String name;
    private String surname;
    private String middleName;
    private String email;
    private Date birthDay;
    private Integer departmentId;
    private Integer rateId;
    private Integer roomId;
}
