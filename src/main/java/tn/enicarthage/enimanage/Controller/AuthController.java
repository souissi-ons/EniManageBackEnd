package tn.enicarthage.enimanage.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import tn.enicarthage.enimanage.DTO.AuthRequest;
import tn.enicarthage.enimanage.DTO.AuthResponse;
import tn.enicarthage.enimanage.DTO.ValidateTokenRequest;
import tn.enicarthage.enimanage.DTO.ValidateTokenResponse;
import tn.enicarthage.enimanage.Model.User;
import tn.enicarthage.enimanage.service.AuthService;
import tn.enicarthage.enimanage.service.JwtService;
import tn.enicarthage.enimanage.service.UserService;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final UserService userService;
    private final JwtService jwtService;


    @PostMapping("/login")
    public ResponseEntity<AuthResponse> authenticate(@RequestBody AuthRequest request) {
        return ResponseEntity.ok(authService.authenticate(request));
    }

    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User user = userService.getUserByEmail(auth.getName());
            return ResponseEntity.ok(user); // IMPORTANT: Toujours retourner 200 si authentifié
        } catch (Exception e) {
            // Ne pas retourner 401/403 ici
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/validate-token")
    public ResponseEntity<ValidateTokenResponse> validateToken(@RequestBody ValidateTokenRequest request) {
        boolean isValid = jwtService.isTokenValid(request.getToken());
        return ResponseEntity.ok(new ValidateTokenResponse(isValid));
    }
}