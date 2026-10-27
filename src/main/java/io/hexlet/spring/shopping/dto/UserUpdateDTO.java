package io.hexlet.spring.shopping.dto;

import io.hexlet.spring.shopping.validation.UpdateValue;
import lombok.Getter;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

@Getter
@Setter
public class UserUpdateDTO {

    @UpdateValue(payload = jakarta.validation.valueextraction.Unwrapping.Skip.class)
    private JsonNullable<String> firstName;

    @UpdateValue(payload = jakarta.validation.valueextraction.Unwrapping.Skip.class)
    private JsonNullable<String> lastName;

    @UpdateValue(email = true, payload = jakarta.validation.valueextraction.Unwrapping.Skip.class)
    private JsonNullable<String> email;

    @UpdateValue(payload = jakarta.validation.valueextraction.Unwrapping.Skip.class)
    private JsonNullable<String> password;
}
