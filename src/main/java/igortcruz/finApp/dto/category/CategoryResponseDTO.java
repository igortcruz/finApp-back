package igortcruz.finApp.dto.category;

import igortcruz.finApp.model.Category;

public record CategoryResponseDTO(Long id, String name, String description) {
    public CategoryResponseDTO(Category category){
        this(category.getId(), category.getName(), category.getDescription());
    }
}
