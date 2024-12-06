package igortcruz.finApp.category;

public record CategoryResponseDTO(Long id, String name, String description) {
    public CategoryResponseDTO(Category category){
        this(category.getId(), category.getName(), category.getDescription());
    }
}
