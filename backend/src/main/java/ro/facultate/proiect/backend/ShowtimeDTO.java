package ro.facultate.proiect.backend;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ShowtimeDTO {
    private Integer showtimeId;
    private String movieTitle;
    private String genre;
    private String roomType;
    private LocalDateTime startTime;
    private Double ticketPrice;
}