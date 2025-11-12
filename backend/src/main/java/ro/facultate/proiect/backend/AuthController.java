package ro.facultate.proiect.backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private UserRepository userRepository;

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

            if (loginData.getPassword().equals(user.getPasswordHash())) {
                
                return ResponseEntity.ok("Login OK! Welcome, " + user.getFirstName());
            } else {
                return ResponseEntity.status(401).body("Error: Wrong Password");
            }
        } else {
            return ResponseEntity.status(404).body("Error: User doesn't exist");
        }
    }
}