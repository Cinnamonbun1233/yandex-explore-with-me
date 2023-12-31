package ewm.server.controller.category;

import ewm.server.dto.category.CategoryDto;
import ewm.server.dto.category.NewCategoryDto;
import ewm.server.service.category.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
public class CategoryController {
    private static final String CAT_ADMIN_GENERAL_PATH = "/admin/categories";
    private static final String CAT_ADMIN_BY_ID_PATH = CAT_ADMIN_GENERAL_PATH + "/{catId}";
    private static final String CAT_PUBLIC_PATH = "/categories";
    private final CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping(value = CAT_ADMIN_GENERAL_PATH)
    public ResponseEntity<CategoryDto> createNewCategory(@Valid @RequestBody NewCategoryDto newCategoryDto) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(categoryService.createNewCategory(newCategoryDto));
    }

    @GetMapping(CAT_PUBLIC_PATH)
    public ResponseEntity<List<CategoryDto>> getAllCategories(@RequestParam(required = false, defaultValue = "0") int from,
                                                              @RequestParam(required = false, defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(from > 0 ? from / size : 0, size);

        return ResponseEntity
                .ok()
                .body(categoryService.getAllCategories(pageable));
    }

    @GetMapping(CAT_PUBLIC_PATH + "/{catId}")
    public ResponseEntity<CategoryDto> getCategoryById(@PathVariable("catId") Long categoryId) {

        return ResponseEntity
                .ok()
                .body(categoryService.getCategoryById(categoryId));
    }

    @PatchMapping(value = CAT_ADMIN_BY_ID_PATH)
    public ResponseEntity<CategoryDto> updateCategoryById(@PathVariable("catId") Long categoryId,
                                                          @Valid @RequestBody CategoryDto categoryDto) {

        return ResponseEntity
                .ok()
                .body(categoryService.updateCategoryById(categoryId, categoryDto));
    }

    @DeleteMapping(value = CAT_ADMIN_BY_ID_PATH)
    public ResponseEntity<Void> deleteCategoryById(@PathVariable("catId") Long categoryId) {

        categoryService.deleteCategoryById(categoryId);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }
}