package by.bsuir.controller.request;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class UserCreateRequest {
    private String name;
    private String surname;
    private String middleName;
    private String email;
    private String birthDay;
    private Integer departmentId;
    private Integer rateId;
    private Integer roomId;

    private String login;
    private String password;
}
