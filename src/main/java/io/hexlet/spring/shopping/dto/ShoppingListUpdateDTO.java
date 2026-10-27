package io.hexlet.spring.shopping.dto;

import io.hexlet.spring.shopping.validation.UpdateValue;
import lombok.Getter;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

@Getter
@Setter
public class ShoppingListUpdateDTO {

    @UpdateValue(positive = true, payload = jakarta.validation.valueextraction.Unwrapping.Skip.class)
    private JsonNullable<Long> authorId;

    @UpdateValue(payload = jakarta.validation.valueextraction.Unwrapping.Skip.class)
    private JsonNullable<String> slug;

    @UpdateValue(payload = jakarta.validation.valueextraction.Unwrapping.Skip.class)
    private JsonNullable<String> name;

    @UpdateValue(payload = jakarta.validation.valueextraction.Unwrapping.Skip.class)
    private JsonNullable<String> description;
}
