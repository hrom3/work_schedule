package by.bsuir.controller.request;

import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class RoleCreateRequest {

    @ApiParam(value = "1 - ADMIN; 2 - USER; 3 - MODERATOR", required = true)
    private List<Integer> roleIds;

}
