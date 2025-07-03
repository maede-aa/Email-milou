package aut.milou.controller;

import aut.milou.model.Email;
import aut.milou.services.EmailService;

import java.util.List;

public class EmailController {
    private final EmailService emailService = new EmailService();

    public String sendEmail(String sender ,List<String> recipients ,String subject ,String body) {
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
        return emailService.readEmail(userEmail ,code);
    }

    public String reply(String sender ,String originalCode ,String body) {
        return emailService.reply(sender ,originalCode ,body);
    }

    public String forward(String sender ,String code ,List<String> newRecipients) {
        return emailService.forward(sender ,code ,newRecipients);
    }
}
