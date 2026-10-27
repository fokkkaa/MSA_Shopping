package io.hexlet.spring.shopping.repository;

import io.hexlet.spring.shopping.model.ShoppingList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShoppingListRepository extends JpaRepository<ShoppingList, Long> {

    Optional<ShoppingList> findBySlug(String slug);
    boolean existsByAuthorId(Long authorId);
}
