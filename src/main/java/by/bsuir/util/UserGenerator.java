package by.bsuir.util;

import by.bsuir.domain.User;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
@AllArgsConstructor
public class UserGenerator {

    public List<User> generate(int count) {
        List<User> result = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            result.add(generate());
        }
        return result;
    }

    public User generate() {
        User user = new User();
        user.setName(RandomFromFile.randomNextName());
        user.setSurname(RandomFromFile.randomNextSurname());
        user.setMiddleName(RandomStringUtils.randomAlphabetic(5, 15));
        user.setEmail(RandomStringUtils.randomAlphabetic(5, 15)
                .toLowerCase(Locale.ROOT) + "@bsuir.by");
//        user.setBirthDay(new Date(RandomUtils.nextLong(0L, 946677600000L)));
        user.setBirthDay(LocalDate.ofInstant(Instant.ofEpochMilli
                        (RandomUtils.nextLong(0L, 946677600000L)),
                ZoneId.systemDefault()));
        user.setDepartmentId(RandomUtils.nextInt(1, 3));
        user.setCreated(new Timestamp(System.currentTimeMillis()));
        user.setChanged(new Timestamp(System.currentTimeMillis()));
        user.setRateId(RandomUtils.nextInt(1, 6));
        user.setRoomId(RandomUtils.nextInt(1, 7));
        return user;
    }

    public User generateLiteUser() {
        User user = new User();

        user.setCreated(new Timestamp(System.currentTimeMillis()));
        user.setChanged(new Timestamp(System.currentTimeMillis()));

        return user;
    }

}
