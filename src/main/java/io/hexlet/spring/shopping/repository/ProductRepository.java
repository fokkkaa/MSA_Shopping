package io.hexlet.spring.shopping.repository;

import io.hexlet.spring.shopping.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByListId(Long listId);

    List<Product> findByListIdAndPurchased(Long listId, Boolean purchased);
    void deleteByListId(Long listId);

    boolean existsByAuthorId(Long authorId);
}
