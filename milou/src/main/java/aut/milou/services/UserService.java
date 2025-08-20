package aut.milou.services;

import aut.milou.model.User;
import aut.milou.Repository.UserRepository;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Optional;
import java.util.regex.Pattern;

public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void register(String name, String email, String password) {
        email = normalizeEmail(email);
        if (!isValidEmail(email)) {
            throw new IllegalArgumentException("Invalid email.");
        }
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("This email is already taken.");
        }
        if (password == null || password.length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters.");
        }
        String hashed = BCrypt.hashpw(password, BCrypt.gensalt());
        User user = new User(name, email, hashed);
        userRepository.save(user);
    }

    public Optional<User> login(String email, String password) {
        email = normalizeEmail(email);
        return userRepository.findByEmail(email)
                .filter(u -> BCrypt.checkpw(password, u.getPassword()));
    }

    private boolean isValidEmail(String email) {
        if (email == null) return false;
        return email.matches("^[\\w.-]+@milou\\.com$");
    }

    private String normalizeEmail(String email) {
        if (email == null) return null;
        String trimmed = email.trim().toLowerCase();
        return trimmed.contains("@") ? trimmed : trimmed + "@milou.com";
    }
}
