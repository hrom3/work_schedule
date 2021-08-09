package by.bsuir.util;

import org.jetbrains.annotations.NotNull;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class ConverterDateTimeStamp {

    //  LocalDateTime to Timestamp
    public @NotNull Timestamp converterDateAndTimeToTimeStamp(LocalDate date, LocalTime time) {
        return Timestamp.valueOf(LocalDateTime.of(date, time));
    }

    public @NotNull Timestamp converterDateToTimeStamp(LocalDate date) {
        return Timestamp.valueOf(LocalDateTime.of(date, LocalTime.of(0, 0)));
    }

    //  Timestamp to LocalDateTime
    public LocalDateTime converterTimeStampToLocalDataTime(@NotNull Timestamp timestamp) {
        return timestamp.toLocalDateTime();
    }

    //  Timestamp to LocalDate
    public LocalDate converterTimeStampToLocalData(@NotNull Timestamp timestamp) {
        return timestamp.toLocalDateTime().toLocalDate();
    }

    //  Timestamp to LocalTime
    public LocalTime convertetrimeStampToLocalTime(@NotNull Timestamp timestamp) {
        return timestamp.toLocalDateTime().toLocalTime();
    }
}
