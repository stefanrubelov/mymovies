package mymovies.gui.controllers.category;

import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.util.Duration;
import mymovies.be.Category;
import mymovies.bll.CategoryManager;
import mymovies.gui.PageManager;
import mymovies.utils.Validator;

public class EditCategoryController {
    private final CategoryManager categoryManager = new CategoryManager();
    private Category category;

    @FXML
    private TextField categoryName;

    @FXML
    private Button saveBtn;

    @FXML
    private Label errorMessageLbl;

    @FXML
    private Label successMessageLbl;

    public void update(ActionEvent actionEvent) {
        if (validateFields()) {
            category.setName(categoryName.getText());
            categoryManager.updateCategory(category);

            saveBtn.setDisable(true);
            successMessageLbl.setVisible(true);
            successMessageLbl.setText("Category updated successfully.");

            PauseTransition pause = new PauseTransition(Duration.millis(1250));
            pause.setOnFinished(event -> {
                successMessageLbl.setVisible(false);
                successMessageLbl.setText("");

                PageManager.allCategories(actionEvent);
            });
            pause.play();
        }
    }

    public void setCategory(Category category) {
        this.category = category;
        setFields();
    }

    public void setFields() {
        categoryName.setText(category.getName());
    }

    private boolean validateFields() {
        Validator validator = new Validator()
                .setField("name", categoryName.getText())
                .required("name")
                .unique("categories", "name", "name", categoryName.getText());

        if (validator.passes()) {
            errorMessageLbl.setText("");
            errorMessageLbl.setVisible(false);
            return true;
        } else {
            StringBuilder errorMessages = new StringBuilder();
            validator.getErrors().forEach((field, messages) -> {
                messages.forEach(message -> errorMessages.append(message).append("\n"));
            });
            System.out.println(errorMessages.toString());
            errorMessageLbl.setText(errorMessages.toString());
        }

        return false;
    }
}


