package igortcruz.finApp.repository;

import igortcruz.finApp.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findAllByOrderByIdAsc();
    Optional<Category> findByName(String name);
}
