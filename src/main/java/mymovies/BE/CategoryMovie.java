package mymovies.BE;

import java.time.LocalDateTime;

public class CategoryMovie {
    private int id;
    private int categoryId;
    private int movieId;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public CategoryMovie(int id, int categoryId, int movieId, LocalDateTime createTime, LocalDateTime updateTime) {
        this.id = id;
        this.categoryId = categoryId;
        this.movieId = movieId;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

    public int getId() {
        return id;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public int getMovieId() {
        return movieId;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
}
