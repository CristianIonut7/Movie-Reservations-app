package ro.facultate.proiect.backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:4200") // Allow Angular frontend
public class AdminDashboardController {

    @Autowired
    private AdminRepository adminRepository;

    @GetMapping("/top-movies")
    public List<Map<String, Object>> getTopMovies() {
        return adminRepository.getTopMovies();
    }

    @GetMapping("/vip-clients")
    public List<Map<String, Object>> getVipClients() {
        return adminRepository.getVipClients();
    }

    @GetMapping("/movies-without-bookings")
    public List<Map<String, Object>> getMoviesWithoutBookings() {
        return adminRepository.getMoviesWithoutBookings();
    }

    @GetMapping("/revenue")
    public List<Map<String, Object>> getRevenuePerMovie() {
        return adminRepository.getRevenuePerMovie();
    }

    @GetMapping("/genre-stats")
    public List<Map<String, Object>> getGenreStats() {
        return adminRepository.getGenreStats();
    }

    @PostMapping("/promote")
    public String promoteUser(@RequestParam String email) {
        int rows = adminRepository.promoteToAdmin(email);
        return rows > 0 ? "User promoted successfully" : "User not found";
    }
}
