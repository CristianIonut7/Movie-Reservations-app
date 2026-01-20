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

    public void addMovie(Movie movie) {
        String sql = "INSERT INTO Movies (Title, Description, Genre, DurationMinutes, ReleaseDate, MinAge, DirectorFirstName, DirectorLastName) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, movie.getTitle(), movie.getDescription(), movie.getGenre(), movie.getDurationMinutes(), 
                            movie.getReleaseDate(), movie.getMinAge(), movie.getDirectorFirstName(), movie.getDirectorLastName());
    }

    public List<Movie> getAllMovies() {
        String sql = "SELECT * FROM Movies";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Movie.class));
    }

    public Movie getMovieById(int id) {
        String sql = "SELECT * FROM Movies WHERE MovieID = ?";
        return jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(Movie.class), id);
    }

    public void updateMovie(Movie movie) {
        String sql = "UPDATE Movies SET Title=?, Description=?, Genre=?, DurationMinutes=?, ReleaseDate=?, MinAge=?, " +
                     "DirectorFirstName=?, DirectorLastName=? WHERE MovieID=?";
        jdbcTemplate.update(sql, movie.getTitle(), movie.getDescription(), movie.getGenre(), movie.getDurationMinutes(), 
                            movie.getReleaseDate(), movie.getMinAge(), movie.getDirectorFirstName(), movie.getDirectorLastName(), movie.getMovieID());
    }

    public void deleteMovie(int id) {
        String sql = "DELETE FROM Movies WHERE MovieID = ?";
        jdbcTemplate.update(sql, id);
    }

    public List<Movie> searchMovies(String query, String genre) {
        StringBuilder sql = new StringBuilder("SELECT * FROM Movies WHERE 1=1");
        java.util.List<Object> params = new java.util.ArrayList<>();

        if (query != null && !query.trim().isEmpty()) {
            sql.append(" AND (Title LIKE ? OR DirectorFirstName LIKE ? OR DirectorLastName LIKE ? OR Description LIKE ?)");
            String likeQuery = "%" + query.trim() + "%";
            params.add(likeQuery);
            params.add(likeQuery);
            params.add(likeQuery);
            params.add(likeQuery);
        }

        if (genre != null && !genre.trim().isEmpty()) {
            sql.append(" AND Genre = ?");
            params.add(genre.trim());
        }

        return jdbcTemplate.query(sql.toString(), new BeanPropertyRowMapper<>(Movie.class), params.toArray());
    }
}