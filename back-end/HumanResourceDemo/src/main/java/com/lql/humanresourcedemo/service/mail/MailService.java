package com.lql.humanresourcedemo.service.mail;

public interface MailService {
    void sendEmail(String to, String subject, String massage);
}
