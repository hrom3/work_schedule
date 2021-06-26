package by.bsuir.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;


@Entity
@Table(name = "rate")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Rate {

    @Id
    private Integer id;

    @Column(name = "salary_rate")
    private Float salaryRate;

    @Column(name = "work_hour")
    private Integer workHour;

    @Column(name = "work_hour_short_day")
    private Integer workHourShortDay;


    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }
}
