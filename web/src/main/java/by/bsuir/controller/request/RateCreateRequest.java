package by.bsuir.controller.request;

import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RateCreateRequest {

    @ApiParam(required = true)
    private Float salaryRate;

    @ApiParam(required = true)
    private Integer workHour;

    private Integer workHourShortDay;

}
