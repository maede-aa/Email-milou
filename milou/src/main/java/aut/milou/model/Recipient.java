package aut.milou.model;

public class Recipient {
    private Long id;
    private String recipientEmail;
    private Email email;

    public Recipient() {}

    public Recipient(String recipientEmail ,Email email) {
        this.recipientEmail = recipientEmail;
        this.email = email;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getRecipientEmail() { return recipientEmail; }
    public void setRecipientEmail(String recipientEmail) { this.recipientEmail = recipientEmail; }
    public Email getEmail() { return email; }
    public void setEmail(Email email) { this.email = email; }

}
