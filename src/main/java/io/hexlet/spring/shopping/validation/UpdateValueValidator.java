package io.hexlet.spring.shopping.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.openapitools.jackson.nullable.JsonNullable;

public class UpdateValueValidator implements ConstraintValidator<UpdateValue, JsonNullable<?>> {
    @org.springframework.beans.factory.annotation.Autowired
    private jakarta.validation.Validator validator;

    private boolean positive;
    private boolean email;

    @Override
    public void initialize(UpdateValue rule) {
        positive = rule.positive();
        email = rule.email();
    }

    @Override
    public boolean isValid(JsonNullable<?> field, ConstraintValidatorContext context) {
        if (field == null || !field.isPresent()) {
            return true;
        }
        Object value = field.orElse(null);
        if (value == null || value instanceof String text && text.isBlank()) {
            return false;
        }
        if (positive && (!(value instanceof Number number) || number.longValue() <= 0)) {
            return false;
        }
        return !email || value instanceof String text
                && validEmail(text);
    }

    private static class EmailValue {
        @jakarta.validation.constraints.Email
        private String value;
    }

    private boolean validEmail(String text) {
        return validator.validateValue(EmailValue.class, "value", text).isEmpty();
    }
}
