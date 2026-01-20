package ro.facultate.proiect.backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

@Repository
public class BookingRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // Complex 4: Subcerere in clauza SELECT (CASE WHEN EXISTS). Verifica starea locurilor pentru o difuzare.
    public List<SeatDTO> getSeatsStatus(int showtimeId) {
        String sql = "SELECT s.SeatID AS seatId, s.RowNumber AS rowNumber, s.SeatNumber AS seatNumber, " +
                "CASE WHEN EXISTS ( " +
                "    SELECT 1 FROM BookedSeats bs " +
                "    JOIN Bookings b ON bs.BookingID = b.BookingID " +
                "    WHERE bs.SeatID = s.SeatID AND b.ShowtimeID = ? " +
                ") THEN 1 ELSE 0 END AS occupied " +
                "FROM Seats s " +
                "WHERE s.RoomID = (SELECT TOP 1 RoomID FROM Showtimes WHERE ShowtimeID = ?)";

        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(SeatDTO.class), showtimeId, showtimeId);
    }

    // Simple 5: Interogare cu JOIN intre 2 tabele in interiorul metodei (createBooking nu este o interogare simpla, dar contine una).
    @Transactional
    public void createBooking(int userId, int showtimeId, List<Integer> seatIds, boolean usePoints) {
        String userSql = "SELECT Age, LoyaltyPoints FROM Users WHERE UserID = ?";
        Map<String, Object> userMap = jdbcTemplate.queryForMap(userSql, userId);
        Integer userAge = (Integer) userMap.get("Age");
        Integer loyaltyPoints = (Integer) userMap.get("LoyaltyPoints");
        if (userAge == null) userAge = 0;
        if (loyaltyPoints == null) loyaltyPoints = 0;

        String showtimeSql = "SELECT s.TicketPrice, m.MinAge FROM Showtimes s JOIN Movies m ON s.MovieID = m.MovieID WHERE s.ShowtimeID = ?";
        Map<String, Object> showtimeMap = jdbcTemplate.queryForMap(showtimeSql, showtimeId);
        java.math.BigDecimal ticketPrice = (java.math.BigDecimal) showtimeMap.get("TicketPrice");
        Integer minAge = (Integer) showtimeMap.get("MinAge");

        if (userAge < minAge) {
            throw new RuntimeException("Nu aveți vârsta necesară pentru acest film (" + minAge + "+).");
        }

        int seatCount = seatIds.size();
        java.math.BigDecimal totalCost = ticketPrice.multiply(new java.math.BigDecimal(seatCount));
        
        if (usePoints && loyaltyPoints >= 100) {
            if (seatCount > 2) {
                throw new RuntimeException("Puteți folosi punctele pentru o reducere doar dacă selectați maxim 2 locuri.");
            }

            String updatePointsSql = "UPDATE Users SET LoyaltyPoints = LoyaltyPoints - 100 WHERE UserID = ?";
            jdbcTemplate.update(updatePointsSql, userId);
            
            totalCost = java.math.BigDecimal.ZERO;
            
        } else {
            String updatePointsSql = "UPDATE Users SET LoyaltyPoints = LoyaltyPoints + 10 WHERE UserID = ?";
            jdbcTemplate.update(updatePointsSql, userId);
        }

        String bookingSql = "INSERT INTO Bookings (UserID, ShowtimeID, BookingTime, Status, PaidPrice) VALUES (?, ?, GETDATE(), 'Confirmed', ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        java.math.BigDecimal finalCost = totalCost;
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(bookingSql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, userId);
            ps.setInt(2, showtimeId);
            ps.setBigDecimal(3, finalCost);
            return ps;
        }, keyHolder);

        Number key = keyHolder.getKey();
        if (key == null)
            throw new RuntimeException("Eroare la generarea ID-ului rezervării.");
        int bookingId = key.intValue();

        String bookedSeatSql = "INSERT INTO BookedSeats (BookingID, SeatID) VALUES (?, ?)";
        for (Integer seatId : seatIds) {
            jdbcTemplate.update(bookedSeatSql, bookingId, seatId);
        }
    }

    // Simple 6: Interogare cu JOIN intre 5 tabele si grupare (fostul Complex 5). Lista rezervarilor unui utilizator cu locurile concatenate.
    public List<Map<String, Object>> getUserBookings(int userId) {
        String sql = "SELECT b.BookingID, m.Title, s.StartTime, b.BookingTime, b.Status, b.PaidPrice, " +
                "STRING_AGG(CONCAT(CHAR(64 + se.RowNumber), se.SeatNumber), ', ') AS Seats " +
                "FROM Bookings b " +
                "JOIN Showtimes s ON b.ShowtimeID = s.ShowtimeID " +
                "JOIN Movies m ON s.MovieID = m.MovieID " +
                "LEFT JOIN BookedSeats bs ON b.BookingID = bs.BookingID " +
                "LEFT JOIN Seats se ON bs.SeatID = se.SeatID " +
                "WHERE b.UserID = ? " +
                "GROUP BY b.BookingID, m.Title, s.StartTime, b.BookingTime, b.Status, b.PaidPrice " +
                "ORDER BY b.BookingTime DESC";

        return jdbcTemplate.queryForList(sql, userId);
    }


    @Transactional
    public void cancelBooking(int bookingId) {
        String infoSql = "SELECT UserID, PaidPrice FROM Bookings WHERE BookingID = ?";
        try {
            Map<String, Object> bookingMap = jdbcTemplate.queryForMap(infoSql, bookingId);
            int userId = (int) bookingMap.get("UserID");
            java.math.BigDecimal paidPrice = (java.math.BigDecimal) bookingMap.get("PaidPrice");

            if (paidPrice == null || paidPrice.compareTo(java.math.BigDecimal.ZERO) == 0) {
                String updatePointsSql = "UPDATE Users SET LoyaltyPoints = LoyaltyPoints + 100 WHERE UserID = ?";
                jdbcTemplate.update(updatePointsSql, userId);
            } else {
                String updatePointsSql = "UPDATE Users SET LoyaltyPoints = CASE WHEN LoyaltyPoints >= 10 THEN LoyaltyPoints - 10 ELSE 0 END WHERE UserID = ?";
                jdbcTemplate.update(updatePointsSql, userId);
            }

        } catch (Exception e) {
            // Rezervarea poate nu exista, continuam cu stergerea safe
        }

        String sqlSeats = "DELETE FROM BookedSeats WHERE BookingID = ?";
        jdbcTemplate.update(sqlSeats, bookingId);

        String sqlBooking = "DELETE FROM Bookings WHERE BookingID = ?";
        jdbcTemplate.update(sqlBooking, bookingId);
    }
}