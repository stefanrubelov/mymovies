package mymovies.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import mymovies.BE.Movies;
import mymovies.dal.db.QueryBuilder;

import javafx.scene.control.ListView;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MovieController {

    @FXML
    private ListView<String> movieListView;

    @FXML
    private ComboBox<String> categoryComboBox;

    @FXML
    public void initialize() {
        loadCategories();
    }

    public void loadCategories() {
        List<String> categories = new ArrayList<>();
        QueryBuilder queryBuilder = new QueryBuilder();

        try (ResultSet rs = queryBuilder
                .select("name")
                .from("categories")
                .get()){

            while (rs != null && rs.next()){
                categories.add(rs.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        categoryComboBox.getItems().setAll(categories);
    }

    public List<Movies> fetchMoviesByCategory(String category) {
        List<Movies> movies = new ArrayList<>();
        QueryBuilder queryBuilder = new QueryBuilder();

        try (ResultSet rs = queryBuilder
                .select("m.*")
                .from("movies m")
                .innerJoin("category_movie cm", "m.id = cm.movie_id")
                .innerJoin("categories c", "c.id = cm.category_id")
                .where ("c.name", "=", category)
                .get()){

            while (rs != null && rs.next()){
                int id = rs.getInt("id");
                String name =rs.getString("name");
                double rating =rs.getDouble("rating");
                String fileLink =rs.getString("file_link");
                LocalDateTime lastView =rs.getTimestamp("last_view").toLocalDateTime();
                LocalDateTime createdAt =rs.getTimestamp("created_at").toLocalDateTime();
                LocalDateTime updatedAt = rs.getTimestamp("updated_at").toLocalDateTime();

                movies.add(new Movies(id, name, rating, fileLink, lastView, createdAt, updatedAt));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return movies;
    }

    //method to show movies in the listView
    public void displayMoviesByCategory(String category) {
        List<Movies> movies = fetchMoviesByCategory (category);
        List<String> moviesNames = movies.stream().map(Movies::getName).toList(); //Extract the names

        movieListView.getItems().setAll(moviesNames);
    }

    public void onCategorySelected(ActionEvent event) {
        String selectedCategory = categoryComboBox.getValue();
        if (selectedCategory != null) {
            displayMoviesByCategory(selectedCategory);
        }
    }
}
