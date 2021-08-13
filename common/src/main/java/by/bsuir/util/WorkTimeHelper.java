package by.bsuir.util;

import org.jetbrains.annotations.NotNull;

import java.sql.Timestamp;
import java.time.LocalDate;

public class WorkTimeHelper {

    public boolean isItTheSameDay(LocalDate date1, LocalDate date2) {
        return date1.isEqual(date2);
    }


    public boolean isItTheSameDay(@NotNull Timestamp timestamp1,
                                  @NotNull Timestamp timestamp2) {
        return timestamp1.toLocalDateTime().getDayOfMonth() ==  timestamp2
                .toLocalDateTime().getDayOfMonth();
    }



}
