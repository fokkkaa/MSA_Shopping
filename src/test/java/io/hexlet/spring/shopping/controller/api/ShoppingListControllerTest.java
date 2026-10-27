package io.hexlet.spring.shopping.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.hexlet.spring.shopping.dto.ShoppingListCreateDTO;
import io.hexlet.spring.shopping.dto.ShoppingListDTO;
import io.hexlet.spring.shopping.mapper.ShoppingListMapper;
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
class ShoppingListControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ShoppingListRepository shoppingListRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ShoppingListMapper shoppingListMapper;

    private User testUser;
    private ShoppingList testList;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();
        shoppingListRepository.deleteAll();
        userRepository.deleteAll();
        testUser = ModelGenerator.buildUser();
        userRepository.save(testUser);
        testList = ModelGenerator.buildShoppingList(testUser);
        shoppingListRepository.save(testList);
    }

    @Test
    void testIndex() throws Exception {
        var result = mockMvc.perform(get("/api/lists"))
                .andExpect(status().isOk())
                .andReturn();
        var body = result.getResponse().getContentAsString();
        assertThatJson(body).isArray();
        List<ShoppingListDTO> listsDTO = List.of(shoppingListMapper.map(testList));
        assertThatJson(body).isEqualTo(objectMapper.writeValueAsString(listsDTO));
    }

    @Test
    void testCreate() throws Exception {
        ShoppingListCreateDTO dto = ModelGenerator.buildShoppingListCreateDTO(testUser.getId());

        var request = post("/api/lists")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto));

        var result = mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andReturn();
        var body = result.getResponse().getContentAsString();

        assertThatJson(body).and(
                v -> v.node("authorId").isEqualTo(testUser.getId()),
                v -> v.node("slug").isEqualTo(dto.getSlug()),
                v -> v.node("name").isEqualTo(dto.getName()),
                v -> v.node("description").isEqualTo(dto.getDescription()),
                v -> v.node("id").isPresent()
        );

        ShoppingList list = shoppingListRepository.findBySlug(dto.getSlug()).orElseThrow();
        assertNotNull(list);
        assertThat(list.getName()).isEqualTo(dto.getName());
        assertThat(list.getAuthor().getId()).isEqualTo(testUser.getId());
    }

    @Test
    void testShow() throws Exception {
        var result = mockMvc.perform(get("/api/lists/{id}", testList.getId()))
                .andExpect(status().isOk())
                .andReturn();
        var body = result.getResponse().getContentAsString();
        ShoppingListDTO expected = shoppingListMapper.map(testList);
        assertThatJson(body).isEqualTo(objectMapper.writeValueAsString(expected));
    }

    @Test
    void testShowNotFound() throws Exception {
        mockMvc.perform(get("/api/lists/{id}", 999999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdate() throws Exception {
        Map<String, JsonNullable<?>> update = new HashMap<>();
        update.put("name", JsonNullable.of("Updated List Name"));
        update.put("description", JsonNullable.of("Updated description"));

        var request = put("/api/lists/{id}", testList.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(update));

        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();
        var body = result.getResponse().getContentAsString();

        assertThatJson(body).and(
                v -> v.node("name").isEqualTo("Updated List Name"),
                v -> v.node("description").isEqualTo("Updated description"),
                v -> v.node("slug").isEqualTo(testList.getSlug())
        );

        ShoppingList updated = shoppingListRepository.findById(testList.getId()).orElseThrow();
        assertThat(updated.getName()).isEqualTo("Updated List Name");
        assertThat(updated.getDescription()).isEqualTo("Updated description");
        assertThat(updated.getSlug()).isEqualTo(testList.getSlug());
    }

    @Test
    void testUpdatePartial() throws Exception {
        Map<String, JsonNullable<?>> partial = new HashMap<>();
        partial.put("slug", JsonNullable.of("new-slug-only"));

        var request = put("/api/lists/{id}", testList.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(partial));

        mockMvc.perform(request)
                .andExpect(status().isOk());

        ShoppingList updated = shoppingListRepository.findById(testList.getId()).orElseThrow();
        assertThat(updated.getSlug()).isEqualTo("new-slug-only");
        assertThat(updated.getName()).isEqualTo(testList.getName());
    }

    @Test
    void testDestroy() throws Exception {
        mockMvc.perform(delete("/api/lists/{id}", testList.getId()))
                .andExpect(status().isNoContent());

        assertThat(shoppingListRepository.existsById(testList.getId())).isFalse();
    }
}
