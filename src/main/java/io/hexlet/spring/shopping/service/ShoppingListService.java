package io.hexlet.spring.shopping.service;

import io.hexlet.spring.shopping.dto.ShoppingListCreateDTO;
import io.hexlet.spring.shopping.dto.ShoppingListDTO;
import io.hexlet.spring.shopping.dto.ShoppingListUpdateDTO;

import java.util.List;

public interface ShoppingListService {

    ShoppingListDTO create(ShoppingListCreateDTO dto);

    List<ShoppingListDTO> getAll();

    ShoppingListDTO getById(Long id);

    ShoppingListDTO update(Long id, ShoppingListUpdateDTO dto);

    void delete(Long id);
}
