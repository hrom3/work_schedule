package by.bsuir.controller.request;

import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
public class UserWorkedTimeRequest {

    @ApiParam(value = "format yyyy-MM-dd HH:mm:ss,SSS (2019-04-03 08:00:00,000)",
            required = true)
    private Timestamp startTime;
    //private String startTime;

    @ApiParam(value = "format yyyy-MM-dd HH:mm:ss,SSS (2019-04-03 16:00:00,000)",
            required = true)
    private Timestamp endTime;
    //private String endTime;

    @ApiParam(value = "Description of work", required = true)
    private String work;

    private Long userId;

    @ApiParam(value = "Format of issue QTC-10235")
    private String issueName;
}
