package ewm.server.service.category;

import ewm.server.dto.category.CategoryDto;
import ewm.server.dto.category.NewCategoryDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CategoryService {
    CategoryDto createNewCategory(NewCategoryDto newCategoryDto);

    List<CategoryDto> getAllCategories(Pageable pageable);

    CategoryDto getCategoryById(Long categoryId);

    CategoryDto updateCategoryById(Long categoryId, CategoryDto categoryDto);

    void deleteCategoryById(Long categoryId);


}