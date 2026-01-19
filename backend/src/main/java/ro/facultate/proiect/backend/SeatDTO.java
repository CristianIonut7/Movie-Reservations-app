package ro.facultate.proiect.backend;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeatDTO {
    private int seatId;
    private int rowNumber;
    private int seatNumber;
    private boolean occupied;
}