package by.bsuir.util;

import by.bsuir.domain.ConfirmationData;
import by.bsuir.domain.User;

import java.sql.Timestamp;
import java.util.UUID;


public class ConfirmationDataGenerator {

    public ConfirmationData generate(User user) {

        //TODO: refactor var
        final long LIFETIME_CONFIRMATION_UUID_IN_MILLIS = 86_000_000L;

        ConfirmationData confirmationData = new ConfirmationData();

        confirmationData.setUser(user);
        confirmationData.setUuid(UUID.randomUUID().toString());
        confirmationData.setDueDate(new Timestamp(System.currentTimeMillis()
                + LIFETIME_CONFIRMATION_UUID_IN_MILLIS));


        return confirmationData;
    }


}
