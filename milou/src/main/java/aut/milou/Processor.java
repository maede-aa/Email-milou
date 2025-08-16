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
        try {
            if (currentUser == null) {
                System.out.print("[L]ogin, [S]ign up : ");
                String cmd = scanner.nextLine().trim();
                if (cmd.equalsIgnoreCase("L") || cmd.equalsIgnoreCase("Login")) {
                    doLogin();
                } else if (cmd.equalsIgnoreCase("S") || cmd.equalsIgnoreCase("Sign up") || cmd.equalsIgnoreCase("Signup")) {
                    doSignUp();
                } else {
                    System.out.println("Invalid command. Please enter [L] for Login or [S] for Sign up.");
                }
            } else {
                System.out.print("[S]end, [V]iew, [R]eply, [F]orward, [L]ogout: ");
                String cmd = scanner.nextLine().trim();
                if (cmd.equalsIgnoreCase("S") || cmd.equalsIgnoreCase("Send")) {
                    doSend();
                } else if (cmd.equalsIgnoreCase("V") || cmd.equalsIgnoreCase("View")) {
                    doView();
                } else if (cmd.equalsIgnoreCase("R") || cmd.equalsIgnoreCase("Reply")) {
                    doReply();
                } else if (cmd.equalsIgnoreCase("F") || cmd.equalsIgnoreCase("Forward")) {
                    doForward();
                } else if (cmd.equalsIgnoreCase("L") || cmd.equalsIgnoreCase("Logout")) {
                    currentUser = null;
                    System.out.println("Logged out successfully.");
                } else {
                    System.out.println("invalid command. Please enter one of : [S]end, [V]iew, [R]eply, [F]orward, [L]ogout.");
                }
            }
        } catch (Exception ex) {
            System.out.println("an unexpected error occurred: " + ex.getMessage());
        }
    }}

    private void doSignup() {
        System.out.print("Name: ");
        String name = scn.nextLine();
        System.out.print("Email: ");
        String email = scn.nextLine();
        System.out.print("Password: ");
        String password = scn.nextLine();

        String result = userController.signUp(name ,email ,password);
        System.out.println(result);
    }

    private void doLogin() {
        System.out.print("Email: ");
        String email = scn.nextLine().trim();
        System.out.print("Password: ");
        String password = scn.nextLine();

        User user = userController.login(email, password);
        if (user == null) {
            System.out.println("Login failed. Invalid email or password.");
        } else {
            this.currentUser = user;
            System.out.println("welcome back , " + capitalize(user.getName()) + "!");
            showUnreadList();
        }
    }

    private void showUnreadEmails() {
        List<Email> unread = emailController.viewUnread(currentUser.getEmail());
        System.out.println("\nUnread Emails:");
        System.out.println(unread.size() + " unread emails:");
        for (Email e : unread) {
            String from = e.getSender() != null ? e.getSender().getEmail() : "unknown";
            System.out.println("+ " + from + " - " + e.getSubject() + " (" + e.getCode() + ")");
        }
        System.out.println()
    }

    private void doSend() {
        System.out.print("Recipient(s): ");
        String r = scanner.nextLine();
        List<String> recipients = Arrays.stream(r.split(",")).map(String::trim).filter(s -> !s.isEmpty()).collect(Collectors.toList());
        f (recipients.isEmpty()) {
            System.out.println("Please enter at least one recipient.");
            return;
        }
        System.out.print("Subject: ");
        String subject = scanner.nextLine();
        System.out.print("Body: ");
        String body = scanner.nextLine();
        try {
            String code = emailController.sendEmail(currentUser.getEmail(), recipients, subject, body);
            System.out.println("Successfully sent your email.\nCode: " + code);
        } catch (Exception ex) {
            System.out.println("Failed to send email: " + ex.getMessage());
        }
    }

    private void doView() {
        System.out.print("[A]ll emails, [U]nread emails, [S]ent emails, Read by [C]ode: ");
        String cmd = scanner.nextLine().trim();
        if (cmd.equalsIgnoreCase("A")) {
            List<Email> all = emailController.viewAll(currentUser.getEmail());
            System.out.println("All Emails:");
            printEmailList(all, false);
        } else if (cmd.equalsIgnoreCase("U")) {
            List<Email> unread = emailController.viewUnread(currentUser.getEmail());
            System.out.println("Unread Emails:");
            printEmailList(unread, false);
        } else if (cmd.equalsIgnoreCase("S")) {
            List<Email> sent = emailController.viewSent(currentUser.getEmail());
            System.out.println("Sent Emails:");
            printEmailList(sent, true);
        } else if (cmd.equalsIgnoreCase("C")) {
            System.out.print("Code: ");
            String code = scanner.nextLine().trim();
            if (code.isEmpty()) {
                System.out.println("Code cannot be empty.");
                return;
            }
            try {
                Email email = emailController.readEmail(currentUser.getEmail(), code);
                printFullEmail(email);
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        } else {
            System.out.println("Invalid option. Please enter A, U, S, or C.");
        }
    }

    private void doReply() {
        System.out.print("Code: ");
        String code = scanner.nextLine().trim();
        if (code.isEmpty()) {
            System.out.println("Code cannot be empty.");
            return;
        }
        System.out.print("Body: ");
        String body = scanner.nextLine();
        if (body.isEmpty()) {
            System.out.println("Body cannot be empty.");
            return;
        }
        try {
            String newCode = emailController.reply(currentUser.getEmail(), code, body);
            System.out.println("Successfully sent your reply to email " + code + ".\nCode: " + newCode);
        } catch (Exception ex) {
            System.out.println("Failed to reply: " + ex.getMessage());
        }
    }

    private void doForward() {
        System.out.print("Code: ");
        String code = scanner.nextLine().trim();
        if (code.isEmpty()) {
            System.out.println("Code cannot be empty.");
            return;
        }
        System.out.print("Recipient(s): ");
        String r = scanner.nextLine();
        List<String> recipients = Arrays.stream(r.split(",")) .map(String::trim).filter(s -> !s.isEmpty()).collect(Collectors.toList());
        if (recipients.isEmpty()) {
            System.out.println("Please enter at least one recipient.");
            return;
        }
        try {
            String newCode = emailController.forward(currentUser.getEmail(), code, recipients);
            System.out.println("Successfully forwarded your email.\nCode: " + newCode);
        } catch (Exception ex) {
            System.out.println("Failed to forward: " + ex.getMessage());
        }
    }

    private void printEmailList(List<Email> list, boolean isSent) {
        if (list.isEmpty()) {
            System.out.println("No emails.");
            return;
        }
        for (Email e : list) {
            if (isSent) {
                String to = String.join(", ", e.getRecipients().stream().map(Recipient::getRecipientEmail).toList());
                System.out.println("+ " + to + " - " + e.getSubject() + " (" + e.getCode() + ")");
            } else {
                String from = e.getSender() != null ? e.getSender().getEmail() : "unknown";
                System.out.println("+ " + from + " - " + e.getSubject() + " (" + e.getCode() + ")");
            }
        }
        System.out.println();
    }

    private void printFullEmail(Email e) {
        System.out.println("Code: " + e.getCode());
        String recipients = e.getRecipients().stream().map(Recipient::getRecipientEmail) .collect(Collectors.joining(", "));
        System.out.println("Recipient(s): " + recipients);
        System.out.println("Subject: " + e.getSubject());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        System.out.println("Date: " + sdf.format(e.getDate()));
        System.out.println();
        System.out.println(e.getBody());
        System.out.println();
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty())
            return "";
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }
}