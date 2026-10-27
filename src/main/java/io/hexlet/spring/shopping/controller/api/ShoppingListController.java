package io.hexlet.spring.shopping.controller.api;

import io.hexlet.spring.shopping.dto.ProductDTO;
import io.hexlet.spring.shopping.dto.ShoppingListCreateDTO;
import io.hexlet.spring.shopping.dto.ShoppingListDTO;
import io.hexlet.spring.shopping.dto.ShoppingListUpdateDTO;
import io.hexlet.spring.shopping.service.ProductService;
import io.hexlet.spring.shopping.service.ShoppingListService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/lists")
@RequiredArgsConstructor
public class ShoppingListController {

    private final ShoppingListService shoppingListService;
    private final ProductService productService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ShoppingListDTO> index() {
        return shoppingListService.getAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ShoppingListDTO create(@Valid @RequestBody ShoppingListCreateDTO dto) {
        return shoppingListService.create(dto);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ShoppingListDTO show(@PathVariable Long id) {
        return shoppingListService.getById(id);
    }

    @GetMapping("/{listId}/products")
    @ResponseStatus(HttpStatus.OK)
    public List<ProductDTO> getProducts(@PathVariable Long listId) {
        return productService.getByListId(listId);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ShoppingListDTO update(@PathVariable Long id, @Valid @RequestBody ShoppingListUpdateDTO dto) {
        return shoppingListService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void destroy(@PathVariable Long id) {
        shoppingListService.delete(id);
    }
}
