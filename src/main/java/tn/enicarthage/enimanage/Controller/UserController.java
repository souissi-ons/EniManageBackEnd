package tn.enicarthage.enimanage.Controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import tn.enicarthage.enimanage.Model.User;
import tn.enicarthage.enimanage.service.FileStorageService;
import tn.enicarthage.enimanage.service.UserService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final FileStorageService fileStorageService;


    public UserController(UserService userService, FileStorageService fileStorageService) {
        this.userService = userService;
        this.fileStorageService = fileStorageService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<User> createUser(
            @RequestPart @Validated User user,
            @RequestPart(required = false) MultipartFile logo) {

        if (logo != null && !logo.isEmpty()) {
            try {
                String fileName = fileStorageService.storeFile(logo);
                user.setLogo(fileName);
            } catch (IOException e) {
                throw new RuntimeException("Erreur lors du traitement du fichier image", e);
            }
        }

        User savedUser = userService.createUserWithGeneratedPassword(user);
        return ResponseEntity.ok(savedUser);
    }


    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        users.forEach(user -> {
            if (user.getLogo() != null) {
                user.setLogo(user.getLogo());
            }
        });
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        if (user.getLogo() != null) {
            user.setLogo(user.getLogo());
        }
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }

    @PatchMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<User> updateUser(
            @PathVariable Long id,
            @RequestPart @Validated User user,
            @RequestPart(required = false) MultipartFile logo) {

        User existingUser = userService.getUserById(id);

        if (logo != null && !logo.isEmpty()) {
            try {
                if (existingUser.getLogo() != null) {
                    fileStorageService.deleteFile(existingUser.getLogo());
                }
                String fileName = fileStorageService.storeFile(logo);
                user.setLogo(fileName);

            } catch (IOException e) {
                throw new RuntimeException("Erreur lors du traitement du fichier image", e);
            }

        }
        else {
            user.setLogo(existingUser.getLogo());
        }

        User updatedUser = userService.updateUserWithoutPassword(id, user);

        if (updatedUser.getLogo() != null) {
            updatedUser.setLogo(updatedUser.getLogo());
        }

        return ResponseEntity.ok(updatedUser);
    }

    @GetMapping("/images/{filename:.+}")
    public ResponseEntity<byte[]> serveFile(@PathVariable String filename) {
        try {
            Path file = fileStorageService.load(filename);
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(Files.readAllBytes(file));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Image non trouvée");
        }
    }
}

