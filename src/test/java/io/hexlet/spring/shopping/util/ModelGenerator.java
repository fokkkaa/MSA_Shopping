package io.hexlet.spring.shopping.util;

import com.github.javafaker.Faker;
import io.hexlet.spring.shopping.dto.ProductCreateDTO;
import io.hexlet.spring.shopping.dto.ShoppingListCreateDTO;
import io.hexlet.spring.shopping.dto.UserCreateDTO;
import io.hexlet.spring.shopping.model.Product;
import io.hexlet.spring.shopping.model.ShoppingList;
import io.hexlet.spring.shopping.model.User;
import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Locale;

import static org.instancio.Select.field;

@ExtendWith(InstancioExtension.class)
public class ModelGenerator {

    private static final Faker FAKER = new Faker(new Locale("en-US"));
    private static final String[] UNITS = {"шт", "кг", "л"};

    public static UserCreateDTO buildUserCreateDTO() {
        return Instancio.of(UserCreateDTO.class)
                .supply(field(UserCreateDTO::getFirstName), () -> FAKER.name().firstName())
                .supply(field(UserCreateDTO::getLastName), () -> FAKER.name().lastName())
                .supply(field(UserCreateDTO::getEmail), () -> FAKER.internet().emailAddress())
                .supply(field(UserCreateDTO::getPassword), () -> FAKER.internet().password(6, 20))
                .create();
    }

    public static User buildUser() {
        return Instancio.of(User.class)
                .ignore(field(User::getId))
                .ignore(field(User::getCreatedAt))
                .ignore(field(User::getUpdatedAt))
                .supply(field(User::getFirstName), () -> FAKER.name().firstName())
                .supply(field(User::getLastName), () -> FAKER.name().lastName())
                .supply(field(User::getEmail), () -> FAKER.internet().emailAddress())
                .supply(field(User::getPassword), () -> FAKER.internet().password(6, 20))
                .create();
    }

    public static ShoppingListCreateDTO buildShoppingListCreateDTO(Long authorId) {
        String listName = FAKER.commerce().department();
        String slug = listName.toLowerCase()
                .replaceAll("[^a-z0-9а-я]+", "-")
                .replaceAll("(^-|-$)", "")
                + "-" + FAKER.number().randomNumber(4, true);
        return Instancio.of(ShoppingListCreateDTO.class)
                .supply(field(ShoppingListCreateDTO::getAuthorId), () -> authorId)
                .supply(field(ShoppingListCreateDTO::getSlug), () -> slug)
                .supply(field(ShoppingListCreateDTO::getName), () -> listName)
                .supply(field(ShoppingListCreateDTO::getDescription), () -> FAKER.lorem().paragraph(3))
                .create();
    }

    public static ShoppingList buildShoppingList(User author) {
        String listName = FAKER.commerce().department();
        String slug = listName.toLowerCase()
                .replaceAll("[^a-z0-9а-я]+", "-")
                .replaceAll("(^-|-$)", "")
                + "-" + FAKER.number().randomNumber(4, true);
        return Instancio.of(ShoppingList.class)
                .ignore(field(ShoppingList::getId))
                .ignore(field(ShoppingList::getCreatedAt))
                .ignore(field(ShoppingList::getUpdatedAt))
                .supply(field(ShoppingList::getAuthor), () -> author)
                .supply(field(ShoppingList::getSlug), () -> slug)
                .supply(field(ShoppingList::getName), () -> listName)
                .supply(field(ShoppingList::getDescription), () -> FAKER.lorem().paragraph(3))
                .create();
    }

    public static ProductCreateDTO buildProductCreateDTO(Long listId, Long authorId) {
        return Instancio.of(ProductCreateDTO.class)
                .supply(field(ProductCreateDTO::getListId), () -> listId)
                .supply(field(ProductCreateDTO::getAuthorId), () -> authorId)
                .supply(field(ProductCreateDTO::getName), () -> FAKER.food().ingredient())
                .supply(field(ProductCreateDTO::getQuantity), () -> FAKER.number().numberBetween(1, 10))
                .supply(field(ProductCreateDTO::getUnit), () -> UNITS[FAKER.number().numberBetween(0, UNITS.length - 1)])
                .create();
    }

    public static Product buildProduct(ShoppingList list, User author) {
        return Instancio.of(Product.class)
                .ignore(field(Product::getId))
                .ignore(field(Product::getCreatedAt))
                .ignore(field(Product::getUpdatedAt))
                .supply(field(Product::getList), () -> list)
                .supply(field(Product::getAuthor), () -> author)
                .supply(field(Product::getName), () -> FAKER.food().ingredient())
                .supply(field(Product::getQuantity), () -> FAKER.number().numberBetween(1, 10))
                .supply(field(Product::getUnit), () -> UNITS[FAKER.number().numberBetween(0, UNITS.length - 1)])
                .supply(field(Product::getPurchased), () -> false)
                .create();
    }
}
