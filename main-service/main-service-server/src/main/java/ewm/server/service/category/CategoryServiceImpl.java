package ewm.server.service.category;

import ewm.server.dto.category.CategoryDto;
import ewm.server.dto.category.NewCategoryDto;
import ewm.server.exception.category.CategoryNotFoundException;
import ewm.server.mapper.category.CategoryMapper;
import ewm.server.model.category.Category;
import ewm.server.repo.category.CategoryRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepo categoryRepo;

    @Autowired
    public CategoryServiceImpl(CategoryRepo categoryRepo) {
        this.categoryRepo = categoryRepo;
    }

    @Override
    public CategoryDto addCategory(NewCategoryDto category) {
        return CategoryMapper.mapModelToDto(categoryRepo.save(CategoryMapper.mapDtoToModel(category)));
    }

    @Override
    public CategoryDto updateCategory(Long catId, CategoryDto inputCategory) {
        Category toBeUpdated = categoryRepo.findById(catId).orElseThrow(() -> {
            throw new CategoryNotFoundException("Category does not exist");
        });
        toBeUpdated.setName(inputCategory.getName());
        return CategoryMapper.mapModelToDto(categoryRepo.save(toBeUpdated));
    }

    @Override
    public void deleteCategory(Long catId) {
        checkIfCategoryExists(catId);
        categoryRepo.deleteById(catId);
    }

    @Override
    public List<CategoryDto> getAllCategories(int from, int size) {
        Pageable request = PageRequest.of(from > 0 ? from / size : 0, size);
        return categoryRepo.findAll(request).getContent().stream()
                .map(CategoryMapper::mapModelToDto)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDto getCategoryById(Long catId) {
        checkIfCategoryExists(catId);
        return CategoryMapper.mapModelToDto(categoryRepo.findById(catId).orElseThrow());
    }

    private void checkIfCategoryExists(Long catId) {
        if (categoryRepo.findById(catId).isEmpty()) {
            throw new CategoryNotFoundException("Category not found");
        }
    }
}