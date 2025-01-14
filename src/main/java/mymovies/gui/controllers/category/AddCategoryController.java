package mymovies.gui.controllers.category;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import mymovies.bll.CategoryManager;
import mymovies.utils.Validator;

public class AddCategoryController {
    final private CategoryManager categoryManager = new CategoryManager();

    @FXML
    private TextField categoryNameInput;

    @FXML
    private Label errorMessageLbl;

    @FXML
    public void save() {
        this.saveCategory();
        //TODO redirect
    }

    public void saveAndReturn() {
        this.saveCategory();
        //TODO redirect to homepage?
    }

    public void saveCategory() {
        if (validateFields()) {
            String categoryName = categoryNameInput.getText();

            categoryManager.addCategory(categoryName);
        }
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
            System.out.println(errorMessages.toString());
            errorMessageLbl.setText(errorMessages.toString());
        }
        return false;
    }
}
