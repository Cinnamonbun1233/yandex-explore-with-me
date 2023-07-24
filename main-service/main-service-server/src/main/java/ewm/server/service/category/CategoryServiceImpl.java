package ewm.server.service.category;

import ewm.server.dto.category.CategoryDto;
import ewm.server.dto.category.NewCategoryDto;
import ewm.server.exception.category.CategoryNotFoundException;
import ewm.server.mapper.category.CategoryMapper;
import ewm.server.model.category.Category;
import ewm.server.repo.category.CategoryRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepo categoryRepo;

    @Transactional
    public CategoryDto addCategory(NewCategoryDto newCategoryDto) {
        return CategoryMapper.mapModelToDto(categoryRepo.save(CategoryMapper.mapDtoToModel(newCategoryDto)));
    }

    public List<CategoryDto> getAllCategories(int from, int size) {
        Pageable pageable = PageRequest.of(from > 0 ? from / size : 0, size);
        return categoryRepo
                .findAll(pageable)
                .getContent()
                .stream()
                .map(CategoryMapper::mapModelToDto)
                .collect(Collectors.toList());
    }

    public CategoryDto getCategoryById(Long catId) {
        checkIfCategoryExists(catId);
        return CategoryMapper.mapModelToDto(categoryRepo.findById(catId).orElseThrow());
    }

    @Transactional
    public CategoryDto updateCategory(Long catId, CategoryDto categoryDto) {
        Category category = categoryRepo.findById(catId).orElseThrow(
                () -> new CategoryNotFoundException(String.format("Category %d does not exist", catId)));
        category.setName(categoryDto.getName());
        return CategoryMapper.mapModelToDto(categoryRepo.save(category));
    }

    @Override
    public void deleteCategory(Long catId) {
        checkIfCategoryExists(catId);
        categoryRepo.deleteById(catId);
    }

    private void checkIfCategoryExists(Long catId) {
        if (categoryRepo.findById(catId).isEmpty()) {
            throw new CategoryNotFoundException(String.format("Category %d does not exist", catId));
        }
    }
}