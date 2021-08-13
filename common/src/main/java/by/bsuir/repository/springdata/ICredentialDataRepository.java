package by.bsuir.repository.springdata;

import by.bsuir.domain.Credential;
import by.bsuir.domain.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;


public interface ICredentialDataRepository extends CrudRepository<Credential, Long>,
        JpaRepository<Credential, Long> {

    Credential findCredentialByUser(User user);
}
