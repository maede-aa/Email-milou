package aut.milou.controller;

import aut.milou.model.Email;
import aut.milou.services.EmailService;

import java.util.List;

public class EmailController {
    private final EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    public String sendEmail(String sender ,List<String> recipients ,String subject ,String body) {
        if (recipients == null || recipients.isEmpty())
            throw new IllegalArgumentException("At least one recipient is required.");
        if (subject == null || subject.isEmpty())
            throw new IllegalArgumentException("Subject cannot be empty.");

        return emailService.send(sender ,recipients ,subject ,body);
    }

    public List<Email> viewAll(String userEmail) {
        return emailService.getAllReceivedEmails(userEmail);
    }

    public List<Email> viewUnread(String userEmail) {
        return emailService.getUnreadEmails(userEmail);
    }

    public List<Email> viewSent(String userEmail) {
        return emailService.getSentEmails(userEmail);
    }

    public Email readEmail(String userEmail ,String code) {
        if (code == null || code.length() != 6)
            throw new IllegalArgumentException("Invalid email code.");

        return emailService.readEmail(userEmail ,code);
    }

    public String reply(String sender ,String originalCode ,String body) {
        if (body == null || body.isEmpty())
            throw new IllegalArgumentException("Reply body cannot be empty.");

        return emailService.reply(sender ,originalCode ,body);
    }

    public String forward(String sender ,String code ,List<String> newRecipients) {
        if (newRecipients == null || newRecipients.isEmpty())
            throw new IllegalArgumentException("At least one recipient is required.");

        return emailService.forward(sender ,code ,newRecipients);
    }
}