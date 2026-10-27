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
class ProductRepositoryTest {
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
        product = products.save(ModelGenerator.buildProduct(list, user));
    }

    @Test
    void savesAndFindsById() {
        products.flush();
        assertThat(product.getId()).isPositive();
        assertThat(products.findById(product.getId())).contains(product);
    }

    @Test
    void findsAll() {
        assertThat(products.findAll()).containsExactly(product);
    }

    @Test
    void findsByDomainField() {
        assertThat(products.findByListId(list.getId())).containsExactly(product);
    }

    @Test
    void updates() {
        product.setName("Updated");
        products.saveAndFlush(product);
        assertThat(products.findById(product.getId()).orElseThrow().getName()).isEqualTo("Updated");
    }

    @Test
    void deletes() {
        products.deleteById(product.getId());
        products.flush();
        assertThat(products.findById(product.getId())).isEmpty();
    }

    @Test
    void filtersBothPurchasedStates() {
        Product bought = ModelGenerator.buildProduct(list, user);
        bought.setPurchased(true);
        products.saveAndFlush(bought);
        assertThat(products.findByListIdAndPurchased(list.getId(), true)).containsExactly(bought);
        assertThat(products.findByListIdAndPurchased(list.getId(), false)).containsExactly(product);
    }
}
