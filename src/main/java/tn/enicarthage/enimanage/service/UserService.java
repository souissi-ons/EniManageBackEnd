package tn.enicarthage.enimanage.service;

import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tn.enicarthage.enimanage.Model.Membership;
import tn.enicarthage.enimanage.Model.Role;
import tn.enicarthage.enimanage.Model.User;
import tn.enicarthage.enimanage.repository.MembershipRepository;
import tn.enicarthage.enimanage.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.SecureRandom;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MembershipRepository membershipRepository;

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

    public void changePassword(Long id, String currentPassword, String newPassword) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Debug: Afficher les valeurs pour investigation
        System.out.println("Input current: " + currentPassword);
        System.out.println("Stored hash: " + user.getPassword());
        System.out.println("Matches: " + passwordEncoder.matches(currentPassword, user.getPassword()));

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            // Vérification alternative pour debug
            if (user.getPassword().equals(currentPassword)) {
                throw new RuntimeException("Le mot de passe stocké n'est pas encodé. Mettez à jour le mot de passe.");
            }
            throw new RuntimeException("Current password is incorrect");
        }

        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new RuntimeException("New password must be different from current password");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    public List<User> getClubMembers(Long clubId) {
        User club = userRepository.findById(clubId)
                .orElseThrow(() -> new RuntimeException("Club not found"));
        return membershipRepository.findByClub(club)
                .stream()
                .map(Membership::getStudent)
                .collect(Collectors.toList());
    }

    public List<User> getNonMemberStudents(Long clubId) {
        User club = userRepository.findById(clubId)
                .orElseThrow(() -> new RuntimeException("Club not found"));
        List<User> allStudents = userRepository.findByRole(Role.STUDENT);
        List<User> members = getClubMembers(clubId);
        return allStudents.stream()
                .filter(student -> !members.contains(student))
                .collect(Collectors.toList());
    }

    public void addMemberToClub(Long clubId, Long studentId) {
        User club = userRepository.findById(clubId)
                .orElseThrow(() -> new RuntimeException("Club not found"));
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        if (!club.getRole().equals(Role.CLUB)) {
            throw new RuntimeException("User is not a club");
        }

        if (!student.getRole().equals(Role.STUDENT)) {
            throw new RuntimeException("User is not a student");
        }

        if (membershipRepository.existsByClubAndStudent(club, student)) {
            throw new RuntimeException("Student is already a member");
        }

        Membership membership = Membership.builder()
                .club(club)
                .student(student)
                .build();

        membershipRepository.save(membership);
    }

    public void removeMemberFromClub(Long clubId, Long studentId) {
        User club = userRepository.findById(clubId)
                .orElseThrow(() -> new RuntimeException("Club not found"));
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        membershipRepository.deleteByClubAndStudent(club, student);
    }

    public List<Membership> getMembershipsByClub(Long clubId) {
        User club = userRepository.findById(clubId)
                .orElseThrow(() -> new RuntimeException("Club not found"));
        return membershipRepository.findByClub(club);
    }

    // Nouvelle méthode pour supprimer une adhésion par son ID
    public void removeMembershipById(Long clubId, Long membershipId) {
        // Vérifier que le club existe
        User club = userRepository.findById(clubId)
                .orElseThrow(() -> new RuntimeException("Club not found"));

        // Vérifier que l'adhésion existe et appartient à ce club
        Membership membership = membershipRepository.findById(membershipId)
                .orElseThrow(() -> new RuntimeException("Membership not found"));

        if (!membership.getClub().getId().equals(clubId)) {
            throw new RuntimeException("Membership does not belong to this club");
        }

        // Supprimer l'adhésion
        membershipRepository.deleteById(membershipId);
    }
}