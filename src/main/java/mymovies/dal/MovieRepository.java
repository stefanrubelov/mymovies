package mymovies.dal;

import mymovies.be.Category;
import mymovies.be.Movie;
import mymovies.dal.db.QueryBuilder;

public class MovieRepository {
final private QueryBuilder queryBuilder = new QueryBuilder();

    public void create(Movie movie) {
        queryBuilder.table("categories")
                .insert("name", movie.getName())
                .insert("personal_rating", movie.getPersonalRating())
                .insert("rating", movie.getRating())
                .insert("path", movie.getFileLink())
                .save();
    }
}
