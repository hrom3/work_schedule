package by.bsuir.controller.request;

import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EmailConfirmRequest {

    @ApiParam(value = "Long, not null", required = true)
    private Long id;
    @ApiParam(value = "String, not null", required = true)
    private String uuid;

}
