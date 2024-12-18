package igortcruz.finApp.service;

import igortcruz.finApp.model.Category;
import igortcruz.finApp.repository.CategoryRepository;
import igortcruz.finApp.dto.category.CategoryRequestDTO;
import igortcruz.finApp.dto.category.CategoryResponseDTO;
import igortcruz.finApp.exception.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {
    @Autowired
    private CategoryRepository repository;

    public List<CategoryResponseDTO> fetchAllCategories() {
        return repository.findAllByOrderByIdAsc().stream().map(CategoryResponseDTO::new).toList();
    }

    public CategoryResponseDTO retrieveCategoryById(Long id) throws NotFoundException {
        Optional<Category> optionalCategory = repository.findById(id);
        return optionalCategory.map(CategoryResponseDTO::new).orElseThrow(NotFoundException::new); //.map : se a Categoria estiver presente no Optional<Category> ele mapeia passando o valor para um novo CategoryResponseDTO (CategoryResponseDTO::new) se não estiver ele joga um Exception (NotFoundException::new)
    }

    public CategoryResponseDTO saveCategory(CategoryRequestDTO data) {
        Optional<Category> optionalCategory = repository.findByName(data.name());
        if (optionalCategory.isPresent()) {
            throw new DataIntegrityViolationException("Category already exists with name: " + data.name());
        } else {
            Category category = repository.save(new Category(data));
            return new CategoryResponseDTO(category);
        }
    }

    public CategoryResponseDTO updateCategory(CategoryRequestDTO data) throws NotFoundException {
        Optional<Category> optionalCategory = repository.findById(data.id());
        if (optionalCategory.isPresent()) {
            Category category = optionalCategory.get();
            category.setName(data.name());
            category.setDescription(data.description());
            Category updatedCategory = repository.save(category);
            return new CategoryResponseDTO(updatedCategory);
        } else {
            throw new NotFoundException();
        }
    }

    public void removeCategory(Long id) throws NotFoundException {
        Optional<Category> optionalCategory = repository.findById(id);
        if (optionalCategory.isPresent()) {
            repository.deleteById(id);
        } else {
            throw new NotFoundException();
        }
    }
}
