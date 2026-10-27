package io.hexlet.spring.shopping.service;

import io.hexlet.spring.shopping.dto.ProductCreateDTO;
import io.hexlet.spring.shopping.dto.ProductDTO;
import io.hexlet.spring.shopping.dto.ProductUpdateDTO;
import io.hexlet.spring.shopping.exception.ResourceNotFoundException;
import io.hexlet.spring.shopping.mapper.ProductMapper;
import io.hexlet.spring.shopping.model.Product;
import io.hexlet.spring.shopping.model.ShoppingList;
import io.hexlet.spring.shopping.model.User;
import io.hexlet.spring.shopping.repository.ProductRepository;
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
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductServiceImpl productService;

    @Test
    void testCreate() {
        User author = ModelGenerator.buildUser();
        author.setId(1L);
        ShoppingList list = ModelGenerator.buildShoppingList(author);
        list.setId(2L);
        ProductCreateDTO createDTO = ModelGenerator.buildProductCreateDTO(list.getId(), author.getId());
        Product product = ModelGenerator.buildProduct(list, author);
        Product saved = ModelGenerator.buildProduct(list, author);
        saved.setId(1L);
        ProductDTO productDTO = new ProductDTO();
        productDTO.setId(1L);
        productDTO.setListId(list.getId());
        productDTO.setAuthorId(author.getId());
        productDTO.setName(createDTO.getName());

        given(productMapper.map(createDTO)).willReturn(product);
        given(productRepository.save(product)).willReturn(saved);
        given(productMapper.map(saved)).willReturn(productDTO);

        ProductDTO result = productService.create(createDTO);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getListId()).isEqualTo(list.getId());
        assertThat(result.getAuthorId()).isEqualTo(author.getId());
        assertThat(result.getName()).isEqualTo(createDTO.getName());
        verify(productMapper, times(1)).map(createDTO);
        verify(productRepository, times(1)).save(product);
    }

    @Test
    void testGetById() {
        User author = ModelGenerator.buildUser();
        author.setId(1L);
        ShoppingList list = ModelGenerator.buildShoppingList(author);
        list.setId(2L);
        Product product = ModelGenerator.buildProduct(list, author);
        product.setId(1L);
        ProductDTO productDTO = new ProductDTO();
        productDTO.setId(1L);
        productDTO.setListId(list.getId());

        given(productRepository.findById(1L)).willReturn(Optional.of(product));
        given(productMapper.map(product)).willReturn(productDTO);

        ProductDTO result = productService.getById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getListId()).isEqualTo(list.getId());
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    void testGetByIdNotFound() {
        Long fakeId = 999L;
        given(productRepository.findById(fakeId)).willReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.getById(fakeId));
        verify(productRepository, times(1)).findById(fakeId);
        verify(productMapper, never()).map(any(Product.class));
    }

    @Test
    void testUpdate() {
        User author = ModelGenerator.buildUser();
        author.setId(1L);
        ShoppingList list = ModelGenerator.buildShoppingList(author);
        list.setId(2L);
        Product product = ModelGenerator.buildProduct(list, author);
        product.setId(1L);
        ProductUpdateDTO updateDTO = new ProductUpdateDTO();
        updateDTO.setName(JsonNullable.of("Updated product"));
        updateDTO.setQuantity(JsonNullable.of(20));
        updateDTO.setPurchased(JsonNullable.of(true));
        Product saved = ModelGenerator.buildProduct(list, author);
        saved.setId(1L);
        saved.setName("Updated product");
        saved.setQuantity(20);
        saved.setPurchased(true);
        ProductDTO productDTO = new ProductDTO();
        productDTO.setId(1L);
        productDTO.setName("Updated product");
        productDTO.setQuantity(20);
        productDTO.setPurchased(true);

        given(productRepository.findById(1L)).willReturn(Optional.of(product));
        given(productRepository.save(product)).willReturn(saved);
        given(productMapper.map(saved)).willReturn(productDTO);

        ProductDTO result = productService.update(1L, updateDTO);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Updated product");
        assertThat(result.getQuantity()).isEqualTo(20);
        assertThat(result.getPurchased()).isEqualTo(true);
        verify(productMapper, times(1)).update(updateDTO, product);
        verify(productRepository, times(1)).save(product);
    }

    @Test
    void testDelete() {
        Long id = 1L;
        given(productRepository.existsById(id)).willReturn(true);

        productService.delete(id);

        verify(productRepository, times(1)).deleteById(id);
        verify(productRepository, times(1)).existsById(id);
    }
    @Test
    void testGetAll() {
        Product entity = new Product();
        ProductDTO dto = new ProductDTO();
        dto.setId(7L);
        given(productRepository.findAll()).willReturn(java.util.List.of(entity));
        given(productMapper.map(entity)).willReturn(dto);
        assertThat(productService.getAll()).containsExactly(dto);
    }
}
