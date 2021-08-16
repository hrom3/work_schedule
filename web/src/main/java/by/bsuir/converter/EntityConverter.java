package by.bsuir.converter;

import by.bsuir.controller.request.UserCreateRequest;
import by.bsuir.domain.ConfirmationData;
import by.bsuir.domain.Credential;
import by.bsuir.domain.User;

import org.springframework.core.convert.converter.Converter;

import java.sql.Timestamp;
import java.time.LocalDate;

public abstract class EntityConverter<S,T> implements Converter<S, T> {
    protected User doConvert(UserCreateRequest request, User user) {

        user.setName(request.getName());

        user.setSurname(request.getSurname());
        user.setMiddleName(request.getMiddleName());
        user.setEmail(request.getEmail());
        user.setBirthDay(LocalDate.parse(request.getBirthDay()));
        user.setIsDeleted(false);
        user.setIsConfirmed(false);

        Credential credentialForGeneratedUser = new Credential();
        credentialForGeneratedUser.setLogin(request.getLogin());

        user.setCredential(credentialForGeneratedUser);
        user.setChanged(new Timestamp(System.currentTimeMillis()));

        return user;
    }
}
