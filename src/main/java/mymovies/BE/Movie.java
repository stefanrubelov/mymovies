package mymovies.be;

import java.time.LocalDateTime;

public class Movie {
    private int id;
    private String name;
    private Integer imdbRating;
    private String filePath;
    private LocalDateTime lastView;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer personalRating;

    public Movie(int id, String name, Integer imdbRating, String filePath, LocalDateTime lastView, LocalDateTime createdAt, LocalDateTime updatedAt, Integer personalRating) {
        this.id = id;
        this.name = name;
        this.imdbRating = imdbRating;
        this.filePath = filePath;
        this.lastView = lastView;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.personalRating = personalRating;
    }
    public Movie(int id, String name, String filePath, Integer imdbRating, Integer personalRating) {
        this.id = id;
        this.name = name;
        this.filePath = filePath;
        this.imdbRating = imdbRating;
        this.personalRating = personalRating;
    }
    public Movie(String name, String filePath, Integer imdbRating, Integer personalRating) {
        this.name = name;
        this.filePath = filePath;
        this.imdbRating = imdbRating;
        this.personalRating = personalRating;
    }

    public Movie(int id){
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Integer getImdbRating() {
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

    public Integer getPersonalRating() {
        return personalRating;
    }
}
