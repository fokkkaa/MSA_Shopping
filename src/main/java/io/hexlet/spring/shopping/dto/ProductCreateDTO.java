package io.hexlet.spring.shopping.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductCreateDTO {

    @NotNull
    @Positive
    private Long listId;

    @NotNull
    @Positive
    private Long authorId;

    @NotBlank
    private String name;

    @NotNull
    @Positive
    private Integer quantity;

    @NotBlank
    private String unit;
}
