package ro.facultate.proiect.backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

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
        public String getEmail() { return email; }
        public String getPassword() { return password; }
    }

    @PostMapping("/login")
    public ResponseEntity<String> handleLogin(@RequestBody LoginRequest loginData) {
        Optional<User> userOptional = userRepository.findByEmail(loginData.getEmail());

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            // Verificăm dacă parola introdusă corespunde cu hash-ul din DB
            if (passwordEncoder.matches(loginData.getPassword(), user.getPasswordHash())) {
                return ResponseEntity.ok("Login OK! Welcome, " + user.getFirstName());
            } else {
                return ResponseEntity.status(401).body("Error: Wrong Password");
            }
        }
        return ResponseEntity.status(404).body("Error: User doesn't exist");
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
}