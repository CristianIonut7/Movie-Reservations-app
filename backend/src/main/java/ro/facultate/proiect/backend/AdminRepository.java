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

    // Complex 1: Subcerere in clauza FROM. Top 3 filme dupa numarul de bilete vandute.
    public List<Map<String, Object>> getTopMovies() {
        String sql = "SELECT TOP 3 Title, TicketCount FROM (" +
                     "  SELECT m.Title, COUNT(bs.BookingID) as TicketCount " +
                     "  FROM Movies m " +
                     "  JOIN Showtimes s ON m.MovieID = s.MovieID " +
                     "  JOIN Bookings b ON s.ShowtimeID = b.ShowtimeID " +
                     "  JOIN BookedSeats bs ON b.BookingID = bs.BookingID " + 
                     "  GROUP BY m.Title" +
                     ") AS MovieStats ORDER BY TicketCount DESC";
        return jdbcTemplate.queryForList(sql);
    }

    // Complex 2: Subcerere in clauza WHERE, HAVING si SELECT. Clienti VIP (exclude admini) ordonati descrescator dupa cheltuieli.
    public List<Map<String, Object>> getVipClients() {
        String sql = "SELECT FirstName, LastName, Email, " +
                     "(SELECT SUM(b2.PaidPrice) FROM Bookings b2 WHERE b2.UserID = u.UserID) as TotalSpent " +
                     "FROM Users u " +
                     "WHERE u.UserRole != 'admin' AND u.UserID IN ( " +
                     "  SELECT b.UserID FROM Bookings b " +
                     "  GROUP BY b.UserID " +
                     "  HAVING SUM(b.PaidPrice) > (SELECT AVG(TicketPrice) FROM Showtimes) " +
                     ") ORDER BY TotalSpent DESC";
        return jdbcTemplate.queryForList(sql);
    }

    // Complex 3: Subcerere cu NOT EXISTS in clauza WHERE. Filme care nu au avut nicio rezervare.
    public List<Map<String, Object>> getMoviesWithoutBookings() {
        String sql = "SELECT m.Title, m.Genre, m.ReleaseDate FROM Movies m " +
                     "WHERE NOT EXISTS (" +
                     "  SELECT 1 FROM Showtimes s " +
                     "  JOIN Bookings b ON s.ShowtimeID = b.ShowtimeID " +
                     "  WHERE s.MovieID = m.MovieID" +
                     ")";
        return jdbcTemplate.queryForList(sql);
    }

    // Simple 1: Interogare cu JOIN intre 3 tabele. Venituri totale generate per film.
    public List<Map<String, Object>> getRevenuePerMovie() {
        String sql = "SELECT m.Title, SUM(b.PaidPrice) as TotalRevenue " +
                     "FROM Movies m " +
                     "JOIN Showtimes s ON m.MovieID = s.MovieID " +
                     "JOIN Bookings b ON s.ShowtimeID = b.ShowtimeID " +
                     "WHERE b.Status = 'Confirmed' " +
                     "GROUP BY m.Title " +
                     "ORDER BY TotalRevenue DESC";
        return jdbcTemplate.queryForList(sql);
    }

    // Simple 2: Interogare cu JOIN intre 3 tabele. Numarul de rezervari grupate pe genuri.
    public List<Map<String, Object>> getGenreStats() {
        String sql = "SELECT m.Genre, COUNT(b.BookingID) as BookingsCount " +
                     "FROM Movies m " +
                     "JOIN Showtimes s ON m.MovieID = s.MovieID " +
                     "JOIN Bookings b ON s.ShowtimeID = b.ShowtimeID " +
                     "GROUP BY m.Genre";
        return jdbcTemplate.queryForList(sql);
    }

    public int promoteToAdmin(String email) {
        return jdbcTemplate.update("UPDATE Users SET UserRole = 'admin' WHERE Email = ?", email);
    }
}