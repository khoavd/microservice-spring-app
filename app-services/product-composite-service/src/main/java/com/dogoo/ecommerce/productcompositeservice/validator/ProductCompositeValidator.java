package com.dogoo.ecommerce.productcompositeservice.validator;

import java.util.Objects;

import org.springframework.stereotype.Component;
import com.dogoo.ecommerce.product.composite.api.model.ProductComposite;
import com.dogoo.ecommerce.productcompositeservice.utils.ProductKeys;
import com.dogoo.exception.model.InvalidInputException;

@Component
public class ProductCompositeValidator {
    
    public boolean validateForAdd(ProductComposite product) {

        validateIsNotNull(ProductKeys.NAME, product.getName(), ProductKeys.IS_REQUIRED);
        validateIsNotNull(ProductKeys.WEIGHT, product.getWeight(), ProductKeys.IS_REQUIRED);

        return true;
    }


    public void validateIsNotNull(String fieldName, String fieldVal, String message) {
        if (!isEmptyString(fieldVal)) {
            return;
        }

        throw new InvalidInputException(fieldName + message);
    }

    public void validateIsNotNull(String fieldName, Object fieldVal, String message) {
        if (!Objects.isNull(fieldVal)) {
            return;
        }

        throw new InvalidInputException(fieldName + message);
    }

    private boolean isEmptyString(String string) {
        return string == null || string.isEmpty() || string.isBlank();
    }
}
