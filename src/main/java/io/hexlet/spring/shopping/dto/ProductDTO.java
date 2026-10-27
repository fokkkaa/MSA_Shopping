package io.hexlet.spring.shopping.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ProductDTO {

    private Long id;

    private Long listId;

    private Long authorId;

    private String name;

    private Integer quantity;

    private String unit;

    private Boolean purchased;

    private LocalDate createdAt;
}
