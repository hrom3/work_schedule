package by.bsuir.controller.rest;

import by.bsuir.beans.EmailProperties;
import by.bsuir.controller.request.UserCreateRequest;
import by.bsuir.domain.ConfirmationData;
import by.bsuir.domain.Credential;
import by.bsuir.domain.User;
import by.bsuir.repository.RepositoryUtils;
import by.bsuir.repository.springdata.IConfirmationDataRepository;
import by.bsuir.repository.springdata.ICredentialDataRepository;
import by.bsuir.repository.springdata.IDepartmentDataRepository;
import by.bsuir.repository.springdata.IRateDataRepository;
import by.bsuir.repository.springdata.IRoomDataRepository;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.MessagingException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Api(value = "Registration controller")
@RestController
@RequestMapping("/registration")
@RequiredArgsConstructor
public class RegistrationController {

    private static final Logger log = LoggerFactory.getLogger(RegistrationController.class);

    private final PasswordEncoder passwordEncoder;

    private final ICredentialDataRepository credentialRepository;

    private final IDepartmentDataRepository departmentRepository;

    private final IRateDataRepository rateRepository;

    private final IRoomDataRepository roomRepository;

    private final RepositoryUtils repositoryUtils;

    private final IConfirmationDataRepository confirmationDataRepository;

    private final IEmailService emailService;

    private final IUserDataRepository userDataRepository;

    private final ConfirmationDataGenerator confirmationDataGenerator;

    private final UserGenerator userGenerator;

    private final EmailProperties emailProperties;


    @PostMapping
    @ApiOperation(value = "Create user in system")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Users was successfully created!"),
            @ApiResponse(code = 500, message = "Internal server error! https://stackoverflow." +
                    "com/questions/37405244/how-to-change-the-response-status-code-for-" +
                    "successful-operation-in-swagger")
    })
    public ResponseEntity<String> createUser(@ModelAttribute UserCreateRequest createRequest) {
        //converter
        User generatedUser = userGenerator.generateLiteUser();

       /* Role foundRole = roleRepository.findById(createRequest.getRoleId()).get();
        Set<Role> roles = new HashSet<>();
        roles.add(foundRole);*/

        generatedUser.setName(createRequest.getName());
        generatedUser.setSurname(createRequest.getSurname());
        generatedUser.setMiddleName(createRequest.getMiddleName());
        generatedUser.setEmail(createRequest.getEmail());
        generatedUser.setBirthDay(LocalDate.parse(createRequest.getBirthDay()));
        generatedUser.setDepartment(repositoryUtils.findDepartmentById(
                departmentRepository, createRequest.getDepartmentId()));
        generatedUser.setRate(repositoryUtils.findRateById(
                rateRepository, createRequest.getRateId()));
        generatedUser.setRoom(repositoryUtils.findRoomById(
                roomRepository, createRequest.getRoomId()));
        generatedUser.setIsDeleted(false);
        generatedUser.setIsConfirmed(false);

        Credential credentialForGeneratedUser = new Credential();

        credentialForGeneratedUser.setLogin(createRequest.getLogin());
        credentialForGeneratedUser.setPassword(passwordEncoder.encode(
                createRequest.getPassword()));

        userDataRepository.save(generatedUser);

        userDataRepository.saveUserRole(generatedUser.getId(), 2);

        credentialForGeneratedUser.setUser(generatedUser);

        credentialRepository.save(credentialForGeneratedUser);

        generatedUser.setCredential(credentialForGeneratedUser);

        ConfirmationData confirmationData = confirmationDataGenerator
                .generate(generatedUser);

        confirmationDataRepository.save(confirmationData);

        AbstractEmailContext emailContext = new AbstractEmailContext();

        String urlToConfirmPage = emailProperties.getUrlToConfirmPage();
        String emailToConfirmation = confirmationData.getUser().getEmail();

        Map<String, Object> contextMap = new HashMap<>();
        contextMap.put("firstName", confirmationData.getUser().getName());
        contextMap.put("verificationURL", urlToConfirmPage +
                "?id=" + confirmationData.getId() +
                "&uuid=" + confirmationData.getUuid());

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
        return new ResponseEntity<>("User " +
                confirmationData.getUser().getCredential().getLogin()
                + " created. Please check your inbox " +
                emailToConfirmation, HttpStatus.OK);
    }
}
