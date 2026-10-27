package io.hexlet.spring.shopping.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShoppingListCreateDTO {

    @NotNull
    @Positive
    private Long authorId;

    @NotBlank
    private String slug;

    @NotBlank
    private String name;

    @NotBlank
    private String description;
}
