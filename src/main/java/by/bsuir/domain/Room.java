package by.bsuir.domain;

import by.bsuir.domain.viewhelper.View;
import com.fasterxml.jackson.annotation.JsonBackReference;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Collections;
import java.util.Set;

@Entity
@Table(name = "rooms")
@Data
@NoArgsConstructor
@EqualsAndHashCode(exclude = {
        "users"
})
public class Room {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator =  "room_id"
    )
    @SequenceGenerator(
            name = "room_id",
            sequenceName = "rooms_id_seq",
            initialValue = 7,
            allocationSize = 1
    )
    private Integer id;

    @Column(name = "room_number")
    @JsonView(View.PublicView.class)
    private String roomNumber;

    @OneToMany(mappedBy = "room", fetch = FetchType.EAGER)
    @JsonBackReference
    Set<User> users = Collections.emptySet();
}
