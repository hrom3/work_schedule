package by.bsuir.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.*;
import java.util.Collections;
import java.util.Set;

@Entity
@Table(name = "rooms")
@Data
//@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(exclude = {
        "users"
})
public class Room {

    @Id
    private Integer id;

    @Column(name = "room_number")
    private String roomNumber;

    @OneToMany(mappedBy = "room", fetch = FetchType.EAGER)
    @JsonBackReference
    Set<User> users = Collections.emptySet();
}
