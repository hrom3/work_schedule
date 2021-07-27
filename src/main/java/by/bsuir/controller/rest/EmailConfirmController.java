package by.bsuir.controller.rest;

import by.bsuir.controller.exception.UnconfirmedUserException;
import by.bsuir.controller.request.EmailConfirmRequest;
import by.bsuir.domain.ConfirmationData;
import by.bsuir.domain.User;
import by.bsuir.repository.IUserRepository;
import by.bsuir.repository.springdata.IConfirmationDataRepository;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Optional;

@RestController
@RequestMapping("/rest/confirm")
@RequiredArgsConstructor
public class EmailConfirmController {

    private static final Logger log = Logger.getLogger(EmailConfirmController.class);

    private final IUserRepository userRepository;

    private final IConfirmationDataRepository confirmationDataRepository;

    @GetMapping
    @ApiOperation(value = "Confirm user in system")
    public ResponseEntity confirmUser(@ModelAttribute EmailConfirmRequest
                                              confirmRequest) {
        Long id = confirmRequest.getId();
        String uuid = confirmRequest.getUuid();

        Optional<ConfirmationData> resultSet =
                confirmationDataRepository.findById(id);

        if (resultSet.isEmpty()) {
            log.info("Confirmation request invalid resultSet.isEmpty id =" +
                    id);
            throw new UnconfirmedUserException("Confirmation request invalid");
        }

        ConfirmationData confirmationData = resultSet.get();
        long currentTimeMillis = System.currentTimeMillis();
        if (confirmationData.getDueDate().getTime() - currentTimeMillis < 0) {
            log.info("Confirmation time expired id =" + id + " currentTime " +
                    currentTimeMillis + " confirmation due date " +
                    confirmationData.getDueDate().getTime());
            throw new UnconfirmedUserException("Confirmation time expired");
        }

        if (confirmationData.getUuid().equals(uuid)) {
            User userToConfirm = confirmationData.getUser();

            boolean isUserConfirmed = userToConfirm.getIsConfirmed();
            if (isUserConfirmed) {
                return new ResponseEntity<>(String.format
                        ("User %s was already confirmed",
                                userToConfirm.getCredential().getLogin()),
                        HttpStatus.OK);

            } else {
                userToConfirm.setIsConfirmed(true);
                userRepository.update(userToConfirm);
                return new ResponseEntity<>(String.format
                        ("User %s has confirmed e-mail",
                                userToConfirm.getCredential().getLogin()),
                        HttpStatus.CREATED);
            }

        } else {
            log.info("Confirmation UUID invalid. Id =" + id);
            throw new UnconfirmedUserException("Confirmation UUID invalid");
        }

    }



}
