package aut.milou.services;

import aut.milou.model.Email;
import aut.milou.model.Recipient;
import aut.milou.model.User;
import aut.milou.Repository.EmailRepository;
import aut.milou.Repository.UserRepository;

import java.security.SecureRandom;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class EmailService {
    private static final Logger logger = Logger.getLogger(EmailService.class.getName());
    private final EmailRepository emailRepository;
    private final UserRepository userRepository;
    private static final String CHARS = "abcdefghijklmnopqrstuvwxyz0123456789";
    private static final int CODE_LENGTH = 6;

    public EmailService(EmailRepository emailRepository, UserRepository userRepository) {
        this.emailRepository = emailRepository;
        this.userRepository = userRepository;
    }

    public String send(String senderEmail, List<String> recipients, String subject, String body) {
        if(senderEmail == null) {
            throw new IllegalArgumentException("Sender not found.");
        }

        senderEmail = normalizeEmail(senderEmail);
        Optional<User> sender = userRepository.findByEmail(senderEmail);
        if (sender.isEmpty()) {
            throw new IllegalArgumentException("Sender not found.");
        }

        List<String> normalizedRecipients = normalizeRecipients(recipients);
        if (normalizedRecipients.isEmpty()) {
            logger.warning("No valid recipients for sender: " + senderEmail);
            throw new IllegalArgumentException("No valid recipients.");
        }

        String code = generateUniqueCode();
        List<Recipient> recipientEntities = normalizedRecipients.stream().map(email -> new Recipient(email, null)).collect(Collectors.toList());

        Email email = new Email(code ,sender.get() ,recipientEntities ,subject ,body ,new Date());
        recipientEntities.forEach(recipient -> recipient.setEmail(email));
        emailRepository.save(email);
        return code;
    }

    private List<String> normalizeRecipients(List<String> recipients) {
        if (recipients == null || recipients.isEmpty()) {
            return new ArrayList<>();
        }
        List<String> normalized = new ArrayList<>();
        for (String email : recipients) {
            if (email != null) {
                String norm = normalizeEmail(email.trim());
                if (norm != null && userRepository.findByEmail(norm).isPresent() && !normalized.contains(norm)) {
                    normalized.add(norm);
                }
            }
        }
        return normalized;
    }

    public String reply(String senderEmail, String originalCode, String body) {
        senderEmail = normalizeEmail(senderEmail);
        Optional<Email> original = emailRepository.findByCode(originalCode);
        if (original.isEmpty() || !canAccessEmail(senderEmail, original.get())) {
            throw new IllegalArgumentException("You cannot reply to this email.");
        }

        Set<String> allRecipients = original.get().getRecipients().stream().map(Recipient::getRecipientEmail).collect(Collectors.toSet());
        allRecipients.add(original.get().getSender().getEmail());
        allRecipients.remove(senderEmail);

        String subject = "[Re] " + original.get().getSubject();
        return send(senderEmail, new ArrayList<>(allRecipients), subject, body);
    }

    public String forward(String senderEmail, String originalCode, List<String> newRecipients) {
        senderEmail = normalizeEmail(senderEmail);
        Optional<Email> original = emailRepository.findByCode(originalCode);
        if (original.isEmpty() || !canAccessEmail(senderEmail, original.get()))
            throw new IllegalArgumentException("You cannot forward this email.");

        String subject = "[Fw] " + original.get().getSubject();
        return send(senderEmail, newRecipients, subject, original.get().getBody());
    }

    public List<Email> getAllReceivedEmails(String userEmail) {
        return emailRepository.findInbox(normalizeEmail(userEmail));
    }

    public List<Email> getUnreadEmails(String userEmail) {
        return emailRepository.findUnread(normalizeEmail(userEmail));
    }

    public List<Email> getSentEmails(String userEmail) {
        return emailRepository.findSent(normalizeEmail(userEmail));
    }

    public Email readEmail(String userEmail ,String code) {
        userEmail = normalizeEmail(userEmail);
        Optional<Email> email = emailRepository.findByCode(code);
        if (email.isEmpty() || !canAccessEmail(userEmail, email.get())) {
            throw new IllegalArgumentException("You cannot read this email.");
        }
        emailRepository.markRecipientRead(email.get().getId(), userEmail);
        return email.get();
    }

    private boolean canAccessEmail(String userEmail, Email email) {
        if (userEmail == null || email == null)
            return false;

        boolean senderMatch = email.getSender() != null && userEmail.equals(email.getSender().getEmail());
        boolean recipientMatch = email.getRecipients() != null && email.getRecipients().stream().anyMatch(recipient -> userEmail.equals(recipient.getRecipientEmail()));
        return senderMatch || recipientMatch;
    }

    private String generateUniqueCode() {
        SecureRandom random = new SecureRandom();
        int maxAttempts = 100;
        for (int attempt = 0 ; attempt < maxAttempts ; attempt++) {
            StringBuilder sb = new StringBuilder(CODE_LENGTH);
            for (int i = 0; i < CODE_LENGTH; i++)
                sb.append(CHARS.charAt(random.nextInt(CHARS.length())));
            String code = sb.toString();
            if (emailRepository.findByCode(code).isEmpty())
                return code;
        }
        throw new RuntimeException("Failed to generate unique code after " + maxAttempts + " attempts.");
    }

    private String normalizeEmail(String email) {
        if(email == null)
            return null;
        String trimmed = email.trim().toLowerCase();
        return trimmed.contains("@") ? trimmed : trimmed + "@milou.com";
    }
}