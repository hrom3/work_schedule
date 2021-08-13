package by.bsuir.controller.rest;

import by.bsuir.beans.EmailProperties;
import by.bsuir.domain.ConfirmationData;
import by.bsuir.domain.User;
import by.bsuir.exception.NoSuchEntityException;
import by.bsuir.repository.springdata.IConfirmationDataRepository;
import by.bsuir.repository.springdata.IUserDataRepository;
import by.bsuir.service.email.IEmailService;
import by.bsuir.service.email.impl.AbstractEmailContext;
import by.bsuir.util.ConfirmationDataGenerator;
import by.bsuir.util.MyMessages;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.MessagingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Api(value = "Email controller for tests only")
@RestController
@RequestMapping("/rest/email")
@RequiredArgsConstructor
public class EmailRestController {

    private static final Logger log = LoggerFactory.getLogger(EmailRestController.class);

    private final IEmailService emailService;

    private final IUserDataRepository userRepository;

    private final IConfirmationDataRepository confirmationDataRepository;

    private final ConfirmationDataGenerator confirmationDataGenerator;

    private final EmailProperties emailProperties;

    @ApiOperation(value = "Send email (for test)")
    @GetMapping(value = "/simple-email")
    public ResponseEntity<String> sendSimpleEmail(@RequestParam String email) {

        try {
            emailService.sendSimpleEmail(email,
                    "Welcome",
                    "This is a welcome email for your!!");
        } catch (MailException mailException) {
            log.error("Error while sending out email..{}" +
                            Arrays.toString(mailException.getStackTrace()),
                    mailException);
            return new ResponseEntity<>("Unable to send email",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>("Please check your inbox",
                HttpStatus.OK);
    }

    @ApiOperation(value = "Send confirmation email (for test)")
    @GetMapping(value = "/confirmation-email/{id}")
    public ResponseEntity<String> sendConfirmationEmail(@PathVariable Long id) {

        Optional<User> searchResult =
                userRepository.findById(id);
        User userToConfirmation;
        if (searchResult.isPresent()) {
            userToConfirmation = searchResult.get();
        } else {
            throw new NoSuchEntityException(MyMessages.NO_SUCH_USER + id);
        }

        String emailToConfirmation = userToConfirmation.getEmail();
        ConfirmationData confirmationDataGenerated =
                confirmationDataGenerator.generate(userToConfirmation);

        confirmationDataRepository.save(confirmationDataGenerated);

        String idFromConfirmationData = confirmationDataGenerated.getId().toString();
        String uuidFromConfirmationData = confirmationDataGenerated.getUuid();
        String urlToConfirmPage = emailProperties.getUrlToConfirmPage();
        AbstractEmailContext emailContext = new AbstractEmailContext();

        Map<String, Object> contextMap = new HashMap<>();
        contextMap.put("firstName", userToConfirmation.getName());
        contextMap.put("verificationURL", urlToConfirmPage + "?id="
                + idFromConfirmationData + "&uuid=" + uuidFromConfirmationData);

        emailContext.setFrom(emailProperties.getFromEmail());
        emailContext.setTo(emailToConfirmation);
        emailContext.setSubject(emailProperties.getSubject());
        emailContext.setTemplateLocation(emailProperties.getTemplate());
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
