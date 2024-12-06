package igortcruz.finApp.controller.category;

import igortcruz.finApp.category.Category;
import igortcruz.finApp.category.CategoryRepository;
import igortcruz.finApp.category.CategoryRequestDTO;
import igortcruz.finApp.category.CategoryResponseDTO;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("category")
public class CategoryController {
    @Autowired
    private CategoryRepository repository;

    @GetMapping
    public ResponseEntity<List<CategoryResponseDTO>> getAllCategories() {
        return ResponseEntity.ok(repository.findAllByOrderByIdAsc().stream().map(CategoryResponseDTO::new).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponseDTO> getCategory(@PathVariable Long id){
        Optional<Category> optionalCategory = repository.findById(id);
        if (optionalCategory.isPresent()){
            return ResponseEntity.ok(new CategoryResponseDTO(optionalCategory.get()));
        }else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping
    @Transactional
    public ResponseEntity<CategoryResponseDTO> postCategory(@RequestBody CategoryRequestDTO data) {
        Category categoryData = repository.save(new Category(data));
        URI location = URI.create("category/" + categoryData.getId());
        return ResponseEntity.created(location).body(new CategoryResponseDTO(categoryData));
    }

    @PutMapping
    @Transactional
    public ResponseEntity<Category> updateCategory(@RequestBody @Validated CategoryRequestDTO data) {
        Optional<Category> optionalCategory = repository.findById(data.id());
        if (optionalCategory.isPresent()) {
            Category category = optionalCategory.get();
            category.setDescription(data.description());
            return ResponseEntity.ok(category);
        } else {
            throw new EntityNotFoundException();
        }
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<String> deleteCategory(@PathVariable Long id) {
        Optional<Category> optionalCategory = repository.findById(id);
        if (optionalCategory.isPresent()){
            repository.deleteById(optionalCategory.get().getId());
            return ResponseEntity.noContent().build();
        }else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

}
