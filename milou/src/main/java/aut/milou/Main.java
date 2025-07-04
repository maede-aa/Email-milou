package aut.milou;

import aut.milou.controller.EmailController;
import aut.milou.controller.UserController;
import aut.milou.Repository.EmailRepository;
import aut.milou.Repository.UserRepository;
import aut.milou.services.EmailService;
import aut.milou.services.UserService;

public class Main {
    public static void main(String[] args) {
        UserRepository userRepository = new UserRepository();
        EmailRepository emailRepository = new EmailRepository();
        UserService userService = new UserService(userRepository);
        EmailService emailService = new EmailService(emailRepository, userRepository);
        UserController userController = new UserController(userService);
        EmailController emailController = new EmailController(emailService);

        Processor processor = new Processor(userController, emailController);
        processor.start();
    }
}
