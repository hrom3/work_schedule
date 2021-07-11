package by.bsuir.repository;

import by.bsuir.domain.Credential;
import by.bsuir.domain.User;

public interface ICredentialRepository extends ICrudOperations<Long, Credential> {

    Credential findByUser(User user);

    void saveUserCredentials(User user, Credential userCredential);

    Credential findByLogin(String login);
}
