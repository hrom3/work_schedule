package by.bsuir.domain;


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
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator =  "confirmation_id_table_id"
    )
    @SequenceGenerator(
            name = "confirmation_id_table_id",
            sequenceName = "confirmation_id_table_id_seq"
    )
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column
    private String uuid;

    @Column(name = "due_date")
    private Timestamp dueDate;
}
