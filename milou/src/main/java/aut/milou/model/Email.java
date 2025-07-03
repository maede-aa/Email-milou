package aut.milou.model;

import java.util.Date;
import java.util.List;

public class Email {
    private final String code;
    private final String sender;
    private final List<String> recipients;
    private final String subject;
    private final String body;
    private final Date date;
    private boolean isRead;

    public Email(String code ,String sender ,List<String> recipients ,String subject ,String body ,Date date ,boolean isRead) {
        this.code = code;
        this.sender = sender;
        this.recipients = recipients;
        this.subject = subject;
        this.body = body;
        this.date = date;
        this.isRead = isRead;
    }

    public String getCode() { return code; }
    public String getSender() { return sender; }
    public List<String> getRecipients() { return recipients; }
    public String getSubject() { return subject; }
    public String getBody() { return body; }
    public Date getDate() { return date; }
    public boolean isRead() { return isRead; }
    public void setIsRead(boolean read) { isRead = read; }
}
