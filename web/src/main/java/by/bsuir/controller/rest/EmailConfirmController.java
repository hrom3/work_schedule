package by.bsuir.controller.rest;

import by.bsuir.controller.request.EmailConfirmRequest;
import by.bsuir.domain.ConfirmationData;
import by.bsuir.domain.User;
import by.bsuir.exception.UnconfirmedUserException;
import by.bsuir.repository.springdata.IConfirmationDataRepository;
import by.bsuir.repository.springdata.IUserDataRepository;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@Api(value = "Email confirmation controller")
@RestController
@RequestMapping("/rest/confirm")
@RequiredArgsConstructor
public class EmailConfirmController {

    private static final Logger log = LoggerFactory.getLogger(EmailConfirmController.class);

    private final IUserDataRepository userRepository;

    private final IConfirmationDataRepository confirmationDataRepository;

    @GetMapping
    @ApiOperation(value = "Confirm user in system")
    public ResponseEntity<String> confirmUser(@ModelAttribute EmailConfirmRequest
                                                      confirmRequest) {
        Long id = confirmRequest.getId();
        String uuid = confirmRequest.getUuid();
        if (id == null || uuid == null) {
            throw new UnconfirmedUserException("Confirmation request invalid");
        }

        Optional<ConfirmationData> resultSet =
                confirmationDataRepository.findById(id);

        if (resultSet.isEmpty()) {
            throw new UnconfirmedUserException("Confirmation request invalid" + id);
        }

        ConfirmationData confirmationData = resultSet.get();
        long currentTimeMillis = System.currentTimeMillis();
        if (confirmationData.getDueDate().getTime() - currentTimeMillis < 0) {
            throw new UnconfirmedUserException("Confirmation time expired id ="
                    + id + " currentTime " + currentTimeMillis +
                    " confirmation due date " +
                    confirmationData.getDueDate().getTime());
        }

        if (confirmationData.getUuid().equals(uuid)) {
            User userToConfirm = confirmationData.getUser();

            boolean isUserConfirmed = userToConfirm.getIsConfirmed();
            if (isUserConfirmed) {
                return ResponseEntity.ok(String.format
                        ("User %s was already confirmed",
                                userToConfirm.getCredential().getLogin()));

            } else {
                userToConfirm.setIsConfirmed(true);
                userRepository.save(userToConfirm);
                return ResponseEntity.ok(String.format
                        ("User %s has confirmed e-mail",
                                userToConfirm.getCredential().getLogin()));
            }

        } else {
            throw new UnconfirmedUserException("Confirmation UUID invalid. " +
                    "Id = " + id);
        }

    }
}
