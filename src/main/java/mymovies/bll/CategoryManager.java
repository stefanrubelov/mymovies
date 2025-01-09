package mymovies.bll;

import mymovies.be.Category;
import mymovies.dal.CategoryRepository;

import java.util.List;

public class CategoryManager {
    private final CategoryRepository categoryRepository = new CategoryRepository();

    public List<Category> getAllCategories() {
        return this.categoryRepository.getAll();
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

    public String prepareName(String name){
        if (name == null || name.isEmpty()) {
            return "";
        }
        name = name.trim().toLowerCase();
        return Character.toUpperCase(name.charAt(0)) + name.substring(1);
    }
}
