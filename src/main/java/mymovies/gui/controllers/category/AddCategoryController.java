package mymovies.gui.controllers.category;

import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.util.Duration;
import mymovies.bll.CategoryManager;
import mymovies.gui.PageManager;
import mymovies.utils.Validator;

public class AddCategoryController {
    final private CategoryManager categoryManager = new CategoryManager();

    @FXML
    private TextField categoryNameInput;

    @FXML
    private Button saveBtn;

    @FXML
    private Button saveAndReturnBtn;

    @FXML
    private Label errorMessageLbl;

    @FXML
    private Label successMessageLbl;

    @FXML
    public void save(ActionEvent actionEvent) {
        if (validateFields()) {
            this.saveCategory();

            saveBtn.setDisable(true);
            saveAndReturnBtn.setDisable(true);
            successMessageLbl.setVisible(true);
            successMessageLbl.setText("Category added successfully.");

            PauseTransition pause = new PauseTransition(Duration.millis(1000));
            pause.setOnFinished(event -> {
                saveBtn.setDisable(false);
                saveAndReturnBtn.setDisable(false);
                successMessageLbl.setVisible(false);
                successMessageLbl.setText("");
                categoryNameInput.clear();
            });
            pause.play();
        }
    }

    public void saveAndReturn(ActionEvent actionEvent) {
        if (validateFields()) {

            this.saveCategory();
            saveBtn.setDisable(true);
            saveAndReturnBtn.setDisable(true);
            successMessageLbl.setVisible(true);
            successMessageLbl.setText("Category added successfully.");

            PauseTransition pause = new PauseTransition(Duration.millis(1000));
            pause.setOnFinished(event -> {
                successMessageLbl.setVisible(false);
                successMessageLbl.setText("");

                PageManager.allCategories(actionEvent);
            });
            pause.play();
        }
    }

    public void saveCategory() {
        String categoryName = categoryNameInput.getText();

        categoryManager.addCategory(categoryName);

    }

    private boolean validateFields() {
        Validator validator = new Validator()
                .setField("name", categoryNameInput.getText())
                .required("name")
                .unique("categories", "name", "name", categoryNameInput.getText());

        if (validator.passes()) {
            errorMessageLbl.setText("");
            errorMessageLbl.setVisible(false);
            return true;
        } else {
            StringBuilder errorMessages = new StringBuilder();
            validator.getErrors().forEach((field, messages) -> {
                messages.forEach(message -> errorMessages.append(message).append("\n"));
            });
            errorMessageLbl.setVisible(true);
            errorMessageLbl.setText(errorMessages.toString());
        }
        return false;
    }
}
