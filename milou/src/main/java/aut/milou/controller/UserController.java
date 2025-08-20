package aut.milou.controller;

import aut.milou.model.User;
import aut.milou.services.UserService;

public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    public String signUp(String name ,String email ,String password) {
        if (name == null || name.trim().isEmpty())
            return "Name cannot be empty.";
        if (email == null || email.trim().isEmpty())
            return "Email cannot be empty.";
        if (password == null || password.isEmpty())
            return "Password cannot be empty.";

        try {
            userService.register(name ,email ,password);
            return "Your new account is created.\nGo ahead and login!";
        } catch (IllegalArgumentException e) {
            return e.getMessage();
        }
    }

    public User login(String email ,String password) {
        if (email == null || email.trim().isEmpty() || password == null || password.isEmpty())
            return null;
        return userService.login(email ,password).orElse(null);
    }
}