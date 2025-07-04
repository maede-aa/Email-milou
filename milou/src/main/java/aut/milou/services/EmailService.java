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
        senderEmail = normalizeEmail(senderEmail);
        Optional<User> sender = userRepository.findByEmail(senderEmail);
        if (sender.isEmpty()) {
            logger.warning("Sender not found: " + senderEmail);
            throw new IllegalArgumentException("Sender not found.");
        }

        List<String> normalizedRecipients = recipients.stream().map(this::normalizeEmail).filter(recipient -> userRepository.findByEmail(recipient).isPresent()).collect(Collectors.toList());

        if (normalizedRecipients.isEmpty()) {
            logger.warning("No valid recipients for sender: " + senderEmail);
            throw new IllegalArgumentException("No valid recipients.");
        }

        String code = generateUniqueCode();
        List<Recipient> recipientEntities = normalizedRecipients.stream().map(email -> new Recipient(email, null)).collect(Collectors.toList());

        Email email = new Email(code, sender.get(), recipientEntities, subject, body, new Date(), false);
        recipientEntities.forEach(recipient -> recipient.setEmail(email));
        emailRepository.save(email);
        logger.info("Email sent with code: " + code + " by " + senderEmail);
        return code;
    }

    public String reply(String senderEmail, String originalCode, String body) {
        senderEmail = normalizeEmail(senderEmail);
        Optional<Email> original = emailRepository.findByCode(originalCode);
        if (original.isEmpty() || !canAccessEmail(senderEmail, original.get())) {
            logger.warning("Unauthorized reply attempt by " + senderEmail + " for code: " + originalCode);
            throw new IllegalArgumentException("You cannot reply to this email.");
        }

        Set<String> allRecipients = new HashSet<>(original.get().getRecipients().stream().map(Recipient::getRecipientEmail).collect(Collectors.toList()));
        allRecipients.add(original.get().getSender().getEmail());
        allRecipients.remove(senderEmail);

        String subject = "[Re] " + original.get().getSubject();
        String replyCode = send(senderEmail, new ArrayList<>(allRecipients), subject, body);
        logger.info("Reply sent with code: " + replyCode + " for original code: " + originalCode);
        return replyCode;
    }

    public String forward(String senderEmail, String originalCode, List<String> newRecipients) {
        senderEmail = normalizeEmail(senderEmail);
        Optional<Email> original = emailRepository.findByCode(originalCode);
        if (original.isEmpty() || !canAccessEmail(senderEmail, original.get())) {
            logger.warning("Unauthorized forward attempt by " + senderEmail + " for code: " + originalCode);
            throw new IllegalArgumentException("You cannot forward this email.");
        }

        String subject = "[Fw] " + original.get().getSubject();
        String forwardCode = send(senderEmail, newRecipients, subject, original.get().getBody());
        logger.info("Email forwarded with code: " + forwardCode + " for original code: " + originalCode);
        return forwardCode;
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

    public Email readEmail(String userEmail, String code) {
        userEmail = normalizeEmail(userEmail);
        Optional<Email> email = emailRepository.findByCode(code);
        if (email.isEmpty() || !canAccessEmail(userEmail, email.get())) {
            logger.warning("Unauthorized read attempt by " + userEmail + " for code: " + code);
            throw new IllegalArgumentException("You cannot read this email.");
        }
        email.get().setIsRead(true);
        emailRepository.update(email.get());
        logger.info("Email read with code: " + code + " by " + userEmail);
        return email.get();
    }

    private boolean canAccessEmail(String userEmail, Email email) {
        return email.getSender().getEmail().equals(userEmail) || email.getRecipients().stream().anyMatch(recipient -> recipient.getRecipientEmail().equals(userEmail));
    }

    private String generateUniqueCode() {
        SecureRandom random = new SecureRandom();
        StringBuilder code = new StringBuilder();
        do {
            code.setLength(0);
            for (int i = 0; i < CODE_LENGTH; i++) {
                code.append(CHARS.charAt(random.nextInt(CHARS.length())));
            }
        } while (emailRepository.findByCode(code.toString()).isPresent());
        return code.toString();
    }

    private String normalizeEmail(String email) {
        return email.contains("@") ? email : email + "@milou.com";
    }
}