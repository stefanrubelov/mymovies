package mymovies.gui.controllers.category;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import mymovies.be.Movie;
import mymovies.dal.db.QueryBuilder;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MainPageController {

    @FXML
    private TableView<Movie> movieTableView;
    @FXML
    private TableColumn<Movie, Integer> idColumn;
    @FXML
    private TableColumn<Movie, String> nameColumn;
    @FXML
    private TableColumn<Movie, Double> ratingColumn;
    @FXML
    private TableColumn<Movie, String> filePathColumn;
    @FXML
    private TableColumn<Movie, LocalDateTime> lastViewColumn;
    @FXML
    private TableColumn<Movie, LocalDateTime> createdAtColumn;
    @FXML
    private TableColumn<Movie, LocalDateTime> updatedAtColumn;
    @FXML
    private TableColumn<Movie, Double> personalRatingColumn;
    @FXML
    private Button addMovieBtn;
    @FXML
    private Button removeMovieBtn;
    @FXML
    private Button addCategoryBtn;
    @FXML
    private Button removeCategoryBtn;
    @FXML
    private Pagination pagination;

    private static final int ROWS_PER_PAGE = 5;
    private ObservableList<Movie> movieList = FXCollections.observableArrayList();


    @FXML
    public void initialize() {
        configureTableView();
        loadMoviesIntoTableView();
    }

    private void configureTableView() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        ratingColumn.setCellValueFactory(new PropertyValueFactory<>("rating"));
        filePathColumn.setCellValueFactory(new PropertyValueFactory<>("filePath"));
        lastViewColumn.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getLastView()));
        createdAtColumn.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getCreatedAt()));
        updatedAtColumn.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getUpdatedAt()));
        personalRatingColumn.setCellValueFactory(new PropertyValueFactory<>("personalRating"));

    }


    private void configurePagination() {
        int totalPages = (int) Math.ceil((double) movieList.size() / ROWS_PER_PAGE);
        pagination.setPageCount(totalPages);

        pagination.setPageFactory(pageIndex -> {
            return createPage(pageIndex);
        });
    }


    private Node createPage(int pageIndex) {
        int start = pageIndex * ROWS_PER_PAGE;
        int end = Math.min(start + ROWS_PER_PAGE, movieList.size());

        ObservableList<Movie> currentPageData = FXCollections.observableArrayList(movieList.subList(start, end));
        movieTableView.setItems(currentPageData);

        return new AnchorPane(movieTableView);

    }



    private void loadMoviesIntoTableView() {
        List<Movie> movies = fetchMovies();
        if (movies.isEmpty()) {
            System.out.println("No movies found in the database!");
        }

        movieList.setAll(movies);

        configurePagination();
    }


    private List<Movie> fetchMovies() {
        List<Movie> movies = new ArrayList<>();
        QueryBuilder queryBuilder = new QueryBuilder();

        try (ResultSet rs = queryBuilder
                .select("id, name, rating, file_path, last_view, created_at, updated_at, personal_rating")
                .from("movies")
                .get()) {

            while (rs != null && rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                double rating = rs.getDouble("rating");
                String filePath = rs.getString("file_path");
                LocalDateTime lastView = rs.getTimestamp("last_view").toLocalDateTime();
                LocalDateTime createdAt = rs.getTimestamp("created_at").toLocalDateTime();
                LocalDateTime updatedAt = rs.getTimestamp("updated_at").toLocalDateTime();
                double personalRating = rs.getDouble("personal_rating");

                movies.add(new Movie(id, name, rating, filePath, lastView, createdAt, updatedAt, personalRating));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return movies;
    }
}
