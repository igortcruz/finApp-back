package igortcruz.finApp.category;

import org.antlr.v4.runtime.misc.NotNull;

public record CategoryRequestDTO(
        Long id,
        String name,
        String description
) {
}
