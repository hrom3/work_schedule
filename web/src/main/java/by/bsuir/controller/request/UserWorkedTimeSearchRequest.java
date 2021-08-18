package by.bsuir.controller.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalTime;

@Data
@AllArgsConstructor
public class UserWorkedTimeSearchRequest {

    @ApiParam(value = "format yyyy-MM-dd (2019-04-03)",
            required = true)
    private String date;

    @ApiParam(value = "format HH:mm (08:00)",
            required = true)
    @JsonFormat(pattern = "HH:mm")
    private LocalTime startTime;


    @ApiParam(value = "format HH:mm (17:00)",
            required = true)
    @JsonFormat(pattern = "HH:mm")
    private LocalTime endTime;

}
