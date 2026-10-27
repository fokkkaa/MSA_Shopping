package io.hexlet.spring.shopping.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.hexlet.spring.shopping.config.JacksonConfig;
import io.hexlet.spring.shopping.dto.UserCreateDTO;
import io.hexlet.spring.shopping.dto.UserDTO;
import io.hexlet.spring.shopping.service.UserService;
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
        controllers = UserController.class,
        excludeAutoConfiguration = {
                DataSourceAutoConfiguration.class,
                HibernateJpaAutoConfiguration.class
        }
)
@Import(JacksonConfig.class)
@TestMethodOrder(OrderAnnotation.class)
class UserControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    private UserDTO userDTO;
    private UserCreateDTO createDTO;

    @BeforeEach
    void setUp() {
        userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setFirstName("Alice");
        userDTO.setLastName("Smith");
        userDTO.setEmail("alice@example.com");
        userDTO.setCreatedAt(LocalDate.now());

        createDTO = ModelGenerator.buildUserCreateDTO();
    }

    @Test
    @Order(1)
    void testCreate() throws Exception {
        given(userService.create(any(UserCreateDTO.class))).willReturn(userDTO);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.firstName", is("Alice")))
                .andExpect(jsonPath("$.lastName", is("Smith")))
                .andExpect(jsonPath("$.email", is("alice@example.com")));
    }

    @Test
    @Order(2)
    void testIndex() throws Exception {
        List<UserDTO> users = Collections.singletonList(userDTO);
        given(userService.getAll()).willReturn(users);

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].firstName", is("Alice")));
    }

    @Test
    @Order(3)
    void testShow() throws Exception {
        given(userService.getById(1L)).willReturn(userDTO);

        mockMvc.perform(get("/api/users/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.email", is("alice@example.com")));
    }

    @Test
    @Order(4)
    void testDelete() throws Exception {
        doNothing().when(userService).delete(1L);

        mockMvc.perform(delete("/api/users/{id}", 1L))
                .andExpect(status().isNoContent());
    }
    @Test
    void testUpdate() throws Exception {
        given(userService.update(org.mockito.ArgumentMatchers.eq(1L), any(io.hexlet.spring.shopping.dto.UserUpdateDTO.class)))
                .willReturn(userDTO);
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON).content("{\"firstName\":\"Updated\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));
        org.mockito.Mockito.verify(userService).update(org.mockito.ArgumentMatchers.eq(1L), any(io.hexlet.spring.shopping.dto.UserUpdateDTO.class));
    }
}
