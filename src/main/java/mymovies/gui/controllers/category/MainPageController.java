package mymovies.gui.controllers.category;

import javafx.fxml.FXML;
import mymovies.dal.db.QueryBuilder;

import javax.swing.table.TableColumn;
import javax.swing.text.TableView;
import java.awt.*;
import java.sql.ResultSet;
import mymovies.be.Movies;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MainPageController {
    @FXML
    private TableView movieList;
    @FXML
    private TableColumn movieColumn;
    @FXML
    private TableColumn categoryColumn;
    @FXML
    private TableColumn ratingColumn;
    @FXML
    private TableColumn linkColumn;
    @FXML
    private TableColumn lastVIewColumn;
    @FXML
    private Button addMovieBtn;
    @FXML
    private Button removeMovieBtn;
    @FXML
    private Button addCategoryBtn;
    @FXML
    private Button removeCategoryBtn;
    @FXML
    private Label currentMovieLbl;


    @FXML
    public void initialize() {
        List<Movies> allMovies = fetchMoviesWithRatings();
        displayMoviesWithRatings(allMovies); //
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
