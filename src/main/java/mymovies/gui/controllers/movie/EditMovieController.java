package mymovies.gui.controllers.movie;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import mymovies.be.Category;
import mymovies.be.Movie;
import mymovies.bll.CategoryManager;
import mymovies.bll.MovieManager;
import mymovies.utils.Validator;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

public class EditMovieController {
    private Movie movie;
    private final MovieManager movieManager = new MovieManager();
    private final CategoryManager categoryManager = new CategoryManager();

    @FXML
    private TextField nameField;

    @FXML
    private ComboBox<Integer> personalRating;

    @FXML
    private ComboBox<Integer> imdbRating;

    @FXML
    private ListView<Category> categorySelector;

    @FXML
    private VBox vboxContainer;

    @FXML
    private Label pathLbl;

    @FXML
    private Label errorMessagesLbl;

    private String moviePath = null;

    public void initialize() {
        fetchCategories();
    }

    public void update(ActionEvent actionEvent) {
        if (validateFields()) {
            List<Integer> selectedCategoryIds = getSelectedCategoryIds();

            String name = nameField.getText();
            Integer imdbRatingValue = imdbRating.getValue();
            Integer personalRatingValue = personalRating.getValue();

            movie.setName(name);
            movie.setImdbRating(imdbRatingValue);
            movie.setPersonalRating(personalRatingValue);
            movie.setFilePath(moviePath);
            movieManager.updateMovie(movie, selectedCategoryIds);
        }
        //TODO redirect to homepage
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
        setFields();
    }

    private void setFields() {
        nameField.setText(movie.getName());
        personalRating.setValue(movie.getPersonalRating());
        imdbRating.setValue(movie.getImdbRating());
        moviePath = movie.getFilePath();
        List<Category> categories = categoryManager.getAllCategoriesByMovie(movie);

        for (Category category : categories) {
            int index = categorySelector.getItems().indexOf(category);
            categorySelector.getSelectionModel().select(index);
        }

        pathLbl.setText("You have selected the following file : " + moviePath);
    }

    @FXML
    private void selectFile(ActionEvent event) {
        Stage stage = (Stage) vboxContainer.getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select a file");
        FileChooser.ExtensionFilter mp4Filter = new FileChooser.ExtensionFilter("Video Files (*.mp4, *.mpeg4)", "*.mp4", "*.mpeg4");
        fileChooser.getExtensionFilters().add(mp4Filter);
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            pathLbl.setText("You selected the following file: " + selectedFile.getAbsolutePath());
            moviePath = selectedFile.getAbsolutePath();
        } else {
            pathLbl.setText("No file selected");
            moviePath = null;
        }
    }

    public void fetchCategories() {
        List<Category> categories = categoryManager.getAllCategories();

        categorySelector.setItems(FXCollections.observableArrayList(categories));
        categorySelector.getSelectionModel().setSelectionMode(javafx.scene.control.SelectionMode.MULTIPLE);
    }

    public List<Integer> getSelectedCategoryIds() {
        return categorySelector.getSelectionModel().getSelectedItems().stream()
                .map(Category::getId)
                .collect(Collectors.toList());
    }

    private boolean validateFields() {
        Validator validator = new Validator()
                .setField("name", nameField.getText())
                .setField("imdbRating", imdbRating.getValue())
                .required("name", "imdbRating")
                .numeric("imdbRating")
                .min("name", 3)
                .min("imdbRating", 1)
                .max("imdbRating", 10);

        if (validator.passes()) {
            errorMessagesLbl.setText("");
            errorMessagesLbl.setVisible(false);
            return true;
        } else {
            StringBuilder errorMessages = new StringBuilder();
            validator.getErrors().forEach((field, messages) -> {
                messages.forEach(message -> errorMessages.append(message).append("\n"));
            });
            System.out.println(errorMessages.toString());
            errorMessagesLbl.setText(errorMessages.toString());
        }
        return false;
    }
}
