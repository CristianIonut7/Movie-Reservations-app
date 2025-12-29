package ro.facultate.proiect.backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public Optional<User> findByEmail(String email) {
        String sql = "SELECT * FROM Users WHERE Email = ?";
        try {
            User user = jdbcTemplate.queryForObject(sql,
                    new BeanPropertyRowMapper<>(User.class), email);
            return Optional.ofNullable(user);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    // Această metodă bifează cerința de INSERT din proiect 
    public int registerUser(User user) {
        String sql = "INSERT INTO Users (FirstName, LastName, Email, PasswordHash, UserRole, Age, City, PhoneNumber) " +
                     "VALUES (?, ?, ?, ?, 'client', ?, ?, ?)";
        
        // Hashing-ul parolei înainte de salvare
        String hashedPassword = passwordEncoder.encode(user.getPasswordHash());

        return jdbcTemplate.update(sql, 
            user.getFirstName(), 
            user.getLastName(), 
            user.getEmail(), 
            hashedPassword, 
            user.getAge(), 
            user.getCity(), 
            user.getPhoneNumber());
    }
}