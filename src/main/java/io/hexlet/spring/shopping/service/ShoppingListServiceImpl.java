package io.hexlet.spring.shopping.service;

import io.hexlet.spring.shopping.dto.ShoppingListCreateDTO;
import io.hexlet.spring.shopping.dto.ShoppingListDTO;
import io.hexlet.spring.shopping.dto.ShoppingListUpdateDTO;
import io.hexlet.spring.shopping.exception.ResourceNotFoundException;
import io.hexlet.spring.shopping.mapper.ShoppingListMapper;
import io.hexlet.spring.shopping.model.ShoppingList;
import io.hexlet.spring.shopping.repository.ShoppingListRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ShoppingListServiceImpl implements ShoppingListService {

    private final ShoppingListRepository shoppingListRepository;
    private final io.hexlet.spring.shopping.repository.ProductRepository productRepository;
    private final ShoppingListMapper shoppingListMapper;

    @Override
    @Transactional
    public ShoppingListDTO create(ShoppingListCreateDTO dto) {
        ShoppingList list = shoppingListMapper.map(dto);
        ShoppingList savedList = shoppingListRepository.save(list);
        return shoppingListMapper.map(savedList);
    }

    @Override
    public List<ShoppingListDTO> getAll() {
        return shoppingListRepository.findAll().stream()
                .map(shoppingListMapper::map)
                .toList();
    }

    @Override
    public ShoppingListDTO getById(Long id) {
        ShoppingList list = shoppingListRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ShoppingList not found: " + id));
        return shoppingListMapper.map(list);
    }

    @Override
    @Transactional
    public ShoppingListDTO update(Long id, ShoppingListUpdateDTO dto) {
        ShoppingList list = shoppingListRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ShoppingList not found: " + id));
        shoppingListMapper.update(dto, list);
        ShoppingList savedList = shoppingListRepository.save(list);
        return shoppingListMapper.map(savedList);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!shoppingListRepository.existsById(id)) {
            throw new ResourceNotFoundException("ShoppingList not found: " + id);
        }
        productRepository.deleteByListId(id);
        productRepository.flush();
        shoppingListRepository.deleteById(id);
    }
}
