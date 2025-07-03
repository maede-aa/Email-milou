package aut.milou.controller;

import aut.milou.model.User;
import aut.milou.services.UserService;

public class UserController {
    private final UserService userService = new UserService();

    public String signUp(String name ,String email ,String password) {
        if (userService.isEmailTaken(email)) {
            return "This email is already taken.";
        }
        if (!userService.isPasswordValid(password)) {
            return "Password must be at least 8 characters.";
        }
        userService.register(name, email, password);
        return "Your new account is created.\ngo ahead and login!";
    }

    public User login(String email, String password) {
        return userService.login(email, password);
    }
}

