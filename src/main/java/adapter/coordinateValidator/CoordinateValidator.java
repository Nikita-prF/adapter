package adapter.coordinateValidator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Map;

/**
 * Constraint class for coordinate validation
 */
public class CoordinateValidator implements ConstraintValidator<CoordinateConstraint, Map<String, String>> {
    @Override
    public void initialize(CoordinateConstraint constraint ) {
    }

    /**
     * The method checks the received coordinates by comparing them with
     * admissible values for latitude and longitude
     *
     * @param crd Map class with coordinate values
     * @param context Provides contextual data and operation when applying a given constraint validator
     * @return boolean value as result of comparing
     */
    @Override
    public boolean isValid(Map<String, String> crd, ConstraintValidatorContext context) {
        return !crd.isEmpty() && crd.get("latitude") != null && crd.get("longitude") != null &&
                Double.parseDouble(crd.get("latitude")) >= -90 && Double.parseDouble(crd.get("latitude")) <= 90 &&
                Double.parseDouble(crd.get("longitude")) >= -180 && Double.parseDouble(crd.get("longitude")) <= 180;
    }


}
