package io.hexlet.spring.shopping.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ShoppingListDTO {

    private Long id;

    private Long authorId;

    private String slug;

    private String name;

    private String description;

    private LocalDate createdAt;

    private LocalDate updatedAt;
}
