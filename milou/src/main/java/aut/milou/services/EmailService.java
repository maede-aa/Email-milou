package aut.milou.services;

import aut.milou.model.Email;

import java.util.*;
import java.util.stream.Collectors;

public class EmailService {

    private final List<Email> emails = new ArrayList<>();

    public Email send(String sender ,List<String> recipients ,String subject ,String body) {
        String code = generateCode();
        Email email = new Email(code ,sender ,recipients ,subject ,body ,new Date() ,false);
        emails.add(email);
        System.out.println("Successfully sent your email.\nCode: " + code);
        return email;
    }

    public Email reply(String originalCode ,String replier ,String body) {
        Email original = findEmailByCode(originalCode);

        if (original == null || !original.getRecipients().contains(replier)) {
            System.out.println("You cannot reply to this email.");
            return null;
        }

        Set<String> allRecipients = new HashSet<>(original.getRecipients());
        allRecipients.add(original.getSender());
        allRecipients.remove(replier);

        String subject = "[Re] " + original.getSubject();
        return send(replier ,new ArrayList<>(allRecipients) ,subject ,body);
    }

    public Email forward(String originalCode ,String sender ,List<String> newRecipients) {
        Email original = findEmailByCode(originalCode);

        if (original == null || !(original.getRecipients().contains(sender) || original.getSender().equals(sender))) {
            System.out.println("You cannot forward this email.");
            return null;
        }

        String subject = "[Fw] " + original.getSubject();
        return send(sender ,newRecipients ,subject ,original.getBody());
    }

    public List<Email> getInbox(String userEmail) {
        return emails.stream().filter(email -> email.getRecipients().contains(userEmail)).sorted(Comparator.comparing(Email::getDate).reversed()).collect(Collectors.toList());
    }

    public List<Email> getUnread(String userEmail) {
        return emails.stream().filter(email -> email.getRecipients().contains(userEmail) && !email.isRead()).sorted(Comparator.comparing(Email::getDate).reversed()).collect(Collectors.toList());
    }

    public List<Email> getSent(String senderEmail) {
        return emails.stream().filter(email -> email.getSender().equals(senderEmail)).sorted(Comparator.comparing(Email::getDate).reversed()).collect(Collectors.toList());
    }

    public Email getEmailByCode(String userEmail ,String code) {
        for (Email email : emails) {
            if (email.getCode().equals(code) && (email.getSender().equals(userEmail) || email.getRecipients().contains(userEmail))) {
                email.setIsRead(true);
                return email;
            }
        }
        System.out.println("you cannot read this email.");
        return null;
    }

    private Email findEmailByCode(String code) {
        for (Email email : emails) {
            if (email.getCode().equals(code))
                return email;
        }
        return null;
    }

    private String generateCode() {
        String chars = "abcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder code = new StringBuilder();
        Random rand = new Random();
        while (code.length() < 6)
            code.append(chars.charAt(rand.nextInt(chars.length())));

        return code.toString();
    }
}
