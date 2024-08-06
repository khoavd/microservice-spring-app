package com.dogoo.ecommerce.productservice.validator;

import com.dogoo.ecommerce.product.api.model.Product;
import com.dogoo.ecommerce.productservice.utils.ProductKeys;
import com.dogoo.exception.model.InvalidInputException;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class ProductValidator {

    public boolean validateForAdd(Product product) {

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
