package mymovies.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;
import mymovies.be.Category;
import mymovies.gui.controllers.category.EditCategoryController;

import java.io.IOException;

public class PageManager {
    private static Stage primaryStage;

    public static void setPrimaryStage(Stage stage) {
        primaryStage = stage;
    }

    public static void switchView(String fxmlPath, ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(PageManager.class.getResource(fxmlPath));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = primaryStage != null ? primaryStage : (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load view: " + fxmlPath, e);
        }
    }

    public static void homepage(ActionEvent event) {
        switchView("/views/homepage.fxml", event);
    }

    public static void addMovie(ActionEvent event) {
        switchView("/views/movie/add-movie.fxml", event);
    }

    public static void allCategories(ActionEvent event) {
        switchView("/views/category/all-categories.fxml", event);
    }

    public static void addCategory(ActionEvent actionEvent) {
        switchView("/views/category/add-category.fxml", actionEvent);
    }

    public static void editCategory(Category category, Node selectedNode) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(PageManager.class.getResource("/views/category/edit-category.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            EditCategoryController editCategoryController = fxmlLoader.getController();
            editCategoryController.setCategory(category);
            Stage stage = (Stage) selectedNode.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load view: /views/category/edit-category.fxml", e);
        }
    }
}
