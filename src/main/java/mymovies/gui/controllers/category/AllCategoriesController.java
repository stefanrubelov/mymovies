package mymovies.gui.controllers.category;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import mymovies.be.Category;
import mymovies.bll.CategoryManager;
import mymovies.gui.PageManager;

import java.io.IOException;
import java.util.List;

public class AllCategoriesController {
    final private CategoryManager categoryManager = new CategoryManager();

    @FXML
    private TableView<Category> categoriesTableView;

    @FXML
    private TableColumn<Category, Integer> idColumn;
    @FXML
    private TableColumn<Category, String> nameColumn;

    // This method will be called to populate the TableView
    public void initialize() {
        setUpTable();

        setUpContextMenu();
    }

    private void setUpTable() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        List<Category> categories = fetchCategories();
        ObservableList<Category> categoryObservableList = FXCollections.observableArrayList(categories);
        categoriesTableView.setItems(categoryObservableList);
    }

    private void setUpContextMenu() {
        ContextMenu contextMenu = new ContextMenu();

        MenuItem editCategoryMenuItem = new MenuItem("Edit");
        MenuItem deleteCategoryMenuItem = new MenuItem("Delete");

        editCategoryMenuItem.setOnAction(event -> {
            Category selectedCategory = categoriesTableView.getSelectionModel().getSelectedItem();
            if (selectedCategory != null) {
                PageManager.editCategory(selectedCategory, categoriesTableView);
            }
        });

        deleteCategoryMenuItem.setOnAction(event -> {
            Category selectedCategory = categoriesTableView.getSelectionModel().getSelectedItem();
            if (selectedCategory != null) {
                categoryManager.removeCategory(selectedCategory.getId());
                ObservableList<Category> currentItems = categoriesTableView.getItems();
                currentItems.remove(selectedCategory);
            }
        });

        contextMenu.getItems().addAll(editCategoryMenuItem, deleteCategoryMenuItem);

        categoriesTableView.setRowFactory(tv -> {
            TableRow<Category> row = new TableRow<>();
            row.setContextMenu(contextMenu);
            return row;
        });
    }

    private List<Category> fetchCategories() {
        return categoryManager.getAllCategories();
    }

    public void addNewCategory(ActionEvent actionEvent){
        PageManager.addCategory(actionEvent);
    }

    public void goBack(ActionEvent actionEvent) {
        PageManager.homepage(actionEvent);
    }
}

