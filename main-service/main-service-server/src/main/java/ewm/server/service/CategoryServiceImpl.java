package ewm.server.service;

import ewm.server.exception.CategoryNotFoundException;
import ewm.server.model.Category;
import ewm.server.repo.CategoryRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepo categoryRepo;

    @Autowired
    public CategoryServiceImpl(CategoryRepo categoryRepo) {
        this.categoryRepo = categoryRepo;
    }

    @Override
    public Category addCategory(Category category) {
        return categoryRepo.save(category);
    }

    @Override
    public Category updateCategory(Long catId, Category inputCategory) {
        Category toBeUpdated = categoryRepo.findById(catId)
                .orElseThrow(() -> {
                    throw new CategoryNotFoundException("Category not found");
                });
        toBeUpdated.setName(inputCategory.getName());
        return categoryRepo.save(toBeUpdated);
    }

    @Override
    public void deleteCategory(Long catId) {
        checkIfCategoryExists(catId);
        categoryRepo.deleteById(catId);
    }

    @Override
    public List<Category> getAllCategories(int from, int size) {
        Pageable request = PageRequest.of(from > 0 ? from / size : 0, size);
        return categoryRepo.findAll(request).getContent();
    }

    @Override
    public Category getCategoryById(Long catId) {
        return categoryRepo.findById(catId)
                .orElseThrow(() -> {
                    throw new CategoryNotFoundException("Category not found");
                });
    }

    private void checkIfCategoryExists(Long catId) {
        if (categoryRepo.findById(catId).isEmpty()) {
            throw new CategoryNotFoundException("Category not found");
        }
    }
}