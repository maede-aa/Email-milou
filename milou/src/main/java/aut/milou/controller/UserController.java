package aut.milou.controller;

import aut.milou.model.User;
import aut.milou.services.UserService;

public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    public String signUp(String name ,String email ,String password) {
        if (name == null || name.isEmpty()) {
            return "Name cannot be empty.";
        }
        if (email == null || email.isEmpty()) {
            return "Email cannot be empty.";
        }
        if (password == null || password.isEmpty()) {
            return "Password cannot be empty.";
        }
        return userService.register(name ,email ,password) ? "Your new account is created.\nGo ahead and login!" : userService.isEmailTaken(email) ? "This email is already taken." : "Password must be at least 8 characters.";
    }

    public User login(String email ,String password) {
        if (email == null || email.isEmpty() || password == null || password.isEmpty())
            return null;

        return userService.login(email ,password);
    }
}

