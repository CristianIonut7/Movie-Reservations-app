package ro.facultate.proiect.backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class ShowtimeRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void addShowtime(Showtime showtime) {
        String sql = "INSERT INTO Showtimes (MovieID, RoomID, StartTime, TicketPrice) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql, showtime.getMovieID(), showtime.getRoomID(), showtime.getStartTime(), showtime.getTicketPrice());
    }

    // Simple 3: Interogare cu LEFT JOIN intre 3 tabele. Lista tuturor difuzarilor cu detalii despre film si sala.
    public List<Showtime> getAllShowtimes() {
        String sql = "SELECT s.ShowtimeID as showtimeID, s.MovieID as movieID, s.RoomID as roomID, " +
                     "s.StartTime as startTime, s.TicketPrice as ticketPrice, " +
                     "m.Title as movieTitle, r.RoomType as roomType " +
                     "FROM Showtimes s " +
                     "LEFT JOIN Movies m ON s.MovieID = m.MovieID " +
                     "LEFT JOIN Rooms r ON s.RoomID = r.RoomID " +
                     "ORDER BY s.StartTime DESC";
        
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Showtime.class));
    }

    public Showtime getShowtimeById(int id) {
        String sql = "SELECT * FROM Showtimes WHERE ShowtimeID = ?";
        return jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(Showtime.class), id);
    }

    public void updateShowtime(Showtime s) {
        String sql = "UPDATE Showtimes SET MovieID=?, RoomID=?, StartTime=?, TicketPrice=? WHERE ShowtimeID=?";
        jdbcTemplate.update(sql, s.getMovieID(), s.getRoomID(), s.getStartTime(), s.getTicketPrice(), s.getShowtimeID());
    }

    public void deleteShowtime(int id) {
        String sql = "DELETE FROM Showtimes WHERE ShowtimeID = ?";
        jdbcTemplate.update(sql, id);
    }

    // Simple 4: Interogare cu LEFT JOIN si clauza WHERE. Lista difuzarilor pentru un anumit film.
    public List<Showtime> getShowtimesByMovieId(int movieId) {
        String sql = "SELECT s.ShowtimeID as showtimeID, s.MovieID as movieID, s.RoomID as roomID, " +
                "s.StartTime as startTime, s.TicketPrice as ticketPrice, " +
                "m.Title as movieTitle, r.RoomType as roomType " +
                "FROM Showtimes s " +
                "LEFT JOIN Movies m ON s.MovieID = m.MovieID " +
                "LEFT JOIN Rooms r ON s.RoomID = r.RoomID " +
                "WHERE s.MovieID = ? " +
                "ORDER BY s.StartTime DESC";

        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Showtime.class), movieId);
    }
}
