package igortcruz.finApp.service;

import igortcruz.finApp.dto.category.CategoryRequestDTO;
import igortcruz.finApp.dto.category.CategoryResponseDTO;
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
        void shouldFetchAllCategories() {

            /*
            Mockar uma lista de categorias com seus atributos.
            quando(when) o categoryRepository roda as funções do JPA para a
            listagem de itens, o Act captura o resultado rodando a função
            simulada do Service(fetchAllCategories) e no Assert temos os
            resultados esperados:
            - Tamanho da lista esperada é 2
            - O nome do primeiro item é Saúde
            - O nome do Segundo item é Internet
             */

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
        void shouldRetrieveCategoryById() throws NotFoundException {

            /*
            1   Mockar uma Categoria e seus atributos com um Id específico.
            2   Chamar o retrieveCategoryById e colocar em uma Instância de Categoria
            3   Descrever os resultados esperados:
                - Id da Categoria encontrada ser igual o Id da Categoria Mockada.
                - Nome da Categoria encontrada ser igual o Nome da Categoria Mockada.
                - Garantir que o resutlado da busca não seja NULO
                - Garantir que o repositório seja chamado somente UMA VEZ.(Ainda não desenvolver para ver o code coverage)
             */
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

        @Test
        void shouldntRetrieveCategoryById() {

            /*
            Simular um ID de Categoria
            Buscar por esse ID e não encontrar lançando a Exception
            Verificar se:
            - O categoryRepository foi chamado apenas 1 vez
            - Verificar se a Message é Nula (Sem parâmetros válidos para um teste real)
             */
            //given
            Long categoryId = 1L;
            when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());
            //when
            NotFoundException exception = assertThrows(NotFoundException.class, () -> categoryService.retrieveCategoryById(categoryId));
            //then
            verify(categoryRepository, times(1)).findById(categoryId);
            assertNull(exception.getMessage());

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
            Category category = new Category(1L, "Energia","Descrição de Energia");
            CategoryRequestDTO categoryRequestDTO = new CategoryRequestDTO(null,"Energia","Descrição de Energia 2");

            when(categoryRepository.findByName(categoryRequestDTO.name())).thenReturn(Optional.of(category));
            //when
            DataIntegrityViolationException exception = assertThrows(DataIntegrityViolationException.class, () -> categoryService.saveCategory(categoryRequestDTO));
            //then
            assertEquals(categoryRequestDTO.name(), category.getName());
            assertEquals(exception.getMessage(), "Category already exists with name: "+ categoryRequestDTO.name());
            verify(categoryRepository, times(1)).findByName(categoryRequestDTO.name());
            verify(categoryRepository, times(0)).save(any());
        }

    }
}
