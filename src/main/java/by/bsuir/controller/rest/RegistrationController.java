package by.bsuir.controller.rest;

import by.bsuir.controller.request.UserCreateRequest;
import by.bsuir.domain.Credential;
import by.bsuir.domain.Role;
import by.bsuir.domain.User;
import by.bsuir.repository.ICredentialRepository;
import by.bsuir.repository.IRoleRepository;
import by.bsuir.repository.IJDBCUserRepository;
import by.bsuir.util.UserGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/registration")
@RequiredArgsConstructor
public class RegistrationController {

    private final PasswordEncoder passwordEncoder;

    private final IJDBCUserRepository userRepository;

    private final ICredentialRepository credentialRepository;

    private final IRoleRepository roleRepository;

    private final UserGenerator userGenerator;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User createUser(@ModelAttribute UserCreateRequest createRequest) {
        //converter
        User generatedUser = userGenerator.generateLiteUser();
//        User generatedUser = userGenerator.generate();

        generatedUser.setName(createRequest.getName());
        generatedUser.setSurname(createRequest.getSurname());
        generatedUser.setMiddleName(createRequest.getMiddleName());
        generatedUser.setEmail(createRequest.getEmail());
        generatedUser.setBirthDay(LocalDate.parse(createRequest.getBirthDay()));
        //generatedUser.setDepartmentId(createRequest.getDepartmentId());
        //generatedUser.setRateId(createRequest.getRateId());
        //generatedUser.setRoomId(createRequest.getRoomId());
        generatedUser.setIsDeleted(false);

        Credential credentialForGeneratedUser = new Credential();

        credentialForGeneratedUser.setLogin(createRequest.getLogin());
        credentialForGeneratedUser.setPassword(passwordEncoder.encode(createRequest.getPassword()));

        User savedUser = userRepository.save(generatedUser);

//        Credential credentialForGeneratedUser = new Credential();
//
//        credentialForGeneratedUser.setLogin(createRequest.getLogin());
//        credentialForGeneratedUser.setPassword(passwordEncoder.encode(createRequest.getPassword()));

        credentialRepository.saveUserCredentials(savedUser,
                credentialForGeneratedUser);

        List<Role> roles = roleRepository.findAll();

        userRepository.saveUserRoles(savedUser, roles);

        return savedUser;
    }
}
