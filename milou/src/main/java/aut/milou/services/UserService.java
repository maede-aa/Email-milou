package aut.milou.services;

import aut.milou.model.User;
import aut.milou.Repository.UserRepository;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Optional;
import java.util.logging.Logger;

public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean register(String name ,String email ,String password) {
        email = normalizeEmail(email);
        if (userRepository.findByEmail(email).isPresent()) {
            return false;
        }
        if (!isPasswordValid(password)) {
            return false;
        }
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        User user = new User(name, email, hashedPassword);
        userRepository.save(user);
        return true;
    }

    public User login(String email ,String password) {
        email = normalizeEmail(email);
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty() || !BCrypt.checkpw(password, user.get().getPassword()))
            return null;

        return user.get();
    }

    public boolean isEmailTaken(String email) {
        return userRepository.findByEmail(normalizeEmail(email)).isPresent();
    }

    public boolean isPasswordValid(String password) {
        return password != null && password.length() >= 8;
    }

    private String normalizeEmail(String email) {
        if (email == null)
            return null;
        String trimmed = email.trim().toLowerCase();
        return trimmed.contains("@") ? trimmed : trimmed + "@milou.com";
    }
}