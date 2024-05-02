package com.lql.humanresourcedemo.service.mail;

import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MailServiceTest {
    @Mock
    private JavaMailSender mailSender;
    private MailService mailService;

    private final String ERROR_MESSAGE = "error";

    @BeforeEach
    void setUp() {
        mailService = new MailServiceImpl(mailSender);
    }

    @Test
    void sendMail_Success() {
        String to = "abc@gmail.com";
        String subject = "";
        String message = "";
        when(mailSender.createMimeMessage()).thenReturn(Mockito.mock(MimeMessage.class));

        mailService.sendEmail(to, subject, message);

        verify(mailSender, times(1)).createMimeMessage();
        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    void sendMail_Fail() {
        String to = "abc@gmail.com";
        String subject = "";
        String message = "";
        when(mailSender.createMimeMessage()).thenReturn(Mockito.mock(MimeMessage.class));
        doThrow(new MailSendException(ERROR_MESSAGE)).when(mailSender).send(any(MimeMessage.class));


        assertThrows(MailSendException.class,
                () -> mailService.sendEmail(to, subject, message),
                "Error sending email to %s, subject: %s, message: %s".formatted(to, subject, message));

    }
}