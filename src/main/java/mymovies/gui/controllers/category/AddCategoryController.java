package mymovies.gui.controllers.category;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import mymovies.be.Category;
import mymovies.bll.CategoryManager;

public class AddCategoryController {
    final private CategoryManager categoryManager = new CategoryManager();

    @FXML
    private TextField categoryNameInput;

    @FXML
    private Label errorMessageLbl;

    @FXML
    public void save() {
        this.saveCategory();
    }

    public void saveAndReturn(){
        this.saveCategory();
    }

    public void saveCategory(){
        String categoryName = categoryNameInput.getText();

        Category existingCategory = categoryManager.getCategoryByName(categoryName);

        if (existingCategory == null) {
            categoryManager.addCategory(categoryName);
            errorMessageLbl.setText("");
        } else {
            errorMessageLbl.setText("This category already exists");
        }
        categoryNameInput.setText("");
    }
}
