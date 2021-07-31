package by.bsuir.security.service;

import by.bsuir.controller.exception.UnconfirmedUserException;
import by.bsuir.domain.ESystemRoles;
import by.bsuir.domain.Role;
import by.bsuir.domain.User;
import by.bsuir.repository.IRoleRepository;
import by.bsuir.repository.springdata.ICredentialDataRepository;
import by.bsuir.repository.springdata.IUserDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class UserProviderService implements UserDetailsService {

    private final IUserDataRepository userRepository;

    private final IRoleRepository roleRepository;

    private final ICredentialDataRepository credentialRepository;

    @Override
    public UserDetails loadUserByUsername(String login)
            throws UsernameNotFoundException, UnconfirmedUserException {
        try {
            Optional<User> searchResult = userRepository
                    .findUserByCredentialLogin(login);

            if (searchResult.isPresent()) {
                User user = searchResult.get();
                if(!user.getIsConfirmed()) {
                    throw new UnconfirmedUserException(String.format
                            ("E-mail %s of  user is not confirmed by user '%s'.",
                                    user.getEmail(), login));
                }
                return new org.springframework.security.core.userdetails.User(
                        user.getCredential().getLogin(),
                        user.getCredential().getPassword(),
//                        ["ROLE_USER"]
                        AuthorityUtils.commaSeparatedStringToAuthorityList(
                                user.getRoles()
                                        .stream()
                                        .map(Role::getRoleName)
                                        .map(ESystemRoles::name)
                                        .collect(Collectors.joining( ", ")))
                );
            } else {
                throw new UsernameNotFoundException(String.format
                        ("No user found with login '%s'.", login));
            }
        } catch (UnconfirmedUserException ex) {
            throw new UsernameNotFoundException(ex.getMessage());

        } catch (Exception e) {
            throw new UsernameNotFoundException("User with this login not found");
        }
    }
}
