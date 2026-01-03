package ro.facultate.proiect.backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    @Autowired
    private BookingRepository bookingRepository;

    @GetMapping("/seats/{showtimeId}")
    public List<SeatDTO> getSeats(@PathVariable int showtimeId) {
        return bookingRepository.getSeatsStatus(showtimeId);
    }

    @PostMapping("/reserve")
    public ResponseEntity<String> reserve(@RequestBody Map<String, Object> payload) {
        try {
            int userId = (int) payload.get("userId");
            int showtimeId = (int) payload.get("showtimeId");
            List<Integer> seatIds = (List<Integer>) payload.get("seatIds");

            bookingRepository.createBooking(userId, showtimeId, seatIds);
            return ResponseEntity.ok("Rezervare finalizată cu succes!");
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Eroare la rezervare: " + e.getMessage());
        }
    }
}