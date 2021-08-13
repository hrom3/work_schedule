package by.bsuir.service.email.impl;

import by.bsuir.service.email.IEmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring3.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;


import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements IEmailService {

    public final JavaMailSender mailSender;

    private final SpringTemplateEngine templateEngine;


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
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
        mimeMessageHelper.setTo(toAddress);
        mimeMessageHelper.setSubject(subject);
        mimeMessageHelper.setText(msg);
        FileSystemResource file = new FileSystemResource(ResourceUtils.getFile(attachment));
        mimeMessageHelper.addAttachment("Purchase Order", file);
        mailSender.send(mimeMessage);
    }

    @Override
    public void sendMail(AbstractEmailContext email) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message,
                MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                StandardCharsets.UTF_8.name());
        Context context = new Context();
        context.setVariables(email.getContext());


        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setCharacterEncoding("UTF-8");
        templateResolver.setOrder(0);

        templateEngine.setTemplateResolver(templateResolver);

        String emailContent = templateEngine.process("creat_account_email", context);

        mimeMessageHelper.setTo(email.getTo());
        mimeMessageHelper.setSubject(email.getSubject());
        mimeMessageHelper.setFrom(email.getFrom());
        mimeMessageHelper.setText(emailContent, true);
        mailSender.send(message);
    }
}
