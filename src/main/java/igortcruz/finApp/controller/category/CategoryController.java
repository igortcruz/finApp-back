package igortcruz.finApp.controller.category;

import igortcruz.finApp.category.CategoryRequestDTO;
import igortcruz.finApp.category.CategoryResponseDTO;
import igortcruz.finApp.exception.NotFoundException;
import igortcruz.finApp.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<CategoryResponseDTO>> getAllCategories() {
        return ResponseEntity.ok(categoryService.fetchAllCategories());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponseDTO> getCategory(@PathVariable Long id){
        try {
            return ResponseEntity.ok(categoryService.retrieveCategoryById(id));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping
    @Transactional
    public ResponseEntity<CategoryResponseDTO> postCategory(@RequestBody CategoryRequestDTO data) {
        CategoryResponseDTO categoryData = categoryService.saveCategory(data);
        URI location = URI.create("category/" + categoryData.id());
        return ResponseEntity.created(location).body(categoryData);
    }

    @PutMapping
    @Transactional
    public ResponseEntity<CategoryResponseDTO> putCategory(@RequestBody @Validated CategoryRequestDTO data) {
        try {
            return ResponseEntity.ok(categoryService.updateCategory(data));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        try {
            categoryService.removeCategory(id);
            return ResponseEntity.noContent().build();
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

}
