package mymovies.dal;

import mymovies.be.Movie;
import mymovies.dal.db.QueryBuilder;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MovieRepository {
    final private QueryBuilder queryBuilder = new QueryBuilder();
    final private Logger logger = Logger.getAnonymousLogger();

    public List<Movie> getAll() {
        List<Movie> movies = new ArrayList<>();

        try (ResultSet rs = queryBuilder
                .select("*")
                .from("movies")
                .get()) {

            while (rs != null && rs.next()) {
                Movie movie = mapModel(rs, rs.getInt("id"));
                movies.add(movie);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }

        return movies;
    }

    public List<Movie> getAllWithCategories() {
        List<Movie> movies = new ArrayList<>();

        try (ResultSet rs = queryBuilder
                .select("movies.id as movie_id", "movies.name", "movies.file_path", "movies.imdb_rating", "movies.personal_rating", "movies.last_view", "movies.created_at", "movies.updated_at", "STRING_AGG(categories.name, ', ') AS categories")
                .from("movies")
                .join("category_movie", "movies.id = category_movie.movie_id", "INNER")
                .join("categories", "category_movie.category_id = categories.id", "INNER")
                .groupBy("movies.id", "movies.name", "movies.imdb_rating", "movies.personal_rating", "movies.last_view", "movies.created_at", "movies.updated_at", "movies.file_path")
                .get()) {

            while (rs != null && rs.next()) {
                Movie movie = mapModel(rs, rs.getInt("movie_id"));
                movie.setCategories(rs.getString("categories"));
                movies.add(movie);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }

        return movies;
    }

    public List<Movie> filterAllMoviesWithCategories(String name, Integer imdbRating, Integer personalRating, List<Integer> categoryIds) {
        List<Movie> movies = new ArrayList<>();

        try (ResultSet rs = queryBuilder
                .select("movies.id as movie_id", "movies.name", "movies.file_path", "movies.imdb_rating", "movies.personal_rating", "movies.last_view", "movies.created_at", "movies.updated_at", "STRING_AGG(categories.name, ', ') AS categories")
                .from("movies")
                .where("movies.name", "like", "%" + name + "%")
                .where("imdb_rating", ">=", imdbRating)
                .where("personal_rating", ">=", personalRating)
                .join("category_movie", "movies.id = category_movie.movie_id", "INNER")
                .join("categories", "category_movie.category_id = categories.id", "INNER")
                .groupBy("movies.id", "movies.name", "movies.imdb_rating", "movies.personal_rating", "movies.last_view", "movies.created_at", "movies.updated_at", "movies.file_path")
                .whereInSubquery(
                        "movies.id",
                        "SELECT category_movie.movie_id FROM category_movie WHERE category_movie.category_id IN (" +
                                String.join(", ", Collections.nCopies(categoryIds.size(), "?")) + ")",
                        categoryIds
                )
                .get()) {

            while (rs != null && rs.next()) {
                Movie movie = mapModel(rs, rs.getInt("movie_id"));
                movie.setCategories(rs.getString("categories"));
                movies.add(movie);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }

        return movies;
    }

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

    public List<Movie> getLowRatedOlderMovies() {
        List<Movie> movies = new ArrayList<>();
        try (ResultSet rs = queryBuilder
                .select("*")
                .from("movies")
                .where("last_view", "<", LocalDateTime.now().minusMonths(24))
                .where("personal_rating", "<", 6)
                .get()) {

            while (rs != null && rs.next()) {
                Movie movie = mapModel(rs, rs.getInt("id"));
                movies.add(movie);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
        return movies;
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

    public void update(Movie movie) {
        queryBuilder
                .table("movies")
                .set("name", movie.getName())
                .set("imdb_rating", movie.getImdbRating())
                .set("file_path", movie.getFilePath())
                .set("personal_rating", movie.getPersonalRating())
                .where("id", "=", movie.getId())
                .update();
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

    public void removeAllCategories(Movie movie) {
        queryBuilder
                .table("category_movie")
                .where("movie_id", "=", movie.getId())
                .delete();
    }

    public void rateMovie(Movie movie){
        queryBuilder
                .table("movies")
                .set("personal_rating", movie.getPersonalRating())
                .where("id", "=", movie.getId())
                .update();
    }

    public void markAsViewed(int id) {
        queryBuilder
                .table("movies")
                .set("last_view", LocalDateTime.now())
                .where("id", "=", id)
                .update();
    }

    private Movie mapModel(ResultSet resultSet, int id) throws SQLException {
        String name = resultSet.getString("name");
        Integer imdbRating = resultSet.getObject("imdb_rating", Integer.class);
        String filePath = resultSet.getString("file_path");
        Integer personalRating = resultSet.getObject("personal_rating", Integer.class);
        LocalDateTime lastView = resultSet.getTimestamp("last_view") != null ? resultSet.getTimestamp("last_view").toLocalDateTime() : null;
        LocalDateTime createdAt = resultSet.getTimestamp("created_at").toLocalDateTime();
        LocalDateTime updatedAt = resultSet.getTimestamp("updated_at").toLocalDateTime();

        return new Movie(id, name, imdbRating, filePath, lastView, createdAt, updatedAt, personalRating);
    }
}
