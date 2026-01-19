package ro.facultate.proiect.backend;

import java.sql.Timestamp;
import java.math.BigDecimal;

public class Showtime {
    private int showtimeID;
    private int movieID;
    private int roomID;
    private Timestamp startTime;
    private BigDecimal ticketPrice;
    
    // Extra fields for display (joins)
    private String movieTitle;
    private String roomType;

    // Getters and Setters
    public int getShowtimeID() { return showtimeID; }
    public void setShowtimeID(int showtimeID) { this.showtimeID = showtimeID; }

    public int getMovieID() { return movieID; }
    public void setMovieID(int movieID) { this.movieID = movieID; }

    public int getRoomID() { return roomID; }
    public void setRoomID(int roomID) { this.roomID = roomID; }

    public Timestamp getStartTime() { return startTime; }
    public void setStartTime(Timestamp startTime) { this.startTime = startTime; }

    public BigDecimal getTicketPrice() { return ticketPrice; }
    public void setTicketPrice(BigDecimal ticketPrice) { this.ticketPrice = ticketPrice; }

    public String getMovieTitle() { return movieTitle; }
    public void setMovieTitle(String movieTitle) { this.movieTitle = movieTitle; }

    public String getRoomType() { return roomType; }
    public void setRoomType(String roomType) { this.roomType = roomType; }
}
