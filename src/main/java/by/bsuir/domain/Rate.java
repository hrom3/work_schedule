package by.bsuir.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Collections;
import java.util.Set;


@Entity
@Table(name = "rate")
@Data
@NoArgsConstructor
@EqualsAndHashCode(exclude = {
        "users"
})
public class Rate {

    @Id
    private Integer id;

    @Column(name = "salary_rate")
    private Float salaryRate;

    @Column(name = "work_hour")
    private Integer workHour;

    @Column(name = "work_hour_short_day")
    private Integer workHourShortDay;

    @OneToMany(mappedBy = "rate", fetch = FetchType.EAGER)
    @JsonBackReference
    private Set<User> users = Collections.emptySet();
}
