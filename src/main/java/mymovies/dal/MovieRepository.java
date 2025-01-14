package mymovies.dal;

import mymovies.be.Movie;
import mymovies.dal.db.QueryBuilder;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MovieRepository {
    final private QueryBuilder queryBuilder = new QueryBuilder();
    final private Logger logger = Logger.getAnonymousLogger();

    public Movie findById(int id) {
        Movie result = null;
        ResultSet resultSet = queryBuilder
                .from("movies")
                .select("*")
                .where("id", "=", id)
                .get();

        try {
            while (resultSet.next()) {
                result = mapModel(resultSet, resultSet.getInt("id"));
            }

            return result;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);

            return result;
        }
    }

    public Movie create(Movie movie) {
        Movie newMovie = null;
        ResultSet resultSet = queryBuilder.table("movies")
                .insert("name", movie.getName())
                .insert("imdb_rating", movie.getImdbRating())
                .insert("file_path", movie.getFilePath())
                .insert("personal_rating", movie.getPersonalRating())
                .saveAndReturn();

        try {
            while (resultSet.next()) {
                newMovie = new Movie(resultSet.getInt("id"));

                return newMovie;
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
        return newMovie;
    }

    public void delete(int id) {
        queryBuilder
                .table("movies")
                .where("id", "=", id)
                .delete();
    }

    public void addCategory(Movie movie, int category_id) {
        queryBuilder.table("category_movie")
                .insert("movie_id", movie.getId())
                .insert("category_id", category_id)
                .save();
    }

    private Movie mapModel(ResultSet resultSet, int id) throws SQLException {

        String name = resultSet.getString("name");
        Integer imdbRating = resultSet.getObject("imdb_rating", Integer.class); // Handles nullable values
        String filePath = resultSet.getString("file_path");
        Integer personalRating = resultSet.getObject("personal_rating", Integer.class); // Handles nullable values

        return new Movie(id, name, filePath, imdbRating, personalRating);
    }
}
