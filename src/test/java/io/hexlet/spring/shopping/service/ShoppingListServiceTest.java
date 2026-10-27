package io.hexlet.spring.shopping.service;

import io.hexlet.spring.shopping.dto.ShoppingListCreateDTO;
import io.hexlet.spring.shopping.dto.ShoppingListDTO;
import io.hexlet.spring.shopping.dto.ShoppingListUpdateDTO;
import io.hexlet.spring.shopping.exception.ResourceNotFoundException;
import io.hexlet.spring.shopping.mapper.ShoppingListMapper;
import io.hexlet.spring.shopping.model.ShoppingList;
import io.hexlet.spring.shopping.model.User;
import io.hexlet.spring.shopping.repository.ShoppingListRepository;
import io.hexlet.spring.shopping.util.ModelGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openapitools.jackson.nullable.JsonNullable;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ShoppingListServiceTest {

    @Mock
    private ShoppingListRepository shoppingListRepository;

    @Mock
    private ShoppingListMapper shoppingListMapper;

    @Mock
    private io.hexlet.spring.shopping.repository.ProductRepository productRepository;

    @InjectMocks
    private ShoppingListServiceImpl shoppingListService;

    @Test
    void testCreate() {
        User author = ModelGenerator.buildUser();
        author.setId(1L);
        ShoppingListCreateDTO createDTO = ModelGenerator.buildShoppingListCreateDTO(author.getId());
        ShoppingList list = ModelGenerator.buildShoppingList(author);
        ShoppingList saved = ModelGenerator.buildShoppingList(author);
        saved.setId(1L);
        saved.setName(createDTO.getName());
        ShoppingListDTO listDTO = new ShoppingListDTO();
        listDTO.setId(1L);
        listDTO.setAuthorId(author.getId());
        listDTO.setName(createDTO.getName());

        given(shoppingListMapper.map(createDTO)).willReturn(list);
        given(shoppingListRepository.save(list)).willReturn(saved);
        given(shoppingListMapper.map(saved)).willReturn(listDTO);

        ShoppingListDTO result = shoppingListService.create(createDTO);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getAuthorId()).isEqualTo(author.getId());
        assertThat(result.getName()).isEqualTo(createDTO.getName());
        verify(shoppingListMapper, times(1)).map(createDTO);
        verify(shoppingListRepository, times(1)).save(list);
    }

    @Test
    void testGetById() {
        User author = ModelGenerator.buildUser();
        author.setId(1L);
        ShoppingList list = ModelGenerator.buildShoppingList(author);
        list.setId(1L);
        ShoppingListDTO listDTO = new ShoppingListDTO();
        listDTO.setId(1L);
        listDTO.setSlug(list.getSlug());
        listDTO.setAuthorId(author.getId());

        given(shoppingListRepository.findById(1L)).willReturn(Optional.of(list));
        given(shoppingListMapper.map(list)).willReturn(listDTO);

        ShoppingListDTO result = shoppingListService.getById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getSlug()).isEqualTo(list.getSlug());
        verify(shoppingListRepository, times(1)).findById(1L);
    }

    @Test
    void testGetByIdNotFound() {
        Long fakeId = 999L;
        given(shoppingListRepository.findById(fakeId)).willReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> shoppingListService.getById(fakeId));
        verify(shoppingListRepository, times(1)).findById(fakeId);
        verify(shoppingListMapper, never()).map(any(ShoppingList.class));
    }

    @Test
    void testUpdate() {
        User author = ModelGenerator.buildUser();
        author.setId(1L);
        ShoppingList list = ModelGenerator.buildShoppingList(author);
        list.setId(1L);
        ShoppingListUpdateDTO updateDTO = new ShoppingListUpdateDTO();
        updateDTO.setName(JsonNullable.of("New Title"));
        ShoppingList saved = ModelGenerator.buildShoppingList(author);
        saved.setId(1L);
        saved.setName("New Title");
        ShoppingListDTO listDTO = new ShoppingListDTO();
        listDTO.setId(1L);
        listDTO.setName("New Title");

        given(shoppingListRepository.findById(1L)).willReturn(Optional.of(list));
        given(shoppingListRepository.save(list)).willReturn(saved);
        given(shoppingListMapper.map(saved)).willReturn(listDTO);

        ShoppingListDTO result = shoppingListService.update(1L, updateDTO);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("New Title");
        verify(shoppingListMapper, times(1)).update(updateDTO, list);
        verify(shoppingListRepository, times(1)).save(list);
    }

    @Test
    void testDelete() {
        Long id = 1L;
        given(shoppingListRepository.existsById(id)).willReturn(true);

        shoppingListService.delete(id);

        verify(shoppingListRepository, times(1)).deleteById(id);
        verify(shoppingListRepository, times(1)).existsById(id);
    }
    @Test
    void testGetAll() {
        ShoppingList entity = new ShoppingList();
        ShoppingListDTO dto = new ShoppingListDTO();
        dto.setId(7L);
        given(shoppingListRepository.findAll()).willReturn(java.util.List.of(entity));
        given(shoppingListMapper.map(entity)).willReturn(dto);
        assertThat(shoppingListService.getAll()).containsExactly(dto);
    }
}
