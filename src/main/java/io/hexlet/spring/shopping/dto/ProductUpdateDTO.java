package io.hexlet.spring.shopping.dto;

import io.hexlet.spring.shopping.validation.UpdateValue;
import lombok.Getter;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

@Getter
@Setter
public class ProductUpdateDTO {

    @UpdateValue(positive = true, payload = jakarta.validation.valueextraction.Unwrapping.Skip.class)
    private JsonNullable<Long> listId;

    @UpdateValue(positive = true, payload = jakarta.validation.valueextraction.Unwrapping.Skip.class)
    private JsonNullable<Long> authorId;

    @UpdateValue(payload = jakarta.validation.valueextraction.Unwrapping.Skip.class)
    private JsonNullable<String> name;

    @UpdateValue(positive = true, payload = jakarta.validation.valueextraction.Unwrapping.Skip.class)
    private JsonNullable<Integer> quantity;

    @UpdateValue(payload = jakarta.validation.valueextraction.Unwrapping.Skip.class)
    private JsonNullable<String> unit;

    @UpdateValue(payload = jakarta.validation.valueextraction.Unwrapping.Skip.class)
    private JsonNullable<Boolean> purchased;
}
