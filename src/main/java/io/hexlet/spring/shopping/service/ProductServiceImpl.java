package io.hexlet.spring.shopping.service;

import io.hexlet.spring.shopping.dto.ProductCreateDTO;
import io.hexlet.spring.shopping.dto.ProductDTO;
import io.hexlet.spring.shopping.dto.ProductUpdateDTO;
import io.hexlet.spring.shopping.exception.ResourceNotFoundException;
import io.hexlet.spring.shopping.mapper.ProductMapper;
import io.hexlet.spring.shopping.model.Product;
import io.hexlet.spring.shopping.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    @Transactional
    public ProductDTO create(ProductCreateDTO dto) {
        Product product = productMapper.map(dto);
        Product savedProduct = productRepository.save(product);
        return productMapper.map(savedProduct);
    }

    @Override
    public List<ProductDTO> getAll() {
        return productRepository.findAll().stream()
                .map(productMapper::map)
                .toList();
    }

    @Override
    public List<ProductDTO> getByListId(Long listId) {
        return productRepository.findByListId(listId).stream()
                .map(productMapper::map)
                .toList();
    }

    @Override
    public ProductDTO getById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + id));
        return productMapper.map(product);
    }

    @Override
    @Transactional
    public ProductDTO update(Long id, ProductUpdateDTO dto) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + id));
        productMapper.update(dto, product);
        Product savedProduct = productRepository.save(product);
        return productMapper.map(savedProduct);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product not found: " + id);
        }
        productRepository.deleteById(id);
    }
}
