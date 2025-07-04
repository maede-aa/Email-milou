package aut.milou;

import aut.milou.controller.EmailController;
import aut.milou.controller.UserController;
import aut.milou.model.Email;
import aut.milou.model.Recipient;
import aut.milou.model.User;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class Processor {
    private static final Logger logger = Logger.getLogger(Processor.class.getName());
    private final Scanner scn = new Scanner(System.in);
    private final UserController userController;
    private final EmailController emailController;
    private User currentUser = null;

    public Processor(UserController userController ,EmailController emailController) {
        this.userController = userController;
        this.emailController = emailController;
    }

    public void start() {
        while (true) {
            if (currentUser == null) {
                System.out.print("[L]ogin ,[S]ign up: ");
                String input = scn.nextLine().trim().toLowerCase();
                try {
                    if (input.equals("l") || input.equals("login")) {
                        handleLogin();
                    } else if (input.equals("s") || input.equals("sign up")) {
                        handleSignup();
                    } else {
                        System.out.println("Invalid command.");
                    }
                } catch (Exception e) {
                    logger.severe("Error in start: " + e.getMessage());
                    System.out.println("An error occurred: " + e.getMessage());
                }
            } else {
                handleMainMenu();
            }
        }
    }

    private void handleSignup() {
        System.out.print("Name: ");
        String name = scn.nextLine().trim();
        System.out.print("Email: ");
        String email = scn.nextLine().trim();
        System.out.print("Password: ");
        String password = scn.nextLine().trim();

        try {
            String result = userController.signUp(name ,email ,password);
            System.out.println(result);
        } catch (Exception e) {
            logger.severe("Signup error: " + e.getMessage());
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void handleLogin() {
        System.out.print("Email: ");
        String email = scn.nextLine().trim();
        System.out.print("Password: ");
        String password = scn.nextLine().trim();

        try {
            User user = userController.login(email ,password);
            if (user != null) {
                currentUser = user;
                System.out.println("Welcome back ," + user.getName() + "!");
                showUnreadEmails();
            } else {
                System.out.println("Invalid credentials.");
            }
        } catch (Exception e) {
            logger.severe("Login error: " + e.getMessage());
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void showUnreadEmails() {
        try {
            List<Email> unread = emailController.viewUnread(currentUser.getEmail());
            if (unread.isEmpty()) {
                System.out.println("No unread emails.");
                return;
            }
            System.out.println("Unread Emails:");
            printEmails(unread);
        } catch (Exception e) {
            logger.severe("Error showing unread emails: " + e.getMessage());
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void handleMainMenu() {
        System.out.print("[S]end ,[V]iew ,[R]eply ,[F]orward ,[L]ogout: ");
        String command = scn.nextLine().trim().toLowerCase();

        try {
            switch (command) {
                case "s":
                case "send":
                    handleSend();
                    break;
                case "v":
                case "view":
                    handleView();
                    break;
                case "r":
                case "reply":
                    handleReply();
                    break;
                case "f":
                case "forward":
                    handleForward();
                    break;
                case "l":
                case "logout":
                    currentUser = null;
                    System.out.println("Logged out successfully.");
                    break;
                default:
                    System.out.println("Invalid command.");
            }
        } catch (Exception e) {
            logger.severe("Error in main menu: " + e.getMessage());
            System.out.println("Error: " + e.getMessage());
        }
    }

    private String readMultiLineBody() {
        System.out.println("Body (end with a single . on a new line):");
        StringBuilder body = new StringBuilder();
        String line;
        while (!(line = scn.nextLine()).equals(".")) {
            body.append(line).append("\n");
        }
        return body.toString().trim();
    }

    private void handleSend() {
        System.out.print("Recipient(s): ");
        String recipientInput = scn.nextLine().trim();
        List<String> recipients = Arrays.stream(recipientInput.split(",")).map(String::trim).filter(s -> !s.isEmpty()).collect(Collectors.toList());

        System.out.print("Subject: ");
        String subject = scn.nextLine().trim();

        String body = readMultiLineBody();

        try {
            String code = emailController.sendEmail(currentUser.getEmail() ,recipients ,subject ,body);
            System.out.println("Successfully sent your email.");
            System.out.println("Code: " + code);
        } catch (Exception e) {
            logger.severe("Send error: " + e.getMessage());
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void handleView() {
        System.out.print("[A]ll emails ,[U]nread emails ,[S]ent emails ,Read by [C]ode: ");
        String option = scn.nextLine().trim().toLowerCase();

        try {
            switch (option) {
                case "a":
                case "all":
                    printEmails(emailController.viewAll(currentUser.getEmail()));
                    break;
                case "u":
                case "unread":
                    printEmails(emailController.viewUnread(currentUser.getEmail()));
                    break;
                case "s":
                case "sent":
                    printEmails(emailController.viewSent(currentUser.getEmail()));
                    break;
                case "c":
                case "code":
                    handleReadByCode();
                    break;
                default:
                    System.out.println("Invalid view option.");
            }
        } catch (Exception e) {
            logger.severe("view error: " + e.getMessage());
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void handleReadByCode() {
        System.out.print("Code: ");
        String code = scn.nextLine().trim();
        try {
            Email email = emailController.readEmail(currentUser.getEmail() ,code);
            if (email != null) {
                String recipients = email.getRecipients().stream()
                        .map(Recipient::getRecipientEmail)
                        .collect(Collectors.joining(" ,"));
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                System.out.println("code: " + email.getCode());
                System.out.println("recipients: " + recipients);
                System.out.println("subject: " + email.getSubject());
                System.out.println("Date: " + sdf.format(email.getDate()));
                System.out.println();
                System.out.println(email.getBody());
            }
        } catch (Exception e) {
            logger.severe("Read error: " + e.getMessage());
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void handleReply() {
        System.out.print("Code: ");
        String code = scn.nextLine().trim();
        String body = readMultiLineBody();

        try {
            String replyCode = emailController.reply(currentUser.getEmail() ,code ,body);
            System.out.println("successfully sent your reply to email " + code );
            System.out.println("Code: " + replyCode);
        } catch (Exception e) {
            logger.severe("Reply error: " + e.getMessage());
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void handleForward() {
        System.out.print("Code: ");
        String code = scn.nextLine().trim();
        System.out.print("Recipients: ");
        String recipientInput = scn.nextLine().trim();
        List<String> recipients = Arrays.stream(recipientInput.split(",")).map(String::trim).filter(s -> !s.isEmpty()).collect(Collectors.toList());

        try {
            String forwardCode = emailController.forward(currentUser.getEmail() ,code ,recipients);
            System.out.println("Successfully forwarded your email.");
            System.out.println("Code: " + forwardCode);
        } catch (Exception e) {
            logger.severe("Forward error: " + e.getMessage());
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void printEmails(List<Email> emails) {
        if (emails.isEmpty()) {
            System.out.println("No emails found.");
            return;
        }

        for (Email email : emails) {
            String prefix = email.getSender().getEmail().equals(currentUser.getEmail()) ? email.getRecipients().stream().map(Recipient::getRecipientEmail).collect(Collectors.joining(" ,")) : email.getSender().getEmail();
            System.out.println("+ " + prefix + " - " + email.getSubject() + " (" + email.getCode() + ")");
        }
    }
}