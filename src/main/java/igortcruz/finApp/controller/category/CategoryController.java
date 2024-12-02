package igortcruz.finApp.controller.category;

import igortcruz.finApp.category.Category;
import igortcruz.finApp.category.CategoryRepository;
import igortcruz.finApp.category.CategoryRequestDTO;
import igortcruz.finApp.category.CategoryResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("category")
public class CategoryController {
    @Autowired
    private CategoryRepository repository;

    @PostMapping
    public void saveCategory(@RequestBody CategoryRequestDTO data){
        Category categoryData = new Category(data);
        repository.save(categoryData);
        return;
    }

    @GetMapping
    public List<CategoryResponseDTO> getAll(){
        List<CategoryResponseDTO> categoryList = repository.findAll().stream().map(CategoryResponseDTO::new).toList();
        return categoryList;
    }
}
