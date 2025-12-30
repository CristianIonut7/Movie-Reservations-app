package ro.facultate.proiect.backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class MovieRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<ShowtimeDTO> getAllShowtimes() {
        // Interogare simplă (JOIN) între 3 tabele 
        String sql = "SELECT m.Title as movieTitle, m.Genre, r.RoomType, s.StartTime, s.TicketPrice " +
                     "FROM Showtimes s " +
                     "JOIN Movies m ON s.MovieID = m.MovieID " +
                     "JOIN Rooms r ON s.RoomID = r.RoomID";

        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(ShowtimeDTO.class));
    }
}