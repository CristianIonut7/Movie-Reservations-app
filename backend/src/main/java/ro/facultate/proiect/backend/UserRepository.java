package ro.facultate.proiect.backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
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

    public int updateUserInfo(User user) {
        String sql = "UPDATE Users SET FirstName = ?, LastName = ?, Age = ?, City = ?, PhoneNumber = ? WHERE Email = ?";
        try {
            int rows = jdbcTemplate.update(sql,
                    user.getFirstName(),
                    user.getLastName(),
                    user.getAge(),
                    user.getCity(),
                    user.getPhoneNumber(),
                    user.getEmail());
            System.out.println("Rows updated: " + rows); // Vezi în consola IDE-ului dacă e 0 sau 1
            return rows;
        } catch (Exception e) {
            e.printStackTrace(); // Aici vei vedea eroarea SQL reală
            return 0;
        }
    }
}