package by.bsuir.controller.rest;

import by.bsuir.service.email.IEmailService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;


@RestController
@RequestMapping("/rest/email")
@RequiredArgsConstructor
public class EmailRestController {

    private static final Logger log = Logger.getLogger(EmailRestController.class);

    private final IEmailService emailService;

    @ApiOperation(value = "Send email")
    @GetMapping(value = "/simple-email")
    //@ResponseStatus(HttpStatus.OK)
    public ResponseEntity sendSimpleEmail(@RequestParam String email) {

        try {
            emailService.sendSimpleEmail(email, "Welcome", "This is a welcome email for your!!");
        } catch (MailException mailException) {
            log.error("Error while sending out email..{}" +
                    Arrays.toString(mailException.getStackTrace()), mailException);
            return new ResponseEntity<>("Unable to send email", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>("Please check your inbox", HttpStatus.OK);
    }
}
