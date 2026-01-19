package ro.facultate.proiect.backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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

    @PostMapping("/login")
    public ResponseEntity<?> handleLogin(@RequestBody LoginRequest loginData) {
        Optional<User> userOptional = userRepository.findByEmail(loginData.getEmail());

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (passwordEncoder.matches(loginData.getPassword(), user.getPasswordHash())) {
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

    @org.springframework.web.bind.annotation.GetMapping("/user/{id}")
    public ResponseEntity<?> getUserById(@org.springframework.web.bind.annotation.PathVariable int id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            User u = user.get();
            u.setPasswordHash(null);
            return ResponseEntity.ok(u);
        }
        return ResponseEntity.status(404).body("User not found");
    }

    public static class ChangePasswordRequest {
        public int userId;
        public String oldPassword;
        public String newPassword;
    }

    @PostMapping("/update-password")
    public ResponseEntity<String> changePassword(@RequestBody ChangePasswordRequest req) {
        Optional<User> userOpt = userRepository.findById(req.userId);
        
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Utilizatorul nu există.");
        }

        User user = userOpt.get();

        if (!passwordEncoder.matches(req.oldPassword, user.getPasswordHash())) {
            return ResponseEntity.status(400).body("Parola veche este incorectă.");
        }

        String newHash = passwordEncoder.encode(req.newPassword);

        boolean success = userRepository.updatePassword(req.userId, newHash);
        
        if (success) {
            return ResponseEntity.ok("Parola a fost schimbată cu succes!");
        } else {
            return ResponseEntity.status(500).body("Eroare la schimbarea parolei.");
        }
    }

    @org.springframework.web.bind.annotation.DeleteMapping("/delete-account/{id}")
    public ResponseEntity<String> deleteAccount(@org.springframework.web.bind.annotation.PathVariable int id) {
        boolean success = userRepository.deleteUser(id);
        if (success) {
            return ResponseEntity.ok("Cont șters cu succes.");
        } else {
            return ResponseEntity.status(500).body("Nu s-a putut șterge contul.");
        }
    }
}