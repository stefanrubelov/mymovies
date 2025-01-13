package mymovies.gui.controllers.movie;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import mymovies.be.Category;
import mymovies.bll.CategoryManager;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class AddMovieController {
    final private CategoryManager categoryManager = new CategoryManager();

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

        List<Integer> selectedCategoryIds = getSelectedCategoryIds();

        System.out.println(nameField.getText() + "  " + personalRating.getValue() + "  " + imdbRating.getValue() + "   " + selectedCategoryIds + "  " + moviePath);
    }

    public List<Integer> getSelectedCategoryIds() {
        return category.getSelectionModel().getSelectedItems().stream()
                .map(Category::getId)
                .collect(Collectors.toList());
    }

    @FXML
    private void selectFile(ActionEvent event) throws IOException {
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
}
