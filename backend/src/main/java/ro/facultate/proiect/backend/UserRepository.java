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

    public Optional<User> findById(int id) {
        String sql = "SELECT * FROM Users WHERE UserID = ?";
        try {
            User user = jdbcTemplate.queryForObject(sql,
                    new BeanPropertyRowMapper<>(User.class), id);
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

    public boolean updatePassword(int userId, String newPasswordHash) {
        String sql = "UPDATE Users SET PasswordHash = ? WHERE UserID = ?";
        try {
            int rows = jdbcTemplate.update(sql, newPasswordHash, userId);
            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @org.springframework.transaction.annotation.Transactional
    public boolean deleteUser(int userId) {
        try {
            // 1. Ștergem toate rezervările utilizatorului
            // Din cauză că avem ON DELETE CASCADE pe BookedSeats (FK_BookedSeats_Bookings),
            // putem șterge direct din Bookings, iar locurile se șterg automat.
            
            // Mai întâi, selectăm ID-urile rezervărilor pentru siguranță sau ștergem direct locurile dacă e nevoie manual
            // Dar conform schemei, FK_BookedSeats_Bookings are ON DELETE CASCADE.
            // Totuși, FK_Bookings_Users NU are? Ai zis "Baza de date nu șterge automat rezervările... lipsă CASCADE pe Users"
            // Deci da, trebuie să ștergem rezervările manual.
            
            // 1.1 Ștergem BookedSeats pentru toate rezervările userului
            String sqlDeleteSeats = "DELETE FROM BookedSeats WHERE BookingID IN (SELECT BookingID FROM Bookings WHERE UserID = ?)";
            jdbcTemplate.update(sqlDeleteSeats, userId);

            // 1.2 Ștergem Rezervările
            String sqlDeleteBookings = "DELETE FROM Bookings WHERE UserID = ?";
            jdbcTemplate.update(sqlDeleteBookings, userId);

            // 2. Ștergem Userul
            String sqlDeleteUser = "DELETE FROM Users WHERE UserID = ?";
            int rows = jdbcTemplate.update(sqlDeleteUser, userId);
            
            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}