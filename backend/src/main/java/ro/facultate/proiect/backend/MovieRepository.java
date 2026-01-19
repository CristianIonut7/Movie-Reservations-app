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

    // --- CRUD OPERATION ---

    // CREATE (Insert)
    public void addMovie(Movie movie) {
        String sql = "INSERT INTO Movies (Title, Description, Genre, DurationMinutes, ReleaseDate, MinAge, DirectorFirstName, DirectorLastName) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, movie.getTitle(), movie.getDescription(), movie.getGenre(), movie.getDurationMinutes(), 
                            movie.getReleaseDate(), movie.getMinAge(), movie.getDirectorFirstName(), movie.getDirectorLastName());
    }

    // READ (Select All)
    public List<Movie> getAllMovies() {
        String sql = "SELECT * FROM Movies";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Movie.class));
    }

    // READ (Select One)
    public Movie getMovieById(int id) {
        String sql = "SELECT * FROM Movies WHERE MovieID = ?";
        return jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(Movie.class), id);
    }

    // UPDATE
    public void updateMovie(Movie movie) {
        String sql = "UPDATE Movies SET Title=?, Description=?, Genre=?, DurationMinutes=?, ReleaseDate=?, MinAge=?, " +
                     "DirectorFirstName=?, DirectorLastName=? WHERE MovieID=?";
        jdbcTemplate.update(sql, movie.getTitle(), movie.getDescription(), movie.getGenre(), movie.getDurationMinutes(), 
                            movie.getReleaseDate(), movie.getMinAge(), movie.getDirectorFirstName(), movie.getDirectorLastName(), movie.getMovieID());
    }

    // DELETE
    public void deleteMovie(int id) {
        String sql = "DELETE FROM Movies WHERE MovieID = ?";
        jdbcTemplate.update(sql, id);
    }
    
    // --- EXISTING ---

    public List<ShowtimeDTO> getAllShowtimes() {
        String sql = "SELECT s.ShowtimeID AS showtimeId, " +
                "m.Title AS movieTitle, " + 
                "m.Genre AS genre, " +
                "r.RoomType AS roomType, " +
                "s.StartTime AS startTime, " +
                "s.TicketPrice AS ticketPrice, " +
                "s.ShowtimeID AS showtimeId " + 
                "FROM Showtimes s " +
                "JOIN Movies m ON s.MovieID = m.MovieID " +
                "JOIN Rooms r ON s.RoomID = r.RoomID";

        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(ShowtimeDTO.class));
    }
}