package igortcruz.finApp.category;

import jakarta.persistence.*;
import lombok.*;

@Table(name = "categories")
@Entity(name = "Category")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Category {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    private String description;

    public Category(CategoryRequestDTO data){
        this.name = data.name();
        this.description = data.description();
    }
}
