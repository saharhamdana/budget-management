package com.onetech.budget.controllers;

import com.onetech.budget.services.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestEmailController {

    private final EmailService emailService;

    @Autowired
    public TestEmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @GetMapping("/send-test-email")
    public String sendTestEmail(@RequestParam String to) {
        emailService.sendEmail(
                to,
                "Test Email",
                "Ceci est un test d'envoi d'email depuis Spring Boot ✅"
        );
        return "Email envoyé à : " + to;
    }
}
