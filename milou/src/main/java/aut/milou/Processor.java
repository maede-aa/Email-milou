package aut.milou;

import aut.milou.controller.EmailController;
import aut.milou.controller.UserController;
import aut.milou.model.Email;
import aut.milou.model.Recipient;
import aut.milou.model.User;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class Processor {
    private final UserController userController;
    private final EmailController emailController;
    private User currentUser = null;

    private JFrame mainFrame;
    private JPanel loginPanel;
    private JPanel signupPanel;
    private JPanel userPanel;
    private JList<String> unreadEmailsList;
    private DefaultListModel<String> unreadModel;

    private static final Color SKY_BLUE = new Color(135 ,206 ,250);
    private static final Color BUTTON_BLUE = new Color(70 ,130 ,180);
    private static final Color TEXT_RED = new Color(255 ,99 ,71);
    private static final Font MAIN_FONT = new Font("Segoe UI" ,Font.PLAIN ,14);
    private static final Font LIST_FONT = new Font("Segoe UI" ,Font.PLAIN ,12);
    private static final Border PANEL_BORDER = BorderFactory.createEmptyBorder(10 ,10 ,10 ,10);
    private static final Border BUTTON_BORDER = BorderFactory.createLineBorder(BUTTON_BLUE.darker() ,1 ,true);

    public Processor(UserController userController ,EmailController emailController) {
        this.userController = userController;
        this.emailController = emailController;
    }

    public void start() {
        SwingUtilities.invokeLater(() -> {
            mainFrame = new JFrame("Milou Email System");
            mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            mainFrame.setSize(600 ,400);
            mainFrame.setLayout(new CardLayout());
            mainFrame.setLocationRelativeTo(null);

            createLoginPanel();
            createSignupPanel();
            createUserPanel();

            mainFrame.add(loginPanel ,"Login");
            mainFrame.add(signupPanel ,"Signup");
            mainFrame.add(userPanel ,"User");

            showPanel("Login");
            mainFrame.setVisible(true);
        });
    }

    private void createLoginPanel() {
        loginPanel = new JPanel(new GridLayout(4 ,2 ,10 ,10));
        loginPanel.setBackground(SKY_BLUE);
        loginPanel.setBorder(PANEL_BORDER);

        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(MAIN_FONT);
        JTextField emailField = new JTextField();
        emailField.setFont(MAIN_FONT);
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(MAIN_FONT);
        JPasswordField passwordField = new JPasswordField();
        passwordField.setFont(MAIN_FONT);
        JButton loginButton = createStyledButton("Login");
        JButton signupButton = createStyledButton("Sign up");

        loginButton.addActionListener(e -> doLogin(emailField.getText() ,new String(passwordField.getPassword())));
        signupButton.addActionListener(e -> showPanel("Signup"));

        loginPanel.add(emailLabel);
        loginPanel.add(emailField);
        loginPanel.add(passwordLabel);
        loginPanel.add(passwordField);
        loginPanel.add(new JLabel());
        loginPanel.add(loginButton);
        loginPanel.add(new JLabel());
        loginPanel.add(signupButton);
    }

    private void createSignupPanel() {
        signupPanel = new JPanel(new GridLayout(5 ,2 ,10 ,10));
        signupPanel.setBackground(SKY_BLUE);
        signupPanel.setBorder(PANEL_BORDER);

        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setFont(MAIN_FONT);
        JTextField nameField = new JTextField();
        nameField.setFont(MAIN_FONT);
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(MAIN_FONT);
        JTextField emailField = new JTextField();
        emailField.setFont(MAIN_FONT);
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(MAIN_FONT);
        JPasswordField passwordField = new JPasswordField();
        passwordField.setFont(MAIN_FONT);
        JButton signupButton = createStyledButton("Sign up");
        JButton backButton = createStyledButton("Back to Login");

        signupButton.addActionListener(e -> doSignup(nameField.getText() ,emailField.getText() ,new String(passwordField.getPassword())));
        backButton.addActionListener(e -> showPanel("Login"));

        signupPanel.add(nameLabel);
        signupPanel.add(nameField);
        signupPanel.add(emailLabel);
        signupPanel.add(emailField);
        signupPanel.add(passwordLabel);
        signupPanel.add(passwordField);
        signupPanel.add(new JLabel());
        signupPanel.add(signupButton);
        signupPanel.add(new JLabel());
        signupPanel.add(backButton);
    }

    private void createUserPanel() {
        userPanel = new JPanel(new BorderLayout(10 ,10));
        userPanel.setBackground(SKY_BLUE);
        userPanel.setBorder(PANEL_BORDER);

        JLabel welcomeLabel = new JLabel("Welcome back!");
        welcomeLabel.setFont(new Font("Segoe UI" ,Font.BOLD ,16));
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);

        unreadModel = new DefaultListModel<>();
        unreadEmailsList = new JList<>(unreadModel);
        unreadEmailsList.setFont(LIST_FONT);
        unreadEmailsList.setBackground(Color.WHITE);
        JScrollPane scrollPane = new JScrollPane(unreadEmailsList);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.GRAY ,1));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER ,10 ,5));
        buttonPanel.setBackground(SKY_BLUE);
        JButton sendButton = createStyledButton("Send");
        JButton viewButton = createStyledButton("View");
        JButton replyButton = createStyledButton("Reply");
        JButton forwardButton = createStyledButton("Forward");
        JButton logoutButton = createStyledButton("Logout");

        sendButton.addActionListener(e -> showSendDialog());
        viewButton.addActionListener(e -> showViewDialog());
        replyButton.addActionListener(e -> showReplyDialog());
        forwardButton.addActionListener(e -> showForwardDialog());
        logoutButton.addActionListener(e -> {
            currentUser = null;
            showPanel("Login");
        });

        buttonPanel.add(sendButton);
        buttonPanel.add(viewButton);
        buttonPanel.add(replyButton);
        buttonPanel.add(forwardButton);
        buttonPanel.add(logoutButton);

        userPanel.add(welcomeLabel ,BorderLayout.NORTH);
        userPanel.add(scrollPane ,BorderLayout.CENTER);
        userPanel.add(buttonPanel ,BorderLayout.SOUTH);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(MAIN_FONT);
        button.setBackground(BUTTON_BLUE);
        button.setForeground(Color.WHITE);
        button.setBorder(BUTTON_BORDER);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(100 ,30));
        return button;
    }

    private void doLogin(String email ,String password) {
        User user = userController.login(email ,password);
        if (user == null) {
            showErrorMessage("Login failed. Invalid email or password.");
        } else {
            currentUser = user;
            ((JLabel) userPanel.getComponent(0)).setText("Welcome back ," + capitalize(user.getName()) + "!");
            refreshUnreadEmails();
            showPanel("User");
        }
    }

    private void doSignup(String name ,String email ,String password) {
        String result = userController.signUp(name ,email ,password);
        JOptionPane.showMessageDialog(mainFrame ,result ,"Signup" ,result.startsWith("Your new account") ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
        if (result.startsWith("Your new account")) {
            showPanel("Login");
        }
    }

    private void refreshUnreadEmails() {
        unreadModel.clear();
        List<Email> unread = emailController.viewUnread(currentUser.getEmail());
        for (Email e : unread) {
            String from = e.getSender() != null ? e.getSender().getEmail() : "unknown";
            unreadModel.addElement("+ " + from + " - " + e.getSubject() + " (" + e.getCode() + ")");
        }
    }

    private void showSendDialog() {
        JDialog dialog = new JDialog(mainFrame ,"Send Email" ,true);
        dialog.setSize(400 ,300);
        dialog.setLayout(new GridLayout(4 ,2 ,10 ,10));
        dialog.setBackground(SKY_BLUE);
        dialog.getContentPane().setBackground(SKY_BLUE);
        dialog.setLocationRelativeTo(mainFrame);

        JLabel recipientLabel = new JLabel("Recipient(s):");
        recipientLabel.setFont(MAIN_FONT);
        JTextField recipientField = new JTextField();
        recipientField.setFont(MAIN_FONT);
        JLabel subjectLabel = new JLabel("Subject:");
        subjectLabel.setFont(MAIN_FONT);
        JTextField subjectField = new JTextField();
        subjectField.setFont(MAIN_FONT);
        JLabel bodyLabel = new JLabel("Body:");
        bodyLabel.setFont(MAIN_FONT);
        JTextArea bodyArea = new JTextArea();
        bodyArea.setFont(LIST_FONT);
        bodyArea.setLineWrap(true);
        bodyArea.setWrapStyleWord(true);
        JButton sendButton = createStyledButton("Send");

        sendButton.addActionListener(e -> {
            List<String> recipients = Arrays.stream(recipientField.getText().split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList());
            String subject = subjectField.getText();
            String body = bodyArea.getText();
            try {
                if (recipients.isEmpty()) throw new Exception("Please enter at least one recipient.");
                if (subject.length() > 100) throw new Exception("Subject is too long. Maximum is 100 characters.");
                String code = emailController.sendEmail(currentUser.getEmail() ,recipients ,subject ,body);
                JOptionPane.showMessageDialog(dialog ,"Successfully sent your email.\nCode: " + code ,"Success" ,JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
            } catch (Exception ex) {
                showErrorMessage("Failed to send: " + ex.getMessage());
            }
        });

        dialog.add(recipientLabel);
        dialog.add(recipientField);
        dialog.add(subjectLabel);
        dialog.add(subjectField);
        dialog.add(bodyLabel);
        dialog.add(new JScrollPane(bodyArea));
        dialog.add(new JLabel());
        dialog.add(sendButton);
        dialog.setVisible(true);
    }

    private void showViewDialog() {
        String[] options = {"All emails" ,"Unread emails" ,"Sent emails" ,"Read by Code"};
        JComboBox<String> viewOptions = new JComboBox<>(options);
        viewOptions.setFont(MAIN_FONT);
        int result = JOptionPane.showConfirmDialog(mainFrame ,viewOptions ,"View Emails" ,JOptionPane.OK_CANCEL_OPTION ,JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) return;

        String choice = (String) viewOptions.getSelectedItem();
        if (choice.equals("Read by Code")) {
            JTextField codeField = new JTextField();
            codeField.setFont(MAIN_FONT);
            int codeResult = JOptionPane.showConfirmDialog(mainFrame ,codeField ,"Enter Code:" ,JOptionPane.OK_CANCEL_OPTION ,JOptionPane.PLAIN_MESSAGE);
            if (codeResult == JOptionPane.OK_OPTION && !codeField.getText().isEmpty()) {
                try {
                    Email email = emailController.readEmail(currentUser.getEmail() ,codeField.getText());
                    showFullEmailDialog(email);
                    refreshUnreadEmails();
                } catch (Exception ex) {
                    showErrorMessage(ex.getMessage());
                }
            }
            return;
        }

        List<Email> emails = null;
        boolean isSent = false;
        switch (choice) {
            case "All emails":
                emails = emailController.viewAll(currentUser.getEmail());
                break;
            case "Unread emails":
                emails = emailController.viewUnread(currentUser.getEmail());
                break;
            case "Sent emails":
                emails = emailController.viewSent(currentUser.getEmail());
                isSent = true;
                break;
        }

        if (emails != null) {
            StringBuilder sb = new StringBuilder(choice + ":\n");
            if (emails.isEmpty()) {
                sb.append("No emails.");
            } else {
                for (Email e : emails) {
                    if (isSent) {
                        String to = e.getRecipients().stream().map(Recipient::getRecipientEmail).collect(Collectors.joining(" ,"));
                        sb.append("+ ").append(to).append(" - ").append(e.getSubject()).append(" (").append(e.getCode()).append(")\n");
                    } else {
                        String from = e.getSender() != null ? e.getSender().getEmail() : "unknown";
                        sb.append("+ ").append(from).append(" - ").append(e.getSubject()).append(" (").append(e.getCode()).append(")\n");
                    }
                }
            }
            JTextArea textArea = new JTextArea(sb.toString());
            textArea.setFont(LIST_FONT);
            textArea.setEditable(false);
            textArea.setBackground(Color.WHITE);
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(500 ,300));
            scrollPane.setBorder(BorderFactory.createLineBorder(Color.GRAY ,1));
            JOptionPane.showMessageDialog(mainFrame ,scrollPane ,choice ,JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void showReplyDialog() {
        JDialog dialog = new JDialog(mainFrame ,"Reply to Email" ,true);
        dialog.setSize(400 ,300);
        dialog.setLayout(new GridLayout(3 ,2 ,10 ,10));
        dialog.setBackground(SKY_BLUE);
        dialog.getContentPane().setBackground(SKY_BLUE);
        dialog.setLocationRelativeTo(mainFrame);

        JLabel codeLabel = new JLabel("Code:");
        codeLabel.setFont(MAIN_FONT);
        JTextField codeField = new JTextField();
        codeField.setFont(MAIN_FONT);
        JLabel bodyLabel = new JLabel("Body:");
        bodyLabel.setFont(MAIN_FONT);
        JTextArea bodyArea = new JTextArea();
        bodyArea.setFont(LIST_FONT);
        bodyArea.setLineWrap(true);
        bodyArea.setWrapStyleWord(true);
        JButton replyButton = createStyledButton("Reply");

        replyButton.addActionListener(e -> {
            String code = codeField.getText();
            String body = bodyArea.getText();
            try {
                if (code.isEmpty() || body.isEmpty()) throw new Exception("Code and Body cannot be empty.");
                String newCode = emailController.reply(currentUser.getEmail() ,code ,body);
                JOptionPane.showMessageDialog(dialog ,"Successfully sent your reply to email " + code + ".\nCode: " + newCode ,"Success" ,JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
            } catch (Exception ex) {
                showErrorMessage("Failed to reply: " + ex.getMessage());
            }
        });

        dialog.add(codeLabel);
        dialog.add(codeField);
        dialog.add(bodyLabel);
        dialog.add(new JScrollPane(bodyArea));
        dialog.add(new JLabel());
        dialog.add(replyButton);
        dialog.setVisible(true);
    }

    private void showForwardDialog() {
        JDialog dialog = new JDialog(mainFrame ,"Forward Email" ,true);
        dialog.setSize(400 ,200);
        dialog.setLayout(new GridLayout(3 ,2 ,10 ,10));
        dialog.setBackground(SKY_BLUE);
        dialog.getContentPane().setBackground(SKY_BLUE);
        dialog.setLocationRelativeTo(mainFrame);

        JLabel codeLabel = new JLabel("Code:");
        codeLabel.setFont(MAIN_FONT);
        JTextField codeField = new JTextField();
        codeField.setFont(MAIN_FONT);
        JLabel recipientLabel = new JLabel("Recipient(s):");
        recipientLabel.setFont(MAIN_FONT);
        JTextField recipientField = new JTextField();
        recipientField.setFont(MAIN_FONT);
        JButton forwardButton = createStyledButton("Forward");

        forwardButton.addActionListener(e -> {
            String code = codeField.getText();
            List<String> recipients = Arrays.stream(recipientField.getText().split(",")).map(String::trim).filter(s -> !s.isEmpty()).collect(Collectors.toList());
            try {
                if (code.isEmpty() || recipients.isEmpty()) throw new Exception("Code and Recipient(s) cannot be empty.");
                String newCode = emailController.forward(currentUser.getEmail() ,code ,recipients);
                JOptionPane.showMessageDialog(dialog ,"Successfully forwarded your email.\nCode: " + newCode ,"Success" ,JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
            } catch (Exception ex) {
                showErrorMessage("Failed to forward: " + ex.getMessage());
            }
        });

        dialog.add(codeLabel);
        dialog.add(codeField);
        dialog.add(recipientLabel);
        dialog.add(recipientField);
        dialog.add(new JLabel());
        dialog.add(forwardButton);
        dialog.setVisible(true);
    }

    private void showFullEmailDialog(Email e) {
        StringBuilder sb = new StringBuilder();
        sb.append("Code: ").append(e.getCode()).append("\n");
        String recipients = e.getRecipients().stream().map(Recipient::getRecipientEmail).collect(Collectors.joining(" ,"));
        sb.append("Recipient(s): ").append(recipients).append("\n");
        sb.append("Subject: ").append(e.getSubject()).append("\n");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sb.append("Date: ").append(sdf.format(e.getDate())).append("\n\n");
        sb.append(e.getBody());

        JTextArea textArea = new JTextArea(sb.toString());
        textArea.setFont(LIST_FONT);
        textArea.setEditable(false);
        textArea.setBackground(Color.WHITE);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(500 ,300));
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.GRAY ,1));
        JOptionPane.showMessageDialog(mainFrame ,scrollPane ,"Email Details" ,JOptionPane.INFORMATION_MESSAGE);
    }

    private void showErrorMessage(String message) {
        JLabel label = new JLabel(message);
        label.setFont(MAIN_FONT);
        label.setForeground(TEXT_RED);
        JOptionPane.showMessageDialog(mainFrame ,label ,"Error" ,JOptionPane.ERROR_MESSAGE);
    }

    private void showPanel(String panelName) {
        CardLayout cl = (CardLayout) mainFrame.getContentPane().getLayout();
        cl.show(mainFrame.getContentPane() ,panelName);
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return "";
        return s.substring(0 ,1).toUpperCase() + s.substring(1);
    }
}