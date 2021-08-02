package by.bsuir.controller.rest;

import by.bsuir.controller.request.UserCreateRequest;
import by.bsuir.domain.ConfirmationData;
import by.bsuir.domain.Credential;
import by.bsuir.domain.User;
import by.bsuir.repository.*;
import by.bsuir.repository.springdata.IConfirmationDataRepository;
import by.bsuir.repository.springdata.IUserDataRepository;
import by.bsuir.service.email.IEmailService;
import by.bsuir.service.email.impl.AbstractEmailContext;
import by.bsuir.util.ConfirmationDataGenerator;
import by.bsuir.util.UserGenerator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import java.time.LocalDate;
import java.util.*;

@Api(description = "Регистрационный контроллер")
@RestController
@RequestMapping("/registration")
@RequiredArgsConstructor
public class RegistrationController {

    private static final Logger log = Logger.getLogger(RegistrationController.class);

    private final PasswordEncoder passwordEncoder;

    private final ICredentialRepository credentialRepository;

    private final IDepartmentRepository departmentRepository;

    private final IRateRepository rateRepository;

    private final IRoomsRepository roomRepository;

    private final IConfirmationDataRepository confirmationDataRepository;

    private final IEmailService emailService;

    private final IUserDataRepository userDataRepository;

    private final ConfirmationDataGenerator confirmationDataGenerator;

    private final UserGenerator userGenerator;

    @PostMapping
    @ApiOperation(value = "Create user in system")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Users was successfully created!"),
            @ApiResponse(code = 500, message = "Internal server error! https://stackoverflow." +
                    "com/questions/37405244/how-to-change-the-response-status-code-for-" +
                    "successful-operation-in-swagger")
    })
    public ResponseEntity createUser(@ModelAttribute UserCreateRequest createRequest) {
        //converter
        User generatedUser = userGenerator.generateLiteUser();

        generatedUser.setName(createRequest.getName());
        generatedUser.setSurname(createRequest.getSurname());
        generatedUser.setMiddleName(createRequest.getMiddleName());
        generatedUser.setEmail(createRequest.getEmail());
        generatedUser.setBirthDay(LocalDate.parse(createRequest.getBirthDay()));
        generatedUser.setDepartment(departmentRepository
                .findOne(createRequest.getDepartmentId()));
        generatedUser.setRate(rateRepository.findOne(createRequest.getRateId()));
        generatedUser.setRoom(roomRepository.findOne(createRequest.getRoomId()));
        generatedUser.setIsDeleted(false);
        generatedUser.setIsConfirmed(false);

        User savedUser = userDataRepository.save(generatedUser);

        userDataRepository.saveUserRole(savedUser.getId(), createRequest.getRoleId());

        Credential credentialForGeneratedUser = new Credential();

        credentialForGeneratedUser.setLogin(createRequest.getLogin());
        credentialForGeneratedUser.setPassword(passwordEncoder
                .encode(createRequest.getPassword()));
        credentialForGeneratedUser.setUser(savedUser);

        credentialRepository.saveUserCredentials(savedUser, credentialForGeneratedUser);

        ConfirmationData confirmationDataGenerated = confirmationDataGenerator
                .generate(savedUser);

        ConfirmationData savedConfirmationData = confirmationDataRepository
                .save(confirmationDataGenerated);

        AbstractEmailContext emailContext = new AbstractEmailContext();

        //TODO: create normal var
        String urlToConfirmPage = "http://localhost:8081/rest/confirm";
        String emailToConfirmation = savedConfirmationData.getUser().getEmail();

        Map<String, Object> contextMap = new HashMap<>();
        contextMap.put("firstName", savedConfirmationData.getUser().getName());
        contextMap.put("verificationURL", urlToConfirmPage +
                "?id=" + savedConfirmationData.getId() +
                "&uuid=" + savedConfirmationData.getUuid());

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
        return new ResponseEntity<>("User "+
                savedConfirmationData.getUser().getCredential().getLogin()
                + " created. Please check your inbox " +
                emailToConfirmation, HttpStatus.CREATED);
    }
}
