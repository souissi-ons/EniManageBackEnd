package tn.enicarthage.enimanage.service;

import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tn.enicarthage.enimanage.Model.Role;
import tn.enicarthage.enimanage.Model.User;
import tn.enicarthage.enimanage.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.SecureRandom;
import java.util.List;

@Service
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<User> getAllUsers() {
        List<User> users = userRepository.findAll();
        System.out.println(users);
        if (!users.isEmpty()) {
            for (User user : users) {
                if (!user.getRole().equals(Role.CLUB)) {
                    user.setLogo(null);
                    user.setDescription(null);
                }
            }
            return users;
        }

        return null;
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public User createUserWithGeneratedPassword(User user) {
        String rawPassword = generateRandomPassword(10);
        logger.info("Generated password for user {}: {}", user.getEmail(), rawPassword);

        String encodedPassword = passwordEncoder.encode(rawPassword);
        user.setPassword(encodedPassword);

        logger.info("Saving user to database...");
        User savedUser = userRepository.save(user);
        logger.info("User saved successfully with ID: {}", savedUser.getId());

        try {
            logger.info("Attempting to send email to: {}", user.getEmail());
            emailService.sendPasswordEmail(user.getEmail(), rawPassword);
            logger.info("Email sent successfully to: {}", user.getEmail());
        } catch (MessagingException e) {
            logger.error("Failed to send email to: " + user.getEmail(), e);
            throw new RuntimeException("Erreur lors de l'envoi de l'email: " + e.getMessage(), e);
        }

        return savedUser;
    }

    private String generateRandomPassword(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    public User updateUserWithoutPassword(Long id, User updatedData) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        existingUser.setName(updatedData.getName());
        existingUser.setEmail(updatedData.getEmail());
        existingUser.setRole(updatedData.getRole());
        existingUser.setBirthDate(updatedData.getBirthDate());
        existingUser.setPhoneNumber(updatedData.getPhoneNumber());
        existingUser.setDescription(updatedData.getDescription());
        existingUser.setLogo(updatedData.getLogo());

        return userRepository.save(existingUser);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
    }
}