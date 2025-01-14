package mymovies.be;

import java.time.LocalDateTime;

public class Movie {
    private int id;
    private String name;
    private double imdbRating;
    private String filePath;
    private LocalDateTime lastView;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private double personalRating;

    //Constructor
    public Movie(int id, String name, double imdbRating, String filePath, LocalDateTime lastView, LocalDateTime createdAt, LocalDateTime updatedAt, double personalRating) {
        this.id = id;
        this.name = name;
        this.imdbRating = imdbRating;
        this.filePath = filePath;
        this.lastView = lastView;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.personalRating = personalRating;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getImdbRating() {
        return imdbRating;
    }

    public String getFilePath() {
        return filePath;
    }

    public LocalDateTime getLastView() {
        return lastView;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public double getPersonalRating() {
        return personalRating;
    }
}
