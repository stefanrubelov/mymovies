package mymovies.gui.controllers.movie;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
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

public class AddMovieController {
    final private CategoryManager categoryManager = new CategoryManager();
    final private MovieManager movieManager = new MovieManager();

    @FXML
    private TextField nameField;

    @FXML
    private ComboBox<Integer> personalRating;

    @FXML
    private ComboBox<Integer> imdbRating;

    @FXML
    private ListView<Category> category;

    @FXML
    private Button uploadFileBtn;

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

    public void fetchCategories() {
        List<Category> categories = categoryManager.getAllCategories();

        category.setItems(FXCollections.observableArrayList(categories));
        category.getSelectionModel().setSelectionMode(javafx.scene.control.SelectionMode.MULTIPLE);
    }

    public void save(ActionEvent actionEvent) {
        if (this.validateFields()) {
            List<Integer> selectedCategoryIds = getSelectedCategoryIds();
            String name = nameField.getText();
            Integer imdbRatingValue = imdbRating.getValue();
            Integer personalRatingValue = personalRating.getValue();

            Movie movie = new Movie(name, moviePath, imdbRatingValue, personalRatingValue);
            movieManager.addMovie(movie, selectedCategoryIds);
        }

    }

    public List<Integer> getSelectedCategoryIds() {
        return category.getSelectionModel().getSelectedItems().stream()
                .map(Category::getId)
                .collect(Collectors.toList());
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
