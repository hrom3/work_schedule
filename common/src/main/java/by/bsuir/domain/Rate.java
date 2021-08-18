package by.bsuir.domain;

import by.bsuir.service.viewhelper.View;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
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
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "rate_id"
    )
    @SequenceGenerator(
            name = "rate_id",
            sequenceName = "rate_id_seq",
            initialValue = 7,
            allocationSize = 1
    )
    private Integer id;

    @Column(name = "salary_rate")
    @JsonView(View.InternalView.class)
    private Float salaryRate;

    @Column(name = "work_hour")
    @JsonView(View.InternalView.class)
    private Integer workHour;

    @Column(name = "work_hour_short_day")

    private Integer workHourShortDay;

    @OneToMany(mappedBy = "rate", fetch = FetchType.EAGER)
    @JsonView(View.InternalView.class)
    @JsonBackReference
    private Set<User> users = Collections.emptySet();
}
