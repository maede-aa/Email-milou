package aut.milou.model;

import jakarta.persistence.*;
import java.util.*;

@Entity
@Table(name = "emails")
public class Email {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false ,unique = true ,length = 6)
    private String code;

    @ManyToOne
    @JoinColumn(name = "sender_id" ,nullable = false)
    private User sender;

    @OneToMany(mappedBy = "email" ,cascade = CascadeType.ALL ,orphanRemoval = true)
    private List<Recipient> recipients;

    @Column(length = 100)
    private String subject;

    @Column(columnDefinition = "TEXT")
    private String body;

    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    public Email() {}

    public Email(String code ,User sender ,List<Recipient> recipients ,String subject ,String body ,Date date ,boolean isRead) {
        if (subject != null && subject.length() > 100) {
            throw new IllegalArgumentException("Subject must be under 100 characters.");
        }
        this.code = code;
        this.sender = sender;
        this.recipients = recipients;
        this.subject = subject;
        this.body = body;
        this.date = date != null ? date : new Date();
    }

    public Long getId() { return id; }
    public String getCode() { return code; }
    public User getSender() { return sender; }
    public List<Recipient> getRecipients() { return recipients; }
    public String getSubject() { return subject; }
    public String getBody() { return body; }
    public Date getDate() { return date; }
}
