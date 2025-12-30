package ro.facultate.proiect.backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.HashMap;
import java.util.Optional;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // DTO pentru Login
    public static class LoginRequest {
        private String email;
        private String password;

        public String getEmail() {
            return email;
        }

        public String getPassword() {
            return password;
        }
    }

    // În AuthController.java
    @PostMapping("/login")
    public ResponseEntity<?> handleLogin(@RequestBody LoginRequest loginData) {
        Optional<User> userOptional = userRepository.findByEmail(loginData.getEmail());

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (passwordEncoder.matches(loginData.getPassword(), user.getPasswordHash())) {
                // În loc de un Map manual, trimitem tot obiectul user (fără parolă din motive
                // de securitate)
                user.setPasswordHash(null);
                return ResponseEntity.ok(user);
            }
        }
        return ResponseEntity.status(401).body("Date invalide");
    }

    @PostMapping("/signup")
    public ResponseEntity<String> handleSignup(@RequestBody User newUser) {
        try {
            userRepository.registerUser(newUser);
            return ResponseEntity.ok("User registered successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Error: Could not create account. Email might be taken.");
        }
    }

    @PutMapping("/update-profile")
    public ResponseEntity<String> updateProfile(@RequestBody User user) {
        int result = userRepository.updateUserInfo(user);
        if (result > 0) {
            return ResponseEntity.ok("Profil actualizat cu succes!");
        } else {
            return ResponseEntity.status(400).body("Eroare la actualizare.");
        }
    }
}