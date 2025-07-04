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

    public Recipient() {}

    public Recipient(String recipientEmail ,Email email) {
        if (!isValidEmail(recipientEmail))
            throw new IllegalArgumentException("Invalid email format: " + recipientEmail);

        this.recipientEmail = recipientEmail;
        this.email = email;
    }

    private boolean isValidEmail(String email) {
        return email != null && email.matches("^[\\w-\\.]+@milou\\.com$");
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getRecipientEmail() { return recipientEmail; }
    public void setRecipientEmail(String recipientEmail) { this.recipientEmail = recipientEmail; }
    public Email getEmail() { return email; }
    public void setEmail(Email email) { this.email = email; }
}
