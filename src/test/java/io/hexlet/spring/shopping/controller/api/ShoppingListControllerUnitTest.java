package io.hexlet.spring.shopping.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.hexlet.spring.shopping.config.JacksonConfig;
import io.hexlet.spring.shopping.dto.ShoppingListCreateDTO;
import io.hexlet.spring.shopping.dto.ShoppingListDTO;
import io.hexlet.spring.shopping.service.ProductService;
import io.hexlet.spring.shopping.service.ShoppingListService;
import io.hexlet.spring.shopping.util.ModelGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = ShoppingListController.class,
        excludeAutoConfiguration = {
                DataSourceAutoConfiguration.class,
                HibernateJpaAutoConfiguration.class
        }
)
@Import(JacksonConfig.class)
@TestMethodOrder(OrderAnnotation.class)
class ShoppingListControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ShoppingListService shoppingListService;

    @MockBean
    private ProductService productService;

    private ShoppingListDTO shoppingListDTO;
    private ShoppingListCreateDTO createDTO;

    @BeforeEach
    void setUp() {
        shoppingListDTO = new ShoppingListDTO();
        shoppingListDTO.setId(1L);
        shoppingListDTO.setAuthorId(1L);
        shoppingListDTO.setSlug("weekly-groceries");
        shoppingListDTO.setName("Weekly Groceries");
        shoppingListDTO.setDescription("Shopping list for the week");
        shoppingListDTO.setCreatedAt(LocalDate.now());
        shoppingListDTO.setUpdatedAt(LocalDate.now());

        createDTO = ModelGenerator.buildShoppingListCreateDTO(1L);
    }

    @Test
    @Order(1)
    void testCreate() throws Exception {
        given(shoppingListService.create(any(ShoppingListCreateDTO.class))).willReturn(shoppingListDTO);

        mockMvc.perform(post("/api/lists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.authorId", is(1)))
                .andExpect(jsonPath("$.slug", is("weekly-groceries")))
                .andExpect(jsonPath("$.name", is("Weekly Groceries")));
    }

    @Test
    @Order(2)
    void testIndex() throws Exception {
        List<ShoppingListDTO> lists = Collections.singletonList(shoppingListDTO);
        given(shoppingListService.getAll()).willReturn(lists);

        mockMvc.perform(get("/api/lists"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].slug", is("weekly-groceries")));
    }

    @Test
    @Order(3)
    void testShow() throws Exception {
        given(shoppingListService.getById(1L)).willReturn(shoppingListDTO);

        mockMvc.perform(get("/api/lists/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.slug", is("weekly-groceries")))
                .andExpect(jsonPath("$.name", is("Weekly Groceries")));
    }

    @Test
    @Order(4)
    void testDelete() throws Exception {
        doNothing().when(shoppingListService).delete(1L);

        mockMvc.perform(delete("/api/lists/{id}", 1L))
                .andExpect(status().isNoContent());
    }
    @Test
    void testUpdate() throws Exception {
        given(shoppingListService.update(org.mockito.ArgumentMatchers.eq(1L), any(io.hexlet.spring.shopping.dto.ShoppingListUpdateDTO.class)))
                .willReturn(shoppingListDTO);
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put("/api/lists/1")
                        .contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"Updated\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));
        org.mockito.Mockito.verify(shoppingListService).update(org.mockito.ArgumentMatchers.eq(1L), any(io.hexlet.spring.shopping.dto.ShoppingListUpdateDTO.class));
    }
}
