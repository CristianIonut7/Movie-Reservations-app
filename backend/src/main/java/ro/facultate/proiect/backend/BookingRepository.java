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

    // 2. Salvare Rezervare (INSERT pe 2 tabele - Relație N:N)
    @Transactional
    public void createBooking(int userId, int showtimeId, List<Integer> seatIds) {
        // 1. Inserăm în Bookings (unde avem ShowtimeID)
        String bookingSql = "INSERT INTO Bookings (UserID, ShowtimeID, BookingTime, Status) VALUES (?, ?, GETDATE(), 'Confirmed')";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(bookingSql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, userId);
            ps.setInt(2, showtimeId);
            return ps;
        }, keyHolder);

        Number key = keyHolder.getKey();
        if (key == null)
            throw new RuntimeException("Eroare la generarea ID-ului rezervării.");
        int bookingId = key.intValue();

        // 2. Inserăm în BookedSeats (doar legătura între Rezervare și Loc)
        String bookedSeatSql = "INSERT INTO BookedSeats (BookingID, SeatID) VALUES (?, ?)";
        for (Integer seatId : seatIds) {
            jdbcTemplate.update(bookedSeatSql, bookingId, seatId);
        }
    }
}