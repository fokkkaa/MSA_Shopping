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
class ShoppingListRepositoryTest {
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
        list = lists.save(ModelGenerator.buildShoppingList(user));
    }

    @Test
    void savesAndFindsById() {
        lists.flush();
        assertThat(list.getId()).isPositive();
        assertThat(lists.findById(list.getId())).contains(list);
    }

    @Test
    void findsAll() {
        assertThat(lists.findAll()).containsExactly(list);
    }

    @Test
    void findsByDomainField() {
        assertThat(lists.findBySlug(list.getSlug())).contains(list);
    }

    @Test
    void updates() {
        list.setName("Updated");
        lists.saveAndFlush(list);
        assertThat(lists.findById(list.getId()).orElseThrow().getName()).isEqualTo("Updated");
    }

    @Test
    void deletes() {
        lists.deleteById(list.getId());
        lists.flush();
        assertThat(lists.findById(list.getId())).isEmpty();
    }

}
