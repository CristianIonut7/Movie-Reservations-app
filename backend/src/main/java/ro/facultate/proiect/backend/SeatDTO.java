package ro.facultate.proiect.backend;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor // OBLIGATORIU pentru RowMapper
@AllArgsConstructor
public class SeatDTO {
    private int seatId;
    private int rowNumber;
    private int seatNumber;
    private boolean occupied; // true dacă locul e deja rezervat pentru acest showtime
}