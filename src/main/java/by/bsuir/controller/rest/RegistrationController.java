package by.bsuir.controller.rest;

import by.bsuir.controller.exception.NoSuchEntityException;
import by.bsuir.controller.request.UserCreateRequest;
import by.bsuir.domain.*;
import by.bsuir.repository.obsolete.ICredentialRepository;
import by.bsuir.repository.obsolete.IDepartmentRepository;
import by.bsuir.repository.obsolete.IRateRepository;
import by.bsuir.repository.obsolete.IRoomsRepository;
import by.bsuir.repository.springdata.*;
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

@Api(value = "Registration controller")
@RestController
@RequestMapping("/registration")
@RequiredArgsConstructor
public class RegistrationController {

    private static final Logger log = Logger.getLogger(RegistrationController.class);

    private final PasswordEncoder passwordEncoder;

    private final ICredentialDataRepository credentialRepository;

    private final IDepartmentDataRepository departmentRepository;

    private final IRateDataRepository rateRepository;

    private final IRoomDataRepository roomRepository;

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
    public ResponseEntity<String> createUser(@ModelAttribute UserCreateRequest createRequest) {
        //converter
        User generatedUser = userGenerator.generateLiteUser();

        Department department;
        Optional<Department> searchDepResult = departmentRepository
                .findById(createRequest.getDepartmentId());
        if (searchDepResult.isPresent()) {
            department = searchDepResult.get();
        } else {
            throw new NoSuchEntityException("No such room with id:"
                    + createRequest.getDepartmentId());
        }

        Rate rate;
        Optional<Rate> searchRateResult = rateRepository
                .findById(createRequest.getRateId());
        if (searchRateResult.isPresent()) {
            rate = searchRateResult.get();
        } else {
            throw new NoSuchEntityException("No such rate with id:"
                    + createRequest.getRateId());
        }

        Room room;
        Optional<Room> searchRoomResult = roomRepository
                .findById(createRequest.getRoomId());
        if (searchRoomResult.isPresent()) {
            room = searchRoomResult.get();
        } else {
            throw new NoSuchEntityException("No such room with id:"
                    + createRequest.getRoomId());
        }

       /* Role foundRole = roleRepository.findById(createRequest.getRoleId()).get();
        Set<Role> roles = new HashSet<>();
        roles.add(foundRole);*/

        generatedUser.setName(createRequest.getName());
        generatedUser.setSurname(createRequest.getSurname());
        generatedUser.setMiddleName(createRequest.getMiddleName());
        generatedUser.setEmail(createRequest.getEmail());
        generatedUser.setBirthDay(LocalDate.parse(createRequest.getBirthDay()));
        generatedUser.setDepartment(department);
        generatedUser.setRate(rate);
        generatedUser.setRoom(room);
        generatedUser.setIsDeleted(false);
        generatedUser.setIsConfirmed(false);


        Credential credentialForGeneratedUser = new Credential();

        credentialForGeneratedUser.setLogin(createRequest.getLogin());
        credentialForGeneratedUser.setPassword(passwordEncoder
                .encode(createRequest.getPassword()));


        User savedUser = userDataRepository.save(generatedUser);

        userDataRepository.saveUserRole(savedUser.getId(), 2);

        /*Credential credentialForGeneratedUser = new Credential();*/
//
//        credentialForGeneratedUser.setLogin(createRequest.getLogin());
//        credentialForGeneratedUser.setPassword(passwordEncoder
//                .encode(createRequest.getPassword()));
        credentialForGeneratedUser.setUser(savedUser);

        credentialRepository.save(credentialForGeneratedUser);

        savedUser.setCredential(credentialForGeneratedUser);

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
            return new ResponseEntity("Unable to send email",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (MessagingException messagingException) {
            log.error("Error while sending out message " +
                    Arrays.toString(messagingException.getStackTrace()), messagingException);
            return new ResponseEntity("Unable to send email",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity("User "+
                savedConfirmationData.getUser().getCredential().getLogin()
                + " created. Please check your inbox " +
                emailToConfirmation, HttpStatus.OK);
    }
}
