package com.lql.humanresourcedemo.service.mail;


import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {
    private final JavaMailSender mailSender;

    @Async
    public void sendEmail(String to, String subject, String msg) {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper messageHelper;

        try {
            messageHelper = new MimeMessageHelper(message, "utf-8");
            messageHelper.setTo(to);
            messageHelper.setSubject(subject);
            messageHelper.setText(msg);
            mailSender.send(message);

        } catch (Exception exception) {
            throw new MailSendException("Error sending email to %s, subject: %s, message: %s".formatted(to, subject, message));
        }
    }
}
