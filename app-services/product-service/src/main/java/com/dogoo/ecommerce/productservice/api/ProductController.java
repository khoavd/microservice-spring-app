package com.dogoo.ecommerce.productservice.api;

import com.dogoo.ecommerce.product.api.ProductsApi;
import com.dogoo.ecommerce.product.api.model.Product;
import com.dogoo.ecommerce.productservice.service.ProductService;
import com.dogoo.ecommerce.productservice.validator.ProductValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1")
public class ProductController implements ProductsApi {

    private final Logger log = LoggerFactory.getLogger(ProductController.class);

    private final ProductService service;

    private final ProductValidator validator;

    public ProductController(ProductService service,
                             ProductValidator validator) {
        this.service = service;
        this.validator = validator;
    }

    @Override
    public Mono<ResponseEntity<Product>> getProduct(String productId,
                                                    Integer delay,
                                                    Integer faultPercent,
                                                    ServerWebExchange exchange) {

        return service.getProductById(productId, delay, faultPercent).map(e -> new ResponseEntity<>(e, HttpStatus.OK));
    }

    @Override
    public Mono<ResponseEntity<Void>> deleteProduct(String productId,
                                                    ServerWebExchange exchange) {

        return service.deleteProduct(productId).map(e -> new ResponseEntity<>(e, HttpStatus.OK));
    }

    @Override
    public Mono<ResponseEntity<Flux<Product>>> getProducts(String filter,
                                                           Integer page,
                                                           Integer pageSize,
                                                           String search,
                                                           String sort,
                                                           ServerWebExchange exchange) {
        return Mono.just(
                new ResponseEntity<>(
                        service.searchProduct(filter, page, pageSize, search, sort),
                        HttpStatus.OK));
    }

    @Override
    public Mono<ResponseEntity<Product>> postProduct(Mono<Product> product,
                                                     ServerWebExchange exchange) {

        return product
                .filter(validator::validateForAdd)
                .flatMap(service::postProduct).map(p -> new ResponseEntity<>(p, HttpStatus.OK));

    }

    @Override
    public Mono<ResponseEntity<Product>> putProduct(String productId,
                                                    Mono<Product> product,
                                                    ServerWebExchange exchange) {
        return product
                .filter(validator::validateForAdd)
                .flatMap(p -> service.saveProduct(productId, p))
                .map(np -> new ResponseEntity<>(np, HttpStatus.OK));
    }
}
