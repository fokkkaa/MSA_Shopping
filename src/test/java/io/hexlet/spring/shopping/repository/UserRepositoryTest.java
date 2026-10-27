package io.hexlet.spring.shopping.repository;

import io.hexlet.spring.shopping.model.*;
import io.hexlet.spring.shopping.util.ModelGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {
    @Autowired private UserRepository users;
    @Autowired private ShoppingListRepository lists;
    @Autowired private ProductRepository products;
    private User user;
    private ShoppingList list;
    private Product product;

    @BeforeEach
    void setUp() {
        products.deleteAll();
        lists.deleteAll();
        users.deleteAll();
        user = users.save(ModelGenerator.buildUser());
    }

    @Test
    void savesAndFindsById() {
        users.flush();
        assertThat(user.getId()).isPositive();
        assertThat(users.findById(user.getId())).contains(user);
    }

    @Test
    void findsAll() {
        assertThat(users.findAll()).containsExactly(user);
    }

    @Test
    void findsByDomainField() {
        assertThat(users.findByEmail(user.getEmail())).contains(user);
    }

    @Test
    void updates() {
        user.setFirstName("Updated");
        users.saveAndFlush(user);
        assertThat(users.findById(user.getId()).orElseThrow().getFirstName()).isEqualTo("Updated");
    }

    @Test
    void deletes() {
        users.deleteById(user.getId());
        users.flush();
        assertThat(users.findById(user.getId())).isEmpty();
    }

}
