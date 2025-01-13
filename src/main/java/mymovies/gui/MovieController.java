package mymovies.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import mymovies.be.Movie;
import mymovies.dal.db.QueryBuilder;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MovieController {

    @FXML
    private TextField searchTitle;

    @FXML
    private ListView<String> movieListView;

    @FXML
    private ComboBox<String> categoryComboBox;

    @FXML
    private Slider ratingSlider;

    @FXML
    private Label ratingLabel;

    @FXML
    public void initialize() {
        loadCategories(); //load categories in the ComboBox
        List<Movie> allMovies = fetchMoviesWithRatings();
        displayMoviesWithRatings(allMovies); //
    }

    private List<Movie> fetchMoviesWithRatings() {
        List<Movie> movies = new ArrayList<>();
        QueryBuilder queryBuilder = new QueryBuilder();

        try (ResultSet rs = queryBuilder
                .select("id, name, rating, file_link, last_view, created_at, updated_at, personal_rating")
                .from("movies")
                .get()) {

            while (rs != null && rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                double rating = rs.getDouble("rating");
                String fileLink = rs.getString("file_link");
                LocalDateTime lastView = rs.getTimestamp("last_view").toLocalDateTime();
                LocalDateTime createdAt = rs.getTimestamp("created_at").toLocalDateTime();
                LocalDateTime updatedAt = rs.getTimestamp("updated_at").toLocalDateTime();
                double personalRating = rs.getDouble("personal_rating");

                movies.add(new Movie(id, name, rating, fileLink, lastView, createdAt, updatedAt, personalRating));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return movies;
    }

    private void displayMoviesWithRatings(List<Movie> movies) {
        List<String> formattedMovies = movies.stream()
                .map(movie -> String.format("%s - IMDB: %.1f | Personal: %.1f",
                        movie.getName(),
                        movie.getRating(),
                        movie.getPersonalRating()))
                .toList();

        movieListView.getItems().setAll(formattedMovies);
    }

    public void loadCategories() {
        List<String> categories = new ArrayList<>();
        QueryBuilder queryBuilder = new QueryBuilder();

        try (ResultSet rs = queryBuilder
                .select("name")
                .from("categories")
                .get()) {

            while (rs != null && rs.next()) {
                categories.add(rs.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        categoryComboBox.getItems().setAll(categories);
    }

    public List<Movie> fetchMoviesByCategory(String category) {
        List<Movie> movies = new ArrayList<>();
        QueryBuilder queryBuilder = new QueryBuilder();

        try (ResultSet rs = queryBuilder
                .select("m.*")
                .from("movies m")
                .innerJoin("category_movie cm", "m.id = cm.movie_id")
                .innerJoin("categories c", "c.id = cm.category_id")
                .where("c.name", "=", category)
                .get()) {

            while (rs != null && rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                double rating = rs.getDouble("rating");
                String fileLink = rs.getString("file_link");
                LocalDateTime lastView = rs.getTimestamp("last_view").toLocalDateTime();
                LocalDateTime createdAt = rs.getTimestamp("created_at").toLocalDateTime();
                LocalDateTime updatedAt = rs.getTimestamp("updated_at").toLocalDateTime();
                double personalRating = rs.getDouble("personal_rating");

                movies.add(new Movie(id, name, rating, fileLink, lastView, createdAt, updatedAt, personalRating));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return movies;
    }

    //method to show movies in the listView by category
    public void displayMoviesByCategory(String category) {
        List<Movie> movies = fetchMoviesByCategory(category);
        List<String> formattedMovies = movies.stream()
                .map(movie -> String.format("%s - IMDB: %.1f | Personal: %.1f",
                        movie.getName(),
                        movie.getRating(),
                        movie.getPersonalRating()))
                .toList();

        movieListView.getItems().setAll(formattedMovies);
    }

    public void onCategorySelected(ActionEvent event) {
        String selectedCategory = categoryComboBox.getValue();
        if (selectedCategory != null) {
            displayMoviesByCategory(selectedCategory);
        }
    }

    public void onSearchKeyReleased(KeyEvent keyEvent) {
        String searchText = searchTitle.getText().trim();
        if (!searchText.isEmpty()) {
            searchMoviesByTitle(searchText);
        } else {
            movieListView.getItems().clear(); //if the searchTitle is empty, clear the ListView or show all movies
        }
    }

    private void searchMoviesByTitle(String title) {
        List<Movie> movies = fetchMoviesByTitle(title);
        List<String> formattedMovies = movies.stream()
                .map(movie -> String.format("%s - IMDB: %.1f | Personal: %.1f",
                        movie.getName(),
                        movie.getRating(),
                        movie.getPersonalRating()))
                .toList();

        movieListView.getItems().setAll(formattedMovies);
    }

    private List<Movie> fetchMoviesByTitle(String title) {
        List<Movie> movies = new ArrayList<>();
        QueryBuilder queryBuilder = new QueryBuilder();

        try (ResultSet rs = queryBuilder
                .select("*")
                .from("movies")
                .where("name", "LIKE", "%" + title + "%")
                .get()) {

            while (rs != null && rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                double rating = rs.getDouble("rating");
                String fileLink = rs.getString("file_link");
                LocalDateTime lastView = rs.getTimestamp("last_view").toLocalDateTime();
                LocalDateTime createdAt = rs.getTimestamp("created_at").toLocalDateTime();
                LocalDateTime updatedAt = rs.getTimestamp("updated_at").toLocalDateTime();
                double personalRating = rs.getDouble("personal_rating");

                movies.add(new Movie(id, name, rating, fileLink, lastView, createdAt, updatedAt, personalRating));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return movies;
    }

    public void onRatingSliderChanged(MouseEvent event) {
        double minRating = ratingSlider.getValue();
        ratingLabel.setText(String.format("%.1f", minRating)); //modify the value in the ratingLabel
        filterMoviesByRating(minRating); //filter movies based on min rating
    }

    private void filterMoviesByRating(double minRating) {
        List<Movie> movies = fetchMoviesByRating(minRating);
        List<String> formattedMovies = movies.stream()
                .map(movie -> String.format("%s - IMDB: %.1f | Personal: %.1f",
                        movie.getName(),
                        movie.getRating(),
                        movie.getPersonalRating()))
                .toList();

        movieListView.getItems().setAll(formattedMovies);
    }

    private List<Movie> fetchMoviesByRating(double minRating) {
        List<Movie> movies = new ArrayList<>();
        QueryBuilder queryBuilder = new QueryBuilder();

        try (ResultSet rs = queryBuilder
                .select("*")
                .from("movies")
                .where("rating", ">=", minRating)
                .get()) {

            while (rs != null && rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                double rating = rs.getDouble("rating");
                String fileLink = rs.getString("file_link");
                LocalDateTime lastView = rs.getTimestamp("last_view").toLocalDateTime();
                LocalDateTime createdAt = rs.getTimestamp("created_at").toLocalDateTime();
                LocalDateTime updatedAt = rs.getTimestamp("updated_at").toLocalDateTime();
                double personalRating = rs.getDouble("personal_rating");

                movies.add(new Movie(id, name, rating, fileLink, lastView, createdAt, updatedAt, personalRating));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return movies;
    }
}