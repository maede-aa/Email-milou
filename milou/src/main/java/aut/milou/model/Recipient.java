package aut.milou.model;

import jakarta.persistence.*;

@Entity
@Table(name = "recipients")
public class Recipient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "recipient_email" ,nullable = false)
    private String recipientEmail;

    @ManyToOne
    @JoinColumn(name = "email_id" ,nullable = false)
    private Email email;

    @Column(name = "is_read")
    private boolean isRead = false;

    public Recipient() {}

    public Recipient(String recipientEmail ,Email email) {
        String normalized = normalizeEmail(recipientEmail);
        if (!isValidEmail(normalized))
            throw new IllegalArgumentException("Invalid email format: " + recipientEmail);

        this.recipientEmail = normalized;
        this.email = email;
        this.isRead = false;
    }

    private boolean isValidEmail(String email) {
        if (email == null) return false;
        return email.matches("^[\\w.-]+@milou\\.com$");
    }

    private String normalizeEmail(String email) {
        if (email == null)
            return null;
        String trimmed = email.trim().toLowerCase();
        return trimmed.contains("@") ? trimmed : trimmed + "@milou.com";
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getRecipientEmail() { return recipientEmail; }
    public void setRecipientEmail(String recipientEmail) { this.recipientEmail = recipientEmail; }
    public Email getEmail() { return email; }
    public void setEmail(Email email) { this.email = email; }
    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }
}