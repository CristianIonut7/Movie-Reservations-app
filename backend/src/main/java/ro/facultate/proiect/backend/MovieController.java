package ro.facultate.proiect.backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/movies")
public class MovieController {

    @Autowired
    private MovieRepository movieRepository;

    // --- CRUD Endpoints ---

    @GetMapping
    public List<Movie> getAllMovies() {
        return movieRepository.getAllMovies();
    }

    @GetMapping("/{id}")
    public Movie getMovieById(@PathVariable int id) {
        return movieRepository.getMovieById(id);
    }

    @PostMapping
    public String addMovie(@RequestBody Movie movie) {
        movieRepository.addMovie(movie);
        return "Movie added successfully";
    }

    @PutMapping("/{id}")
    public String updateMovie(@PathVariable int id, @RequestBody Movie movie) {
        movie.setMovieID(id);
        movieRepository.updateMovie(movie);
        return "Movie updated successfully";
    }

    @DeleteMapping("/{id}")
    public String deleteMovie(@PathVariable int id) {
        movieRepository.deleteMovie(id);
        return "Movie deleted successfully";
    }

    // --- Existing SHOWTIME Endpoint (Combined) ---
    @GetMapping("/showtimes")
    public List<ShowtimeDTO> getShowtimes() {
        return movieRepository.getAllShowtimes();
    }

    // --- Search Endpoint ---
    @GetMapping("/search")
    public List<Movie> searchMovies(@RequestParam(required = false) String query, @RequestParam(required = false) String genre) {
        return movieRepository.searchMovies(query, genre);
    }
}