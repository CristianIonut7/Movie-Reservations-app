package ro.facultate.proiect.backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.List;

@Repository
public class AdminRepository {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    // 1. Subcerere în clauza FROM: Top 3 filme după numărul de rezervări
    public List<Map<String, Object>> getTopMovies() {
        String sql = "SELECT Title, ReservationCount FROM (" +
                     "  SELECT m.Title, COUNT(bs.BookedSeatID) as ReservationCount " +
                     "  FROM Movies m " +
                     "  JOIN Showtimes s ON m.MovieID = s.MovieID " +
                     "  JOIN BookedSeats bs ON s.ShowtimeID = bs.ShowtimeID " +
                     "  GROUP BY m.Title" +
                     ") AS MovieStats ORDER BY ReservationCount DESC"; // Top 3 se poate face cu TOP 3 în SQL Server
        return jdbcTemplate.queryForList("SELECT TOP 3 * FROM (" + sql + ") AS T");
    }

    // 2. Subcerere în clauza WHERE: Utilizatori care au cheltuit peste medie
    public List<Map<String, Object>> getVipClients() {
        String sql = "SELECT FirstName, LastName, Email FROM Users " +
                     "WHERE UserID IN ( " +
                     "  SELECT b.UserID FROM Bookings b " +
                     "  JOIN Showtimes s ON b.ShowtimeID = s.ShowtimeID " +
                     "  GROUP BY b.UserID " +
                     "  HAVING SUM(s.TicketPrice) > (SELECT AVG(TicketPrice) FROM Showtimes)" +
                     ")";
        return jdbcTemplate.queryForList(sql);
    }

    // 3. Operație de Management: Promovare Admin (UPDATE)
    public int promoteToAdmin(String email) {
        return jdbcTemplate.update("UPDATE Users SET UserRole = 'admin' WHERE Email = ?", email);
    }
}