package mymovies.gui.controllers.category;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import mymovies.be.Category;
import mymovies.bll.CategoryManager;
import mymovies.utils.Validator;

public class EditCategoryController {
    private final CategoryManager categoryManager = new CategoryManager();
    private Category category;

    @FXML
    private TextField categoryName;

    @FXML
    private Label errorMessageLbl;

    public void update() {
        if (validateFields()) {
            category.setName(categoryName.getText());
            categoryManager.updateCategory(category);
            //TODO success message?
            //TODO redirect to homepage
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


