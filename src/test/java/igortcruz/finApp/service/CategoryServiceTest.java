package igortcruz.finApp.service;

import igortcruz.finApp.dto.category.CategoryRequestDTO;
import igortcruz.finApp.dto.category.CategoryResponseDTO;
import igortcruz.finApp.exception.CategoryNameAlreadyExistsException;
import igortcruz.finApp.exception.NotFoundException;
import igortcruz.finApp.model.Category;
import igortcruz.finApp.repository.CategoryRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Nested
    public class GetCategory {
        @Test
        void SUCCESS_whenGetAllCategories() {
            // given
            Category category1 = new Category(1L, "Saúde", "Categoria de Saúde");
            Category category2 = new Category(2L, "Internet", "Categoria de Internet");
            when(categoryRepository.findAllByOrderByIdAsc())
                    .thenReturn(List.of(category1, category2));
            // when
            List<CategoryResponseDTO> result = categoryService.fetchAllCategories();

            // then
            assertEquals(2, result.size());
            assertEquals("Saúde", result.get(0).name());
            assertEquals("Internet", result.get(1).name());
        }

        @Test
        void SUCCESS_whenIdExist() {
            //given
            Long categoryId = 1L;
            when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());
            //when
            NotFoundException exception = assertThrows(NotFoundException.class, () -> categoryService.retrieveCategoryById(categoryId));
            //then
            verify(categoryRepository, times(1)).findById(categoryId);
            assertNull(exception.getMessage());
        }

        @Test
        void FAILURE_whenIdDoesNotExist() throws NotFoundException {
            // given
            Long categoryId = 1L;
            Category category = new Category(categoryId, "Energia", "Categoria de Energia");
            when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
            //when
            CategoryResponseDTO result = categoryService.retrieveCategoryById(categoryId);
            //then
            assertEquals(result.id(), categoryId);
            assertEquals(result.name(), category.getName());
            assertNotNull(result);
            verify(categoryRepository, times(1)).findById(categoryId);
        }
    }

    @Nested
    public class SaveCategory {
        @Test
        void SUCCESS_whenNameDoesNotExist() {
            // given
            CategoryRequestDTO categoryRequestDTO = new CategoryRequestDTO(null, "Energia", "Teste Energia Descrição");
            Category category = new Category(categoryRequestDTO);
            when(categoryRepository.save(category)).thenReturn(category);
            // when
            CategoryResponseDTO categoryResponseDTO = categoryService.saveCategory(categoryRequestDTO);
            // then
            // 1 version
            assertEquals(category.getId(), categoryResponseDTO.id());
            assertEquals(categoryResponseDTO.name(), categoryRequestDTO.name());
            assertEquals(categoryResponseDTO.description(), categoryRequestDTO.description());
            // 2 version
            // assertEquals(new CategoryResponseDTO(category), categoryResponseDTO);
            verify(categoryRepository, times(1)).save(category);
        }

        @Test
        void FAILURE_whenNameAlreadyExist() {
            //given
            Category category = new Category(1L, "Energia", "Descrição de Energia");
            CategoryRequestDTO categoryRequestDTO = new CategoryRequestDTO(null, "Energia", "Descrição de Energia 2");

            when(categoryRepository.findByName(categoryRequestDTO.name())).thenReturn(Optional.of(category));
            //when
            CategoryNameAlreadyExistsException exception = assertThrows(CategoryNameAlreadyExistsException.class, () -> categoryService.saveCategory(categoryRequestDTO));
            //then
            assertEquals(categoryRequestDTO.name(), category.getName());
            assertEquals(exception.getMessage(), "Category already exists with name: " + categoryRequestDTO.name());
            verify(categoryRepository, times(1)).findByName(categoryRequestDTO.name());
            verify(categoryRepository, times(0)).save(any());
        }
    }

    @Nested
    public class UpdateCategory {
        @Test
        void SUCCESS_whenACategoryIsUpdated() throws NotFoundException {
            //given
            CategoryRequestDTO categoryRequestDTO = new CategoryRequestDTO(1L, "Casa Atualizada", "Descrição de Casa Atualizada");
            Category category = new Category();
            category.setId(1L);
            category.setName("Casa");
            category.setDescription("Descrição de Casa");
            when(categoryRepository.findById(categoryRequestDTO.id())).thenReturn(Optional.of(category));
            category.setName(categoryRequestDTO.name());
            category.setDescription(categoryRequestDTO.description());
            when(categoryRepository.save(category)).thenReturn(category);
            //when
            CategoryResponseDTO categoryResponseDTO = categoryService.updateCategory(categoryRequestDTO);
            //then
            assertEquals("Casa Atualizada", categoryResponseDTO.name());
            assertEquals("Descrição de Casa Atualizada", categoryResponseDTO.description());
            verify(categoryRepository, times(1)).findById(any());
            verify(categoryRepository, times(1)).save(any());
        }

        @Test
        void FAILURE_whenACategoryDoesNotFoundId() {
            //given
            Long categoryId = 1L;
            CategoryRequestDTO categoryRequestDTO = new CategoryRequestDTO(categoryId, "Teste", "Teste descrição");
            when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());
            //when
            NotFoundException notFoundException = assertThrows(NotFoundException.class, () -> categoryService.updateCategory(categoryRequestDTO));
            //then
            verify(categoryRepository, times(1)).findById(categoryId);
            assertNull(notFoundException.getMessage());
        }
    }

    @Nested
    public class RemoveCategory {
        @Test
        void SUCCESS_whenACategoryHasBeenDeleted () throws NotFoundException {
            //given
            Long categoryId = 1L;
            Category category = new Category(categoryId,"Teste Nome", "Teste Descrição");
            when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
            doNothing().when(categoryRepository).deleteById(categoryId);
            //when
            categoryService.removeCategory(categoryId);
            //then
            verify(categoryRepository, times(1)).findById(categoryId);
            verify(categoryRepository, times(1)).deleteById(categoryId);
        }

        @Test
        void FAILURE_whenACategoryDoesNotFoundId () {
            //given
            Long categoryId = 1L;
            when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());
            //when
            NotFoundException notFoundException = assertThrows(NotFoundException.class, () -> categoryService.removeCategory(categoryId));
            // then
            verify(categoryRepository, times(1)).findById(categoryId);
        }
    }
}
