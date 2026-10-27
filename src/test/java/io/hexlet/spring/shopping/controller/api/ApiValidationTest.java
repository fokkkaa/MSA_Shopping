package io.hexlet.spring.shopping.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.hexlet.spring.shopping.model.*;
import io.hexlet.spring.shopping.repository.*;
import io.hexlet.spring.shopping.util.ModelGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.http.MediaType;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ApiValidationTest {
    @Autowired private org.springframework.test.web.servlet.MockMvc mvc;
    @Autowired private ObjectMapper json;
    @Autowired private UserRepository users;
    @Autowired private ShoppingListRepository lists;
    @Autowired private ProductRepository products;
    private User user;
    private ShoppingList list;
    private Product product;

    @BeforeEach
    void setUp() {
        products.deleteAll(); lists.deleteAll(); users.deleteAll();
        user = users.save(ModelGenerator.buildUser());
        list = lists.save(ModelGenerator.buildShoppingList(user));
        product = products.save(ModelGenerator.buildProduct(list, user));
    }

    @ParameterizedTest
    @ValueSource(strings = {"{\"quantity\":0}", "{\"quantity\":-2}", "{\"quantity\":null}",
            "{\"name\":\"   \"}", "{\"name\":null}", "{\"unit\":\"\"}",
            "{\"purchased\":null}", "{\"listId\":null}", "{\"authorId\":-1}"})
    void rejectsInvalidProductUpdates(String body) throws Exception {
        mvc.perform(put("/api/products/" + product.getId()).contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
        assertThat(products.findById(product.getId()).orElseThrow().getQuantity()).isEqualTo(product.getQuantity());
    }

    @Test
    void rejectsInvalidCreation() throws Exception {
        var dto = ModelGenerator.buildProductCreateDTO(list.getId(), user.getId());
        dto.setQuantity(-1);
        mvc.perform(post("/api/products").contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
        dto.setQuantity(1); dto.setName(" ");
        mvc.perform(post("/api/products").contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void validatesUserAndListUpdates() throws Exception {
        mvc.perform(put("/api/users/" + user.getId()).contentType(MediaType.APPLICATION_JSON).content("{\"email\":\"wrong\"}"))
                .andExpect(status().isBadRequest());
        mvc.perform(put("/api/lists/" + list.getId()).contentType(MediaType.APPLICATION_JSON).content("{\"name\":null}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void rejectsUnknownReferences() throws Exception {
        var dto = ModelGenerator.buildProductCreateDTO(Long.MAX_VALUE, user.getId());
        mvc.perform(post("/api/products").contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
        mvc.perform(put("/api/products/" + product.getId()).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"authorId\":9223372036854775807}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void rejectsDuplicateValues() throws Exception {
        var dto = ModelGenerator.buildUserCreateDTO(); dto.setEmail(user.getEmail());
        mvc.perform(post("/api/users").contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(dto)))
                .andExpect(status().isConflict());
        var listDto = ModelGenerator.buildShoppingListCreateDTO(user.getId()); listDto.setSlug(list.getSlug());
        mvc.perform(post("/api/lists").contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(listDto)))
                .andExpect(status().isConflict());
    }

    @Test
    void deletesListWithProductsThenUser() throws Exception {
        mvc.perform(delete("/api/users/" + user.getId())).andExpect(status().isConflict());
        mvc.perform(delete("/api/lists/" + list.getId())).andExpect(status().isNoContent());
        assertThat(products.findById(product.getId())).isEmpty();
        assertThat(lists.findById(list.getId())).isEmpty();
        mvc.perform(delete("/api/users/" + user.getId())).andExpect(status().isNoContent());
    }

    @Test
    void partialUpdateKeepsOmittedFields() throws Exception {
        mvc.perform(put("/api/products/" + product.getId()).contentType(MediaType.APPLICATION_JSON).content("{\"purchased\":true}"))
                .andExpect(status().isOk());
        Product actual = products.findById(product.getId()).orElseThrow();
        assertThat(actual.getPurchased()).isTrue();
        assertThat(actual.getName()).isEqualTo(product.getName());
        assertThat(actual.getQuantity()).isEqualTo(product.getQuantity());
    }
}
