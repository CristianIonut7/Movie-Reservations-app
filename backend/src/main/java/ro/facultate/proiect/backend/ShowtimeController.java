package ro.facultate.proiect.backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/showtimes")
@CrossOrigin(origins = "http://localhost:4200")
public class ShowtimeController {

    @Autowired
    private ShowtimeRepository showtimeRepository;

    @GetMapping
    public List<Showtime> getAllShowtimes() {
        return showtimeRepository.getAllShowtimes();
    }

    @GetMapping("/{id}")
    public Showtime getShowtimeById(@PathVariable int id) {
        return showtimeRepository.getShowtimeById(id);
    }

    @PostMapping
    public String addShowtime(@RequestBody Showtime showtime) {
        showtimeRepository.addShowtime(showtime);
        return "Showtime added successfully";
    }

    @PutMapping("/{id}")
    public String updateShowtime(@PathVariable int id, @RequestBody Showtime showtime) {
        showtime.setShowtimeID(id);
        showtimeRepository.updateShowtime(showtime);
        return "Showtime updated successfully";
    }

    @DeleteMapping("/{id}")
    public String deleteShowtime(@PathVariable int id) {
        showtimeRepository.deleteShowtime(id);
        return "Showtime deleted successfully";
    }
}
