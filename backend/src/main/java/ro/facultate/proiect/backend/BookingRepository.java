package ro.facultate.proiect.backend;

import org.springframework.beans.factory.annotation.Autowired;
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
        String sql = "SELECT s.SeatID, s.RowNumber, s.SeatNumber, " +
                     "CASE WHEN bs.SeatID IS NOT NULL THEN 1 ELSE 0 END as isOccupied " +
                     "FROM Seats s " +
                     "LEFT JOIN BookedSeats bs ON s.SeatID = bs.SeatID AND bs.ShowtimeID = ? " +
                     "WHERE s.RoomID = (SELECT RoomID FROM Showtimes WHERE ShowtimeID = ?)";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            SeatDTO seat = new SeatDTO();
            seat.setSeatId(rs.getInt("SeatID"));
            seat.setRowNumber(rs.getInt("RowNumber"));
            seat.setSeatNumber(rs.getInt("SeatNumber"));
            seat.setOccupied(rs.getBoolean("isOccupied"));
            return seat;
        }, showtimeId, showtimeId);
    }

    // 2. Salvare Rezervare (INSERT pe 2 tabele - Relație N:N)
    @Transactional
    public void createBooking(int userId, int showtimeId, List<Integer> seatIds) {
        // A. Inserăm în tabelul Bookings
        String bookingSql = "INSERT INTO Bookings (UserID, ShowtimeID, BookingDate, Status) VALUES (?, ?, GETDATE(), 'Confirmed')";
        
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(bookingSql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, userId);
            ps.setInt(2, showtimeId);
            return ps;
        }, keyHolder);

        int bookingId = keyHolder.getKey().intValue();

        // B. Inserăm fiecare loc selectat în BookedSeats (Tabelul de legătură)
        String bookedSeatSql = "INSERT INTO BookedSeats (BookingID, SeatID, ShowtimeID) VALUES (?, ?, ?)";
        for (Integer seatId : seatIds) {
            jdbcTemplate.update(bookedSeatSql, bookingId, seatId, showtimeId);
        }
    }
}