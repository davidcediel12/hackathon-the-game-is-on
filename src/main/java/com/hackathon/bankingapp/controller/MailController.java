package com.hackathon.bankingapp.controller;


import com.hackathon.bankingapp.services.email.impl.EmailServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MailController {

    private final EmailServiceImpl emailServiceImpl;

    @PostMapping("/testmail")
    public ResponseEntity<Void> sendMail(){
        emailServiceImpl.sendSimpleMessage("esdago18@gmail.com", "test", "body");
        return ResponseEntity.ok().build();
    }
}
