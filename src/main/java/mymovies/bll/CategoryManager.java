package mymovies.bll;

import mymovies.be.Category;
import mymovies.be.Movie;
import mymovies.dal.CategoryRepository;

import java.util.List;
import java.util.stream.Collectors;

public class CategoryManager {
    private final CategoryRepository categoryRepository = new CategoryRepository();

    public List<Category> getAllCategories() {
        return this.categoryRepository.getAll();
    }

    public List<Category> getAllCategoriesByMovie(Movie movie) {
        return this.categoryRepository.getAllByMovieId(movie.getId());
    }

    public String getAllCategoriesByMovieToString(Movie movie) {
        List<Category> categoriesList = this.categoryRepository.getAllByMovieId(movie.getId());

        return categoriesList.stream()
                .map(Category::getName)
                .collect(Collectors.joining(", "));
    }

    public Category getCategoryById(int id) {
        Category category = new Category(id);
        return categoryRepository.findById(category);
    }

    public Category getCategoryByName(String name) {
        name = prepareName(name);
        Category category = new Category(name);
        return categoryRepository.findByName(category);
    }

    public void addCategory(String name) {
        name = prepareName(name);
        Category category = new Category(name);
        categoryRepository.create(category);
    }

    public void removeCategory(int id) {
        Category category = new Category(id);
        categoryRepository.delete(category);
    }

    public String prepareName(String name) {
        if (name == null || name.isEmpty()) {
            return "";
        }
        name = name.trim().toLowerCase();
        return Character.toUpperCase(name.charAt(0)) + name.substring(1);
    }
}
