package tn.enicarthage.enimanage.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    public void sendPasswordEmail(String to, String password) throws MessagingException {
        Context context = new Context();
        context.setVariable("password", password);

        String emailContent = templateEngine.process("password-email", context);

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom("projetpfemailer@gmail.com");
        helper.setTo(to);
        helper.setSubject("Votre mot de passe EniManage");
        helper.setText(emailContent, true);

        mailSender.send(message);
    }
}