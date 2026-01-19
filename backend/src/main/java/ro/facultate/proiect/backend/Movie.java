package ro.facultate.proiect.backend;

import java.sql.Date;

public class Movie {
    private int movieID;
    private String title;
    private String description;
    private String genre;
    private int durationMinutes;
    private Date releaseDate;
    private int minAge;
    private String directorFirstName;
    private String directorLastName;

    public int getMovieID() { return movieID; }
    public void setMovieID(int movieID) { this.movieID = movieID; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }

    public int getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(int durationMinutes) { this.durationMinutes = durationMinutes; }

    public Date getReleaseDate() { return releaseDate; }
    public void setReleaseDate(Date releaseDate) { this.releaseDate = releaseDate; }

    public int getMinAge() { return minAge; }
    public void setMinAge(int minAge) { this.minAge = minAge; }

    public String getDirectorFirstName() { return directorFirstName; }
    public void setDirectorFirstName(String directorFirstName) { this.directorFirstName = directorFirstName; }

    public String getDirectorLastName() { return directorLastName; }
    public void setDirectorLastName(String directorLastName) { this.directorLastName = directorLastName; }
}
