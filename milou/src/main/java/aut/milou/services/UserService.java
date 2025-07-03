package aut.milou.services;

import aut.milou.model.User;

import java.util.ArrayList;
import java.util.List;

public class UserService {
    private final List<User> users = new ArrayList<>();

    public boolean register(String name, String email, String password) {
        email = normalizeEmail(email);

        if (findUserByEmail(email) != null) {
            System.out.println("This email is already taken. Please try again.");
            return false;
        }
        if (!Password.isValid(password)) {
            System.out.println("password must be at least 8 characters long.");
            return false;
        }

        User user = new User(name, email, password);
        users.add(user);
        System.out.println("Your new account is created.\nGo ahead and login!");
        return true;
    }

    public User login(String email, String password) {
        email = normalizeEmail(email);
        User user = findUserByEmail(email);

        if (user == null) {
            System.out.println("User not found.");
            return null;
        }
        if (!user.getPassword().equals(password)) {
            System.out.println("Invalid password.");
            return null;
        }

        return user;
    }

    private User findUserByEmail(String email) {
        for (User user : users) {
            if (user.getEmail().equalsIgnoreCase(email))
                return user;
        }
        return null;
    }

    private String normalizeEmail(String email) {
        return email.contains("@") ? email : email + "@milou.com";
    }
}
