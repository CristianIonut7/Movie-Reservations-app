package ro.facultate.proiect.backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/movies")
public class MovieController {

    @Autowired
    private MovieRepository movieRepository;

    @GetMapping("/showtimes")
    public List<ShowtimeDTO> getShowtimes() {
        return movieRepository.getAllShowtimes();
    }
}