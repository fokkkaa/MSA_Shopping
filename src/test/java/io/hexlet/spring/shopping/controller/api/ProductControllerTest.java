package io.hexlet.spring.shopping.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.hexlet.spring.shopping.dto.ProductCreateDTO;
import io.hexlet.spring.shopping.dto.ProductDTO;
import io.hexlet.spring.shopping.mapper.ProductMapper;
import io.hexlet.spring.shopping.model.Product;
import io.hexlet.spring.shopping.model.ShoppingList;
import io.hexlet.spring.shopping.model.User;
import io.hexlet.spring.shopping.repository.ProductRepository;
import io.hexlet.spring.shopping.repository.ShoppingListRepository;
import io.hexlet.spring.shopping.repository.UserRepository;
import io.hexlet.spring.shopping.util.ModelGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ShoppingListRepository shoppingListRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductMapper productMapper;

    private User testUser;
    private ShoppingList testList;
    private Product testProduct;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();
        shoppingListRepository.deleteAll();
        userRepository.deleteAll();
        testUser = ModelGenerator.buildUser();
        userRepository.save(testUser);
        testList = ModelGenerator.buildShoppingList(testUser);
        shoppingListRepository.save(testList);
        testProduct = ModelGenerator.buildProduct(testList, testUser);
        productRepository.save(testProduct);
    }

    @Test
    void testIndex() throws Exception {
        var result = mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andReturn();
        var body = result.getResponse().getContentAsString();
        assertThatJson(body).isArray();
        List<ProductDTO> expected = List.of(productMapper.map(testProduct));
        assertThatJson(body).isEqualTo(objectMapper.writeValueAsString(expected));
    }

    @Test
    void testCreate() throws Exception {
        ProductCreateDTO dto = ModelGenerator.buildProductCreateDTO(testList.getId(), testUser.getId());

        var request = post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto));

        var result = mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andReturn();
        var body = result.getResponse().getContentAsString();

        assertThatJson(body).and(
                v -> v.node("listId").isEqualTo(testList.getId()),
                v -> v.node("authorId").isEqualTo(testUser.getId()),
                v -> v.node("name").isEqualTo(dto.getName()),
                v -> v.node("quantity").isEqualTo(dto.getQuantity()),
                v -> v.node("unit").isEqualTo(dto.getUnit()),
                v -> v.node("purchased").isEqualTo(false),
                v -> v.node("id").isPresent()
        );

        List<Product> all = productRepository.findAll();
        assertThat(all).hasSize(2);
        Product created = all.stream()
                .filter(c -> c.getName().equals(dto.getName()))
                .findFirst().orElseThrow();
        assertNotNull(created);
        assertThat(created.getList().getId()).isEqualTo(testList.getId());
        assertThat(created.getAuthor().getId()).isEqualTo(testUser.getId());
    }

    @Test
    void testShow() throws Exception {
        var result = mockMvc.perform(get("/api/products/{id}", testProduct.getId()))
                .andExpect(status().isOk())
                .andReturn();
        var body = result.getResponse().getContentAsString();
        ProductDTO expected = productMapper.map(testProduct);
        assertThatJson(body).isEqualTo(objectMapper.writeValueAsString(expected));
    }

    @Test
    void testShowNotFound() throws Exception {
        mockMvc.perform(get("/api/products/{id}", 999999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdate() throws Exception {
        Map<String, JsonNullable<?>> update = new HashMap<>();
        update.put("name", JsonNullable.of("Updated product name"));
        update.put("quantity", JsonNullable.of(5));
        update.put("purchased", JsonNullable.of(true));

        var request = put("/api/products/{id}", testProduct.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(update));

        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();
        var body = result.getResponse().getContentAsString();

        assertThatJson(body).and(
                v -> v.node("name").isEqualTo("Updated product name"),
                v -> v.node("quantity").isEqualTo(5),
                v -> v.node("purchased").isEqualTo(true)
        );

        Product updated = productRepository.findById(testProduct.getId()).orElseThrow();
        assertThat(updated.getName()).isEqualTo("Updated product name");
        assertThat(updated.getQuantity()).isEqualTo(5);
        assertThat(updated.getPurchased()).isEqualTo(true);
    }

    @Test
    void testDestroy() throws Exception {
        mockMvc.perform(delete("/api/products/{id}", testProduct.getId()))
                .andExpect(status().isNoContent());

        assertThat(productRepository.existsById(testProduct.getId())).isFalse();
    }

    @Test
    void testGetProductsByList() throws Exception {
        var result = mockMvc.perform(get("/api/lists/{listId}/products", testList.getId()))
                .andExpect(status().isOk())
                .andReturn();
        var body = result.getResponse().getContentAsString();

        assertThatJson(body).isArray();
        List<ProductDTO> expected = List.of(productMapper.map(testProduct));
        assertThatJson(body).isEqualTo(objectMapper.writeValueAsString(expected));
    }

    @Test
    void testGetProductsByListEmpty() throws Exception {
        productRepository.deleteAll();
        var result = mockMvc.perform(get("/api/lists/{listId}/products", testList.getId()))
                .andExpect(status().isOk())
                .andReturn();
        var body = result.getResponse().getContentAsString();
        assertThatJson(body).isArray().isEmpty();
    }
}
