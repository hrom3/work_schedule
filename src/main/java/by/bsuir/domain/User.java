package by.bsuir.domain;

import lombok.*;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.sql.Date;
import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

    private Long id;
    private String name;
    private String surname;
    private String middleName;
    private String email;
    private Date birthDay;
    private Integer departmentId;
    private Timestamp created;
    private Timestamp changed;
    private boolean isDeleted;
    private Integer rateId;
    private Integer roomId;

    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }
}
