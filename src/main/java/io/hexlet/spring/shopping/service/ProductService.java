package io.hexlet.spring.shopping.service;

import io.hexlet.spring.shopping.dto.ProductCreateDTO;
import io.hexlet.spring.shopping.dto.ProductDTO;
import io.hexlet.spring.shopping.dto.ProductUpdateDTO;

import java.util.List;

public interface ProductService {

    ProductDTO create(ProductCreateDTO dto);

    List<ProductDTO> getAll();

    List<ProductDTO> getByListId(Long listId);

    ProductDTO getById(Long id);

    ProductDTO update(Long id, ProductUpdateDTO dto);

    void delete(Long id);
}
