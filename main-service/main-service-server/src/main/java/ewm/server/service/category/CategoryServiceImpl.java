package ewm.server.service.category;

import ewm.server.dto.category.CategoryDto;
import ewm.server.dto.category.NewCategoryDto;
import ewm.server.exception.category.CategoryNotFoundException;
import ewm.server.mapper.category.CategoryMapper;
import ewm.server.model.category.Category;
import ewm.server.repo.category.CategoryRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepo categoryRepo;

    @Transactional
    @Override
    public CategoryDto createNewCategory(NewCategoryDto newCategoryDto) {

        return CategoryMapper.mapModelToDto(categoryRepo.save(CategoryMapper.mapDtoToModel(newCategoryDto)));
    }

    @Transactional
    @Override
    public List<CategoryDto> getAllCategories(Pageable pageable) {

        return categoryRepo
                .findAll(pageable)
                .getContent()
                .stream()
                .map(CategoryMapper::mapModelToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public CategoryDto getCategoryById(Long categoryId) {

        checkIfCategoryExists(categoryId);

        return CategoryMapper.mapModelToDto(categoryRepo.findById(categoryId).orElseThrow());
    }

    @Transactional
    @Override
    public CategoryDto updateCategoryById(Long categoryId, CategoryDto categoryDto) {

        Category category = categoryRepo.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException(String.format("Category %d does not exist", categoryId)));
        category.setName(categoryDto.getName());

        return CategoryMapper.mapModelToDto(categoryRepo.save(category));
    }

    @Transactional
    @Override
    public void deleteCategoryById(Long categoryId) {

        checkIfCategoryExists(categoryId);

        categoryRepo.deleteById(categoryId);
    }

    private void checkIfCategoryExists(Long categoryId) {

        if (categoryRepo.findById(categoryId).isEmpty()) {
            throw new CategoryNotFoundException(String.format("Category %d does not exist", categoryId));
        }
    }
}