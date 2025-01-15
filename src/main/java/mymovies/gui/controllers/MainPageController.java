package mymovies.gui.controllers;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import mymovies.App;
import mymovies.be.Movie;
import mymovies.bll.MovieManager;
import mymovies.gui.PageManager;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MainPageController {
    private final MovieManager movieManager = new MovieManager();

    @FXML
    private TableView<Movie> movieTableView;
    @FXML
    private TableColumn<Movie, Integer> idColumn;
    @FXML
    private TableColumn<Movie, String> nameColumn;
    @FXML
    private TableColumn<Movie, Double> imdbRatingColumn;
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
    private TableColumn<Movie, String> categoriesColumn;
    @FXML
    private Button addNewMovieBtn;
    @FXML
    private Button viewCategoriesBtn;
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
        imdbRatingColumn.setCellValueFactory(new PropertyValueFactory<>("imdbRating"));
        filePathColumn.setCellValueFactory(new PropertyValueFactory<>("filePath"));
        lastViewColumn.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getLastView()));
//        createdAtColumn.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getCreatedAt()));
//        updatedAtColumn.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getUpdatedAt()));
        personalRatingColumn.setCellValueFactory(new PropertyValueFactory<>("personalRating"));
        categoriesColumn.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getCategories()));
    }

    private void configurePagination() {
        int totalPages = (int) Math.ceil((double) movieList.size() / ROWS_PER_PAGE);
        pagination.setPageCount(totalPages);

        pagination.setPageFactory(pageIndex -> createPage(pageIndex));
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
       return movieManager.getAllMoviesWithCategories();
    }

    @FXML
    private void addNewMovieScene(ActionEvent actionEvent){
        PageManager.addMovie(actionEvent);
    }

    @FXML
    private void viewAllCategoriesScene(ActionEvent actionEvent){
        PageManager.allCategories(actionEvent);
    }
}
