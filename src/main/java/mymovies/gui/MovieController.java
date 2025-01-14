package mymovies.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import mymovies.be.Movie;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import mymovies.dal.db.QueryBuilder;

import java.awt.*;
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
        setupMovieCellFactory(); // to add stars to ratings
        List<Movie> allMovies = fetchMoviesWithRatings();
        displayMoviesWithRatings(allMovies); //
    }

    private List<Movie> fetchMoviesWithRatings() {
        List<Movie> movies = new ArrayList<>();
        QueryBuilder queryBuilder = new QueryBuilder();

        try (ResultSet rs = queryBuilder
                .select("id, name, rating, file_path, last_view, created_at, updated_at, personal_rating")
                .from("movies")
                .get()) {

            while (rs != null && rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                int rating = rs.getInt("rating");
                String fileLink = rs.getString("file_path");
                LocalDateTime lastView = rs.getTimestamp("last_view").toLocalDateTime();
                LocalDateTime createdAt = rs.getTimestamp("created_at").toLocalDateTime();
                LocalDateTime updatedAt = rs.getTimestamp("updated_at").toLocalDateTime();
                int personalRating = rs.getInt("personal_rating");

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
                        movie.getImdbRating(),
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
                int rating = rs.getInt("rating");
                String fileLink = rs.getString("file_path");
                LocalDateTime lastView = rs.getTimestamp("last_view").toLocalDateTime();
                LocalDateTime createdAt = rs.getTimestamp("created_at").toLocalDateTime();
                LocalDateTime updatedAt = rs.getTimestamp("updated_at").toLocalDateTime();
                int personalRating = rs.getInt("personal_rating");

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
                        movie.getImdbRating(),
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
                        movie.getImdbRating(),
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
                int rating = rs.getInt("rating");
                String fileLink = rs.getString("file_path");
                LocalDateTime lastView = rs.getTimestamp("last_view").toLocalDateTime();
                LocalDateTime createdAt = rs.getTimestamp("created_at").toLocalDateTime();
                LocalDateTime updatedAt = rs.getTimestamp("updated_at").toLocalDateTime();
                int personalRating = rs.getInt("personal_rating");

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
                        movie.getImdbRating(),
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
                int rating = rs.getInt("rating");
                String fileLink = rs.getString("file_path");
                LocalDateTime lastView = rs.getTimestamp("last_view").toLocalDateTime();
                LocalDateTime createdAt = rs.getTimestamp("created_at").toLocalDateTime();
                LocalDateTime updatedAt = rs.getTimestamp("updated_at").toLocalDateTime();
                int personalRating = rs.getInt("personal_rating");

                movies.add(new Movie(id, name, rating, fileLink, lastView, createdAt, updatedAt, personalRating));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return movies;
    }
    //Modify the ListView with a personalized cell
    private void setupMovieCellFactory(){
        movieListView.setCellFactory(listView ->new ListCell<>(){
            @Override
            protected void updateItem(String formattedMovie, boolean empty) {
                super.updateItem(formattedMovie, empty);
                if (empty || formattedMovie == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    HBox hbox = new HBox(10);

                    String[] parts = formattedMovie.split(" - IMDB:");
                    String titleText = parts[0];
                    double rating = Double.parseDouble(parts[1].split("\\|")[0].trim());

                    Text title = new Text(titleText);


                    HBox starsBox = new HBox(2); //space between stars
                    int starsCount = (int) Math.floor(rating); // number of full stars
                    int maxStars = 5;

                    Image star = new Image ("file:resources/star.png");

                    for (int i = 0; i < maxStars; i++) {
                        ImageView starView = null;
                        if (i < starsCount) {
                            starView = new ImageView(star);
                        } else {


                        }
                        starView.setFitHeight(16);
                        starView.setFitWidth(16);
                        starsBox.getChildren().add(starView);
                    }

                    hbox.getChildren().addAll(title, starsBox);
                    setGraphic(hbox);
                }
            }
        });
    }
}