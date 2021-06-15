package by.bsuir.domain;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "users_role")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsersRole {

    @Id
    private Long id;

    @Column(name = "id_user")
    private Long idUser;

    @Column(name = "id_role")
    private Integer idRole;

//    https://coderoad.ru/15974474/%D0%A1%D0%BE%D0%BF%D0%BE%D1%81%D1%82%D0%B0%D0%B2%D0%BB%D0%B5%D0%BD%D0%B8%D0%B5-%D1%81%D1%82%D0%BE%D0%BB%D0%B1%D1%86%D0%B0-PostgreSQL-JSON-%D1%81%D0%B2%D0%BE%D0%B9%D1%81%D1%82%D0%B2%D1%83-%D1%81%D1%83%D1%89%D0%BD%D0%BE%D1%81%D1%82%D0%B8-Hibernate
//    @Column
//    private JsonObject permission;

    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }

}
