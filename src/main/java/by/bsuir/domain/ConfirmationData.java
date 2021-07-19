package by.bsuir.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "confirmation_id_table")
@Data
@NoArgsConstructor
@EqualsAndHashCode(exclude = {"user"})
public class ConfirmationData {

    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id")
    //@JsonBackReference
    private User user;

    @Column
    private String uuid;

    @Column(name = "due_date")
    private Timestamp dueDate;
}
