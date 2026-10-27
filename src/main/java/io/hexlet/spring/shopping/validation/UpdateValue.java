package io.hexlet.spring.shopping.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UpdateValueValidator.class)
public @interface UpdateValue {
    String message() default "must contain a valid non-null value";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    boolean positive() default false;
    boolean email() default false;
}
