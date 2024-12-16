package igortcruz.finApp.dto.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CategoryRequestDTO(
        Long id,
        @NotNull(message = "Name must not be Null")
        @NotBlank(message = "Name must not be Empty")
        String name,
        String description
) {
}
