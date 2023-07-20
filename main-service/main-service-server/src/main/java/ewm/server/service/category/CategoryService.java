package ewm.server.service.category;

import ewm.server.model.category.Category;

import java.util.List;

public interface CategoryService {
    Category addCategory(Category category);

    Category updateCategory(Long catId, Category category);

    void deleteCategory(Long catId);

    List<Category> getAllCategories(int from, int size);

    Category getCategoryById(Long catId);
}