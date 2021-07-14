package by.bsuir.service.email;

import javax.mail.MessagingException;
import java.io.FileNotFoundException;

public interface IEmailService {

    void sendSimpleEmail(String toAddress, String subject, String msg);

    void sendEmailWithAttachment(String toAddress, String subject, String msg,
                                String attachment) throws MessagingException,
            FileNotFoundException;

}

