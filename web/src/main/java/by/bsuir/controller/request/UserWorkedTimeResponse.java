package by.bsuir.controller.request;

import by.bsuir.domain.UserWorkedTime;
import by.bsuir.domain.viewhelper.View;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class UserWorkedTimeResponse {

    @JsonView(View.PublicView.class)
    List<UserWorkedTime> userWorkTimes;

    @JsonView(View.PublicView.class)
    String time;
}
