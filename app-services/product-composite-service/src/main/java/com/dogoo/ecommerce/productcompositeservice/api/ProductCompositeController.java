package com.dogoo.ecommerce.productcompositeservice.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

import com.dogoo.ecommerce.product.composite.api.ProductsCompositeApi;
import com.dogoo.ecommerce.product.composite.api.model.ProductComposite;
import com.dogoo.ecommerce.productcompositeservice.service.ProductCompositeService;
import com.dogoo.ecommerce.productcompositeservice.validator.ProductCompositeValidator;

import reactor.core.publisher.Mono;


@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1")
public class ProductCompositeController implements ProductsCompositeApi {

    private final Logger LOG = LoggerFactory.getLogger(ProductCompositeController.class);

    private final ProductCompositeService service;

    private final ProductCompositeValidator validator;

    public ProductCompositeController(ProductCompositeService service,
                                      ProductCompositeValidator validator) {
        this.service = service;
        this.validator = validator;
    }

    @Override
    @PreAuthorize("hasAnyRole('MODERATOR', 'USER', 'ADMIN')")
    public Mono<ResponseEntity<ProductComposite>> getProductComposites(String productId,
                                                                       Integer delay,
                                                                       Integer faultPercent,
                                                                       ServerWebExchange exchange) {
        return service.getProductById(productId, delay, faultPercent)
                .map(e -> new ResponseEntity<>(e, HttpStatus.OK));
    }

    @Override
    public Mono<ResponseEntity<ProductComposite>> addProductComposite(Mono<ProductComposite> productComposite,
                                                                      ServerWebExchange exchange) {

        return productComposite
                    .filter(validator::validateForAdd)
                    .flatMap(service::addProductComposite)
                    .map(d -> new ResponseEntity<>(d, HttpStatus.OK));
    }

    @Override
    public Mono<ResponseEntity<Void>> deleteProductComposite(String productId,
                                                             ServerWebExchange exchange) {
        return service.deleteProduct(productId).map(e -> new ResponseEntity<>(HttpStatus.OK));
    }

    @Override
    public Mono<ResponseEntity<ProductComposite>> updateProductComposite(String productId,
                                                                         Mono<ProductComposite> productComposite,
                                                                         ServerWebExchange exchange) {
        return productComposite
            .filter(validator::validateForAdd)
            .flatMap(d -> service.updateProductComposite(productId, d))
            .map(d -> new ResponseEntity<>(d, HttpStatus.OK));
    }
}
