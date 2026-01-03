package ro.facultate.proiect.backend;

import lombok.Data;

@Data
public class SeatDTO {
    private int seatId;
    private int rowNumber;
    private int seatNumber;
    private boolean occupied; // true dacă locul e deja rezervat pentru acest showtime
}