package by.bsuir.service.email.impl;

import by.bsuir.service.email.IEmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.spring3.SpringTemplateEngine;


import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;

@Service
public class EmailServiceImpl implements IEmailService {

    @Autowired
    public JavaMailSender mailSender;

  //  @Autowired
  //  private SpringTemplateEngine templateEngine;

    @Override
    public void sendSimpleEmail(String toAddress, String subject, String msg) {

        var simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(toAddress);
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setText(msg);
        mailSender.send(simpleMailMessage);
    }

    @Override
    public void sendEmailWithAttachment(String toAddress, String subject,
                                        String msg, String attachment)
            throws MessagingException, FileNotFoundException {

    }

//    @Override
//    public void sendMail(AbstractEmailContext email) throws MessagingException {
//        MimeMessage message = mailSender.createMimeMessage();
//        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message,
//                MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
//                StandardCharsets.UTF_8.name());
//        Context context = new Context();
//        context.setVariables(email.getContext());
//        String emailContent = templateEngine.process(email.getTemplateLocation(), context);
//
//        mimeMessageHelper.setTo(email.getTo());
//        mimeMessageHelper.setSubject(email.getSubject());
//        mimeMessageHelper.setFrom(email.getFrom());
//        mimeMessageHelper.setText(emailContent, true);
//        emailSender.send(message);
//    }
}
