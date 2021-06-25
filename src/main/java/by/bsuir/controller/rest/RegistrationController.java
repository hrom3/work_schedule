package by.bsuir.controller.rest;

import by.bsuir.controller.request.UserCreateRequest;
import by.bsuir.domain.Credential;
import by.bsuir.domain.Role;
import by.bsuir.domain.User;
import by.bsuir.repository.ICredentialRepository;
import by.bsuir.repository.IRoleRepository;
import by.bsuir.repository.IUserRepository;
import by.bsuir.security.utils.TokenUtils;
import by.bsuir.util.UserGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/registration")
@RequiredArgsConstructor
public class RegistrationController {

    private final AuthenticationManager authenticationManager;

    private final TokenUtils tokenUtils;

    private final UserDetailsService userProvider;

    private final IUserRepository userRepository;

    private final ICredentialRepository credentialRepository;

    private final IRoleRepository roleRepository;

    private final UserGenerator userGenerator;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User createUser(@ModelAttribute UserCreateRequest createRequest) {

        User generatedUser = userGenerator.generateLiteUser();
//        User generatedUser = userGenerator.generate();

        generatedUser.setName(createRequest.getName());
        generatedUser.setSurname(createRequest.getSurname());
        generatedUser.setMiddleName(createRequest.getMiddleName());
        generatedUser.setEmail(createRequest.getEmail());
        generatedUser.setBirthDay(LocalDate.parse(createRequest.getBirthDay()));
        generatedUser.setDepartmentId(createRequest.getDepartmentId());
        generatedUser.setRateId(createRequest.getRateId());
        generatedUser.setRoomId(createRequest.getRoomId());

        User savedUser = userRepository.save(generatedUser);

        Credential credentialForGeneratedUser = new Credential();

        credentialForGeneratedUser.setLogin(createRequest.getLogin());
        credentialForGeneratedUser.setPassword(createRequest.getPassword());

        credentialRepository.saveUserCredentials(savedUser,
                credentialForGeneratedUser);

        List<Role> roles = roleRepository.findAll();

        userRepository.saveUserRoles(savedUser, roles);

        return savedUser;
    }
}
