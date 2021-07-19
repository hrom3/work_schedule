package by.bsuir.controller.rest;

import by.bsuir.domain.ConfirmationData;
import by.bsuir.domain.User;
import by.bsuir.repository.IUserRepository;
import by.bsuir.service.email.IEmailService;
import by.bsuir.service.email.impl.AbstractEmailContext;
import by.bsuir.util.ConfirmationDataGenerator;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/rest/email")
@RequiredArgsConstructor
public class EmailRestController {

    private static final Logger log = Logger.getLogger(EmailRestController.class);

    private final IEmailService emailService;

    private final IUserRepository userRepository;

    private final ConfirmationDataGenerator confirmationDataGenerator;

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

    @ApiOperation(value = "Send confirmation email")
    @GetMapping(value = "/confirmation-email/{id}")
    public ResponseEntity sendConfirmationEmail(@PathVariable Long id) {

        User userToConfirmation = userRepository.findOne(id);
        String emailToConfirmation = userToConfirmation.getEmail();
        ConfirmationData confirmationDataGenerated =
                confirmationDataGenerator.generate(/*userToConfirmation*/);
        //TODO: save confirmationDataGenerated data

        String idFromConfirmationData = confirmationDataGenerated.getId().toString();
        String uuidFromConfirmationData = confirmationDataGenerated.getUuid();
        String urlToConfirmPage = "http://localhost:8081/rest/confirm";
        AbstractEmailContext emailContext = new AbstractEmailContext();

        Map<String, Object> contextMap = new HashMap<>();
        contextMap.put("firstName", userToConfirmation.getName());
        contextMap.put("verificationURL", urlToConfirmPage + "?=id"
                + idFromConfirmationData + "&uuid=" + uuidFromConfirmationData);

        //TODO: Add to property fromEmail
        emailContext.setFrom("blablabla@mail.mmm");
        emailContext.setTo(emailToConfirmation);
        emailContext.setSubject("Verify your email");
        emailContext.setTemplateLocation("creat_account_email");
        emailContext.setContext(contextMap);


        try {
            emailService.sendMail(emailContext);
        } catch (MailException mailException) {
            log.error("Error while sending out email to " +
                    emailToConfirmation + " {}" +
                    Arrays.toString(mailException.getStackTrace()), mailException);
            return new ResponseEntity<>("Unable to send email",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (MessagingException messagingException) {
            log.error("Error while sending out message " +
                    Arrays.toString(messagingException.getStackTrace()), messagingException);
            return new ResponseEntity<>("Unable to send email",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>("Please check your inbox " +
                emailToConfirmation, HttpStatus.OK);
    }
}
