package by.bsuir.util;

import org.jetbrains.annotations.NotNull;

import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.concurrent.TimeUnit;

public class WorkTimeHelper {

    public boolean isItTheSameDay(LocalDate date1, LocalDate date2) {
        return date1.isEqual(date2);
    }

    public boolean isItTheSameDay(@NotNull Timestamp timestamp1,
                                  @NotNull Timestamp timestamp2) {
        return timestamp1.toLocalDateTime().getDayOfMonth() == timestamp2
                .toLocalDateTime().getDayOfMonth();
    }

    public String workedTimeFromMillisecToStr(long milliseconds) {
        String sign = "";
        if (milliseconds < 0 ) {
            sign = "-";
            milliseconds *= -1;
        }


        long hours = TimeUnit.MILLISECONDS.toHours(milliseconds);
        milliseconds -= TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds);
        milliseconds -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds);

        return sign + hours + " : " + minutes + " : " + seconds;
    }

    public long needToWorkInMonthInMilliseconds(LocalDate date,
                                                int hoursNeedToWorkInDay) {

        long milliseconds;

        LocalDate now = LocalDate.now();
        LocalDate start = date.withDayOfMonth(1);
        LocalDate finish;

        if (date.getMonthValue() == now.getMonthValue() &&
                date.getYear() == now.getYear()) {
            finish = now;
        } else {
            finish = start.withDayOfMonth(start.lengthOfMonth());
        }

        long workingDaysInMonth = 0;
        while (finish.getDayOfMonth() >= start.getDayOfMonth() &&
                finish.getMonthValue() == start.getMonthValue()) {
            if (start.getDayOfWeek() != DayOfWeek.SUNDAY &&
                    start.getDayOfWeek() != DayOfWeek.SATURDAY) {
                workingDaysInMonth++;
            }
            start = start.plusDays(1L);
        }

        milliseconds = TimeUnit.HOURS.toMillis(workingDaysInMonth * hoursNeedToWorkInDay);
        return milliseconds;
    }


}
