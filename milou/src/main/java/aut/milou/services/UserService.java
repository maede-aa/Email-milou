package aut.milou.services;

import aut.milou.model.User;
import aut.milou.Repository.UserRepository;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Optional;
import java.util.logging.Logger;

public class UserService {
    private static final Logger logger = Logger.getLogger(UserService.class.getName());
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean register(String name ,String email ,String password) {
        email = normalizeEmail(email);
        Optional<User> existingUser = userRepository.findByEmail(email);
        if (existingUser.isPresent()) {
            logger.warning("Attempt to register with taken email: " + email);
            return false;
        }
        if (!isPasswordValid(password)) {
            logger.warning("Invalid password for email: " + email);
            return false;
        }

        String hashedPassword = BCrypt.hashpw(password ,BCrypt.gensalt());
        User user = new User(name ,email ,hashedPassword);
        userRepository.save(user);
        logger.info("User registered successfully: " + email);
        return true;
    }

    public User login(String email ,String password) {
        email = normalizeEmail(email);
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            logger.warning("Login attempt with non-existent email: " + email);
            return null;
        }
        if (!BCrypt.checkpw(password ,user.get().getPassword())) {
            logger.warning("Invalid password for email: " + email);
            return null;
        }

        logger.info("User logged in: " + email);
        return user.get();
    }

    public boolean isEmailTaken(String email) {
        return userRepository.findByEmail(normalizeEmail(email)).isPresent();
    }

    public boolean isPasswordValid(String password) {
        return password != null && password.length() >= 8;
    }

    private String normalizeEmail(String email) {
        return email.contains("@") ? email : email + "@milou.com";
    }
}