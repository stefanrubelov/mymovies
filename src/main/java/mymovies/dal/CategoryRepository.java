package mymovies.dal;

import mymovies.be.Category;
import mymovies.dal.db.QueryBuilder;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CategoryRepository {
    private QueryBuilder queryBuilder;
    final private Logger logger = Logger.getAnonymousLogger();

    public CategoryRepository() {
        queryBuilder = new QueryBuilder();
    }

    public List<Category> getAll() {
        List<Category> categories = new ArrayList<>();

        ResultSet resultSet = queryBuilder
                .from("categories")
                .select("*")
                .get();

        try {
            while (resultSet.next()) {
                Category category = mapModel(resultSet);

                categories.add(category);
            }

            return categories;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);

            return categories;
        }
    }

    public Category findById(int categoryId) {
        Category result = null;
        ResultSet resultSet = queryBuilder
                .from("categories")
                .select("*")
                .where("id", "=", categoryId)
                .get();

        try {
            while (resultSet.next()) {
                result = mapModel(resultSet);
            }

            return result;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);

            return result;
        }
    }

    public Category findByName(Category category) {
        Category result = null;

        ResultSet resultSet = queryBuilder
                .from("categories")
                .select("*")
                .where("name", "=", category.getName())
                .top(1)
                .get();

        try {
            while (resultSet.next()) {
                result = mapModel(resultSet);
            }

            return result;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);

            return result;
        }
    }

    public void create(Category category) {
        queryBuilder.table("categories")
                .insert("name", category.getName())
                .save();
    }

    public void update(Category category) {
        queryBuilder.table("categories")
                .set("name", category.getName())
                .where("id", "=", category.getId())
                .update();
    }

    public void delete(Category category) {
        queryBuilder
                .from("categories")
                .where("id", "=", category.getId())
                .delete();
    }

    public List<Category> getAllByMovieId(int movieId) {
        List<Category> categories = new ArrayList<>();
        ResultSet resultSet = queryBuilder
                .from("category_movie")
                .join("categories", "category_movie.category_id = categories.id", "")
                .select("categories.name as name, categories.id as id")
                .where("movie_id", "=", movieId)
                .get();

        try {
            while (resultSet.next()) {
                Category category = mapModel(resultSet);

                categories.add(category);
            }

            return categories;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);

            return categories;
        }
    }

    private Category mapModel(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("id");
        String name = resultSet.getString("name");
        return new Category(id, name);
    }
}
