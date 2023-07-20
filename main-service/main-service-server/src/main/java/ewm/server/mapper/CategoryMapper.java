package ewm.server.mapper;

import ewm.server.dto.CategoryDto;
import ewm.server.model.category.Category;

public class CategoryMapper {
    public static CategoryDto mapModelToDto(Category category) {
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }
}