package mymovies.gui.controllers;

import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import mymovies.App;
import mymovies.be.Category;
import mymovies.be.Movie;
import mymovies.bll.CategoryManager;
import mymovies.bll.MovieManager;
import mymovies.gui.PageManager;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class HomepageController {
    private final MovieManager movieManager = new MovieManager();
    private final CategoryManager categoryManager = new CategoryManager();

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
    private TableColumn<Movie, Double> personalRatingColumn;
    @FXML
    private TableColumn<Movie, String> categoriesColumn;
    @FXML
    private Slider personalRatingSlider;
    @FXML
    private Slider imdbRatingSlider;
    @FXML
    private ListView<Category> category;
    @FXML
    private TextField movieTitleField;

    private ObservableList<Movie> movieList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        configureTableView();
        fetchMovies();
        fetchCategories();
        configureSliders();
        setUpContextMenu();
        checkForOlderLowRatedMovies();
    }

    private void configureSliders() {
        personalRatingSlider.setMajorTickUnit(1);
        personalRatingSlider.setMinorTickCount(0);
        personalRatingSlider.setSnapToTicks(true);
        imdbRatingSlider.setMajorTickUnit(1);
        imdbRatingSlider.setMinorTickCount(0);
        imdbRatingSlider.setSnapToTicks(true);
    }

    private void configureTableView() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        imdbRatingColumn.setCellValueFactory(new PropertyValueFactory<>("imdbRating"));
        filePathColumn.setCellValueFactory(new PropertyValueFactory<>("filePath"));
        lastViewColumn.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getLastView()));
        personalRatingColumn.setCellValueFactory(new PropertyValueFactory<>("personalRating"));
        categoriesColumn.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getCategories()));
    }

    private void fetchMovies() {
        List<Movie> movies = movieManager.getAllMoviesWithCategories();
        if (movies.isEmpty()) {
            System.out.println("No movies found in the database!");
        }

        movieList.clear();
        movieList.setAll(movies);
        movieTableView.setItems(movieList);
    }

    public void fetchCategories() {
        List<Category> categories = categoryManager.getAllCategories();

        category.setItems(FXCollections.observableArrayList(categories));
        category.getSelectionModel().setSelectionMode(javafx.scene.control.SelectionMode.MULTIPLE);
    }

    private void loadMoviesIntoTable(List<Movie> movies) {
        if (movies.isEmpty()) {
            System.out.println("No movies found in the database!");
        }

        movieList.clear();
        movieList.setAll(movies);
        movieTableView.setItems(movieList);
    }

    public List<Integer> getSelectedCategoryIds() {
        return category.getSelectionModel().getSelectedItems().stream()
                .map(Category::getId)
                .collect(Collectors.toList());
    }

    @FXML
    public void filterMovies() {
        String movieTitle = movieTitleField.getText();
        Integer personalRating = personalRatingSlider.valueProperty().intValue();
        Integer imdbRating = imdbRatingSlider.valueProperty().intValue();
        List<Integer> selectedCategoryIds = getSelectedCategoryIds();

        List<Movie> movies = movieManager.filterAllMoviesWithCategories(movieTitle, personalRating, imdbRating, selectedCategoryIds);
        loadMoviesIntoTable(movies);
    }

    @FXML
    public void resetFilters() {
        fetchMovies();
    }

    @FXML
    private void goToAddNewMovieScene(ActionEvent actionEvent) {
        PageManager.addMovie(actionEvent);
    }

    @FXML
    private void goToViewAllCategoriesScene(ActionEvent actionEvent) {
        PageManager.allCategories(actionEvent);
    }

    private void setUpContextMenu() {
        ContextMenu contextMenu = new ContextMenu();

        MenuItem playMovieMenuItem = new MenuItem("Play");
        MenuItem rateMovieMenuItem = new MenuItem("Rate movie");
        MenuItem editMovieMenuItem = new MenuItem("Edit");
        MenuItem deleteMovieMenuItem = new MenuItem("Delete");

        playMovieMenuItem.setOnAction(_ -> {
            Movie selectedMovie = movieTableView.getSelectionModel().getSelectedItem();
            if (selectedMovie != null) {
                movieManager.playMovie(selectedMovie);
                fetchMovies();
            }
        });

        rateMovieMenuItem.setOnAction(_ -> {
            Movie selectedMovie = movieTableView.getSelectionModel().getSelectedItem();
            if (selectedMovie != null) {
                showRatingDialog(selectedMovie);
            }
        });

        editMovieMenuItem.setOnAction(_ -> {
            Movie selectedMovie = movieTableView.getSelectionModel().getSelectedItem();
            if (selectedMovie != null) {
                PageManager.editMovie(selectedMovie, movieTableView);
            }
        });

        deleteMovieMenuItem.setOnAction(_ -> {
            Movie selectedMovie = movieTableView.getSelectionModel().getSelectedItem();
            if (selectedMovie != null) {
                movieManager.deleteMovie(selectedMovie);
                ObservableList<Movie> currentItems = movieTableView.getItems();
                currentItems.remove(selectedMovie);
            }
        });

        contextMenu.getItems().addAll(playMovieMenuItem, rateMovieMenuItem, editMovieMenuItem, deleteMovieMenuItem);

        movieTableView.setRowFactory(_ -> {
            TableRow<Movie> row = new TableRow<>();
            row.setContextMenu(contextMenu);
            return row;
        });
    }

    public void checkForOlderLowRatedMovies() {
        if (!App.hasCheckedForOlderMovies() && movieManager.checkForLowRatedOlderMovies()) {
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Reminder");
                alert.setHeaderText(null);
                alert.setContentText("You have movies with rating lower than 6 that you haven't opened in more than 2 years.");
                alert.showAndWait();
            });
            App.setHasCheckedForOlderMovies(true);
        }
    }

    public void showRatingDialog(Movie movie) {
        Dialog<Integer> dialog = new Dialog<>();
        dialog.setTitle("Change Rating");

        VBox content = new VBox();
        content.setSpacing(10);
        Label label = new Label("Select a new rating:");

        ComboBox<Integer> ratingComboBox = new ComboBox<>();
        ratingComboBox.getItems().addAll(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        if (movie.getPersonalRating() != null) {
            ratingComboBox.setValue(movie.getPersonalRating());
        }

        content.getChildren().addAll(label, ratingComboBox);
        dialog.getDialogPane().setContent(content);

        ButtonType confirmButton = new ButtonType("Confirm", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(confirmButton, cancelButton);

        dialog.setResultConverter(button -> {
            if (button == confirmButton) {
                return ratingComboBox.getValue();
            }
            return null;
        });

        Optional<Integer> result = dialog.showAndWait();
        result.ifPresent(newRating -> {
            movieManager.rateMovie(movie.getId(), newRating);
            fetchMovies();
        });
    }
}
