package by.bsuir.security.controller;

import by.bsuir.controller.exception.UnconfirmedUserException;
import by.bsuir.domain.User;
import by.bsuir.repository.IUserRepository;
import by.bsuir.security.requests.AuthRequest;
import by.bsuir.security.requests.AuthResponse;
import by.bsuir.security.utils.TokenUtils;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/authentication")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;

    private final TokenUtils tokenUtils;

    private final UserDetailsService userProvider;

    private final IUserRepository userRepository;

    @ApiOperation(value = "Login user in system",
            notes = "Return Auth-Token with user login")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Successful authorization"),
            @ApiResponse(code = 400, message = "Request error"),
            @ApiResponse(code = 423, message = "User is not confirmed"),
            @ApiResponse(code = 500, message = "Server error")
    })
    @PostMapping
    public ResponseEntity<AuthResponse> loginUser(@RequestBody AuthRequest request
            /*, @ApiIgnore Principal principal*/) {

        //TODO: take out in a separate method
        /*Check is e-mail confirmed*/
        Optional<User> searchResult = Optional.ofNullable(userRepository
                .findUserByLogin(request.getLogin()));
        if (searchResult.isPresent()) {
            User user = searchResult.get();
            boolean isEmailConfirmed = user.getIsConfirmed();
            if (!isEmailConfirmed) {
                throw new UnconfirmedUserException(String.format
                        ("E-mail %s is not confirmed by user '%s'.",
                                user.getEmail(), request.getLogin()));
            }
        }
        /*Check login and password*/
        Authentication authenticate = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getLogin(),
                        request.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authenticate);


        /*Generate token with answer to user*/
        return ResponseEntity.ok(
                AuthResponse
                        .builder()
                        .login(request.getLogin())
                        .token(tokenUtils.generateToken(userProvider
                                .loadUserByUsername(request.getLogin())))
                        .build()
        );
    }
}
