package by.bsuir.security.service;

import by.bsuir.domain.Credential;
import by.bsuir.domain.ESystemRoles;
import by.bsuir.domain.Role;
import by.bsuir.domain.User;
import by.bsuir.repository.ICredentialRepository;
import by.bsuir.repository.IHibernateUserRepository;
import by.bsuir.repository.IRoleRepository;
import by.bsuir.repository.IUserRepository;
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

    private final IHibernateUserRepository userRepository;

    private final IRoleRepository roleRepository;

    private final ICredentialRepository credentialRepository;

    @Override
    public UserDetails loadUserByUsername(String login)
            throws UsernameNotFoundException {
        try {
            Optional<User> searchResult = Optional.ofNullable(userRepository
                    .findUserByLogin(login));

            if (searchResult.isPresent()) {
                User user = searchResult.get();
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
        } catch (Exception e) {
            throw new UsernameNotFoundException("User with this login not found");
        }
    }
}
