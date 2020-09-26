package adapter.coordinateValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * Configuring the interface for a special constraint class
 */
@Documented
@Constraint(validatedBy = CoordinateValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface CoordinateConstraint {
    String message() default "Invalid coordinate values";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
