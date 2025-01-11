package mymovies.gui.controllers.category;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import mymovies.be.Movies;
import mymovies.dal.db.QueryBuilder;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MainPageController {

    @FXML
    private TableView<Movies> movieTableView;

    @FXML
    private TableColumn<Movies, Integer> idColumn;
    @FXML
    private TableColumn<Movies, String> nameColumn;
    @FXML
    private TableColumn<Movies, Double> ratingColumn;
    @FXML
    private TableColumn<Movies, String> fileLinkColumn;
    @FXML
    private TableColumn<Movies, LocalDateTime> lastViewColumn;
    @FXML
    private TableColumn<Movies, LocalDateTime> createdAtColumn;
    @FXML
    private TableColumn<Movies, LocalDateTime> updatedAtColumn;
    @FXML
    private TableColumn<Movies, Double> personalRatingColumn;

    @FXML
    public void initialize() {
        configureTableView(); // Set up the TableView columns
        loadMoviesIntoTableView(); // Load data into the TableView
    }

    private void configureTableView() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        ratingColumn.setCellValueFactory(new PropertyValueFactory<>("rating"));
        fileLinkColumn.setCellValueFactory(new PropertyValueFactory<>("fileLink"));
        lastViewColumn.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getLastView()));
        createdAtColumn.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getCreatedAt()));
        updatedAtColumn.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getUpdatedAt()));
        personalRatingColumn.setCellValueFactory(new PropertyValueFactory<>("personalRating"));
    }

    private void loadMoviesIntoTableView() {
        List<Movies> moviesList = fetchMovies();
        if (moviesList.isEmpty()) {
            System.out.println("No movies found in the database!");
        }
        ObservableList<Movies> observableMovies = FXCollections.observableArrayList(moviesList);
        movieTableView.setItems(observableMovies);
    }


    private List<Movies> fetchMovies() {
        List<Movies> movies = new ArrayList<>();
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

                movies.add(new Movies(id, name, rating, fileLink, lastView, createdAt, updatedAt, personalRating));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return movies;
    }
}
