package mymovies.be;

import java.time.LocalDateTime;

public class Movies {
    private int id;
    private String name;
    private double rating;
    private String fileLink;
    private LocalDateTime lastView;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private double personalRating;

    //Constructor
    public Movies(int id, String name, double rating, String fileLink, LocalDateTime lastView, LocalDateTime createdAt, LocalDateTime updatedAt, double personalRating) {
        this.id = id;
        this.name = name;
        this.rating = rating;
        this.fileLink = fileLink;
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

    public double getRating() {
        return rating;
    }

    public String getFileLink() {
        return fileLink;
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
