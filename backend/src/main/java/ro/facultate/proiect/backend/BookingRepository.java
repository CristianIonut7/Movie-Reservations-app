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

    // 1. Vezi locurile libere/ocupate pentru o difuzare (JOIN + Subquery)
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

    // 2. Salvare Rezervare (INSERT pe 2 tabele - Relație N:N) + Business Logic
    @Transactional
    public void createBooking(int userId, int showtimeId, List<Integer> seatIds, boolean usePoints) {
        // A. Obținem datele despre Utilizator (Vârstă, Puncte)
        String userSql = "SELECT Age, LoyaltyPoints FROM Users WHERE UserID = ?";
        Map<String, Object> userMap = jdbcTemplate.queryForMap(userSql, userId);
        Integer userAge = (Integer) userMap.get("Age");
        Integer loyaltyPoints = (Integer) userMap.get("LoyaltyPoints");
        if (userAge == null) userAge = 0;
        if (loyaltyPoints == null) loyaltyPoints = 0;

        // B. Obținem datele despre Film și Difuzare (Preț bilet, Vârstă minimă)
        String showtimeSql = "SELECT s.TicketPrice, m.MinAge FROM Showtimes s JOIN Movies m ON s.MovieID = m.MovieID WHERE s.ShowtimeID = ?";
        Map<String, Object> showtimeMap = jdbcTemplate.queryForMap(showtimeSql, showtimeId);
        java.math.BigDecimal ticketPrice = (java.math.BigDecimal) showtimeMap.get("TicketPrice");
        Integer minAge = (Integer) showtimeMap.get("MinAge");

        // C. Validare Vârstă
        if (userAge < minAge) {
            throw new RuntimeException("Nu aveți vârsta necesară pentru acest film (" + minAge + "+).");
        }

        // D. Calcul Preț și Puncte de Loialitate
        int seatCount = seatIds.size();
        java.math.BigDecimal totalCost = ticketPrice.multiply(new java.math.BigDecimal(seatCount));
        
        // Logică Puncte:
        // - Dacă usePoints = true și user are >= 100 puncte: scădem 100 pt, și primele 2 bilete sunt GRATUITE.
        // - Altfel: user primește 10 puncte (indiferent de nr. bilete, per tranzacție - simplisficat).
        
        if (usePoints && loyaltyPoints >= 100) {
            // VERIFICARE NOUĂ: Maxim 2 locuri permise pentru reducerea cu puncte
            if (seatCount > 2) {
                throw new RuntimeException("Puteți folosi punctele pentru o reducere doar dacă selectați maxim 2 locuri.");
            }

            // Aplicăm discount
            // Scădem 100 puncte
            String updatePointsSql = "UPDATE Users SET LoyaltyPoints = LoyaltyPoints - 100 WHERE UserID = ?";
            jdbcTemplate.update(updatePointsSql, userId);
            
            // Calculăm noul preț (GRATUIT deoarece am validat că sunt maxim 2 locuri)
            totalCost = java.math.BigDecimal.ZERO;
            
        } else {
            // Adăugăm puncte (ex: 10 puncte per rezervare)
            String updatePointsSql = "UPDATE Users SET LoyaltyPoints = LoyaltyPoints + 10 WHERE UserID = ?";
            jdbcTemplate.update(updatePointsSql, userId);
        }

        // E. Inserăm în Bookings (PaidPrice inclus)
        String bookingSql = "INSERT INTO Bookings (UserID, ShowtimeID, BookingTime, Status, PaidPrice) VALUES (?, ?, GETDATE(), 'Confirmed', ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        java.math.BigDecimal finalCost = totalCost; // effectively final for lambda
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

        // F. Inserăm în BookedSeats
        String bookedSeatSql = "INSERT INTO BookedSeats (BookingID, SeatID) VALUES (?, ?)";
        for (Integer seatId : seatIds) {
            jdbcTemplate.update(bookedSeatSql, bookingId, seatId);
        }
    }

    public List<Map<String, Object>> getUserBookings(int userId) {
        // Interogare complexă (JOIN) conform cerinței 4
        // Nu afișăm ID-uri, ci informații relevante: Titlu, Dată, Status
        String sql = "SELECT b.BookingID, m.Title, s.StartTime, b.BookingTime, b.Status, b.PaidPrice, " +
                "(SELECT STRING_AGG(CONCAT(CHAR(64 + se.RowNumber), se.SeatNumber), ', ') " +
                " FROM BookedSeats bs JOIN Seats se ON bs.SeatID = se.SeatID " +
                " WHERE bs.BookingID = b.BookingID) AS Seats " +
                "FROM Bookings b " +
                "JOIN Showtimes s ON b.ShowtimeID = s.ShowtimeID " +
                "JOIN Movies m ON s.MovieID = m.MovieID " +
                "WHERE b.UserID = ? " +
                "ORDER BY b.BookingTime DESC";

        return jdbcTemplate.queryForList(sql, userId);
    }

    @Transactional
    public void cancelBooking(int bookingId) {
        // Obținem informații despre rezervare înainte de ștergere
        String infoSql = "SELECT UserID, PaidPrice FROM Bookings WHERE BookingID = ?";
        try {
            Map<String, Object> bookingMap = jdbcTemplate.queryForMap(infoSql, bookingId);
            int userId = (int) bookingMap.get("UserID");
            java.math.BigDecimal paidPrice = (java.math.BigDecimal) bookingMap.get("PaidPrice");

            // LOGICA RESTITUIRE / ANULARE PUNCTE:
            if (paidPrice == null || paidPrice.compareTo(java.math.BigDecimal.ZERO) == 0) {
                // Preț 0 -> A folosit puncte -> Îi dăm înapoi cele 100 de puncte
                String updatePointsSql = "UPDATE Users SET LoyaltyPoints = LoyaltyPoints + 100 WHERE UserID = ?";
                jdbcTemplate.update(updatePointsSql, userId);
            } else {
                // Preț > 0 -> A primit puncte (10) -> I le luăm înapoi (pentru a evita abuzul)
                // Ne asigurăm că nu scade sub 0
                String updatePointsSql = "UPDATE Users SET LoyaltyPoints = CASE WHEN LoyaltyPoints >= 10 THEN LoyaltyPoints - 10 ELSE 0 END WHERE UserID = ?";
                jdbcTemplate.update(updatePointsSql, userId);
            }

        } catch (Exception e) {
            // Rezervarea poate nu există, continuăm cu ștergerea safe
        }

        // 1. Ștergem locurile rezervate (tabelul de legătură N:N)
        String sqlSeats = "DELETE FROM BookedSeats WHERE BookingID = ?";
        jdbcTemplate.update(sqlSeats, bookingId);

        // 2. Ștergem rezervarea propriu-zisă
        String sqlBooking = "DELETE FROM Bookings WHERE BookingID = ?";
        jdbcTemplate.update(sqlBooking, bookingId);
    }
}