package com.hackathon.bankingapp.services.email;

public interface EmailService {

    void sendSimpleMessage(String to, String subject, String text);
}
