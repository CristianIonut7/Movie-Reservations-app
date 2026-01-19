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

    // 1. [Complex 1] Subcerere în clauza FROM: Top 3 filme după numărul de bilete vândute
    public List<Map<String, Object>> getTopMovies() {
        String sql = "SELECT TOP 3 Title, TicketCount FROM (" +
                     "  SELECT m.Title, COUNT(bs.BookingID) as TicketCount " +
                     "  FROM Movies m " +
                     "  JOIN Showtimes s ON m.MovieID = s.MovieID " +
                     "  JOIN Bookings b ON s.ShowtimeID = b.ShowtimeID " +
                     "  JOIN BookedSeats bs ON b.BookingID = bs.BookingID " + // Correct path: Movies->Showtimes->Bookings->BookedSeats
                     "  GROUP BY m.Title" +
                     ") AS MovieStats ORDER BY TicketCount DESC";
        return jdbcTemplate.queryForList(sql);
    }

    // 2. [Complex 2] Subcerere în clauza WHERE: Clienți VIP (cheltuieli peste medie)
    public List<Map<String, Object>> getVipClients() {
        String sql = "SELECT FirstName, LastName, Email, (SELECT SUM(s2.TicketPrice) FROM Bookings b2 JOIN Showtimes s2 ON b2.ShowtimeID = s2.ShowtimeID WHERE b2.UserID = u.UserID) as TotalSpent " +
                     "FROM Users u " +
                     "WHERE u.UserID IN ( " +
                     "  SELECT b.UserID FROM Bookings b " +
                     "  JOIN Showtimes s ON b.ShowtimeID = s.ShowtimeID " +
                     "  GROUP BY b.UserID " +
                     "  HAVING SUM(s.TicketPrice) > (SELECT AVG(TicketPrice) FROM Showtimes)" +
                     ")";
        return jdbcTemplate.queryForList(sql);
    }

    // 3. [Complex 3] Filme care nu au avut nicio rezervare (NOT EXISTS)
    public List<Map<String, Object>> getMoviesWithoutBookings() {
        String sql = "SELECT m.Title, m.Genre, m.ReleaseDate FROM Movies m " +
                     "WHERE NOT EXISTS (" +
                     "  SELECT 1 FROM Showtimes s " +
                     "  JOIN Bookings b ON s.ShowtimeID = b.ShowtimeID " +
                     "  WHERE s.MovieID = m.MovieID" +
                     ")";
        return jdbcTemplate.queryForList(sql);
    }

    // 4. [Simple 1] Venituri generate per Film (JOIN Movie-Showtime-Booking)
    public List<Map<String, Object>> getRevenuePerMovie() {
        String sql = "SELECT m.Title, SUM(s.TicketPrice) as TotalRevenue " +
                     "FROM Movies m " +
                     "JOIN Showtimes s ON m.MovieID = s.MovieID " +
                     "JOIN Bookings b ON s.ShowtimeID = b.ShowtimeID " +
                     "WHERE b.Status = 'Confirmed' " +
                     "GROUP BY m.Title " +
                     "ORDER BY TotalRevenue DESC";
        return jdbcTemplate.queryForList(sql);
    }

    // 5. [Simple 2] Statistici Rezervări pe Genuri (JOIN Genre-Movie-Showtime-Booking)
    public List<Map<String, Object>> getGenreStats() {
        String sql = "SELECT m.Genre, COUNT(b.BookingID) as BookingsCount " +
                     "FROM Movies m " +
                     "JOIN Showtimes s ON m.MovieID = s.MovieID " +
                     "JOIN Bookings b ON s.ShowtimeID = b.ShowtimeID " +
                     "GROUP BY m.Genre";
        return jdbcTemplate.queryForList(sql);
    }

    // 3. Operație de Management: Promovare Admin (UPDATE)
    public int promoteToAdmin(String email) {
        return jdbcTemplate.update("UPDATE Users SET UserRole = 'admin' WHERE Email = ?", email);
    }
}