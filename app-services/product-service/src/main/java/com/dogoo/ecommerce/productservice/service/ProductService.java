package com.dogoo.ecommerce.productservice.service;

import com.dogoo.ecommerce.product.api.model.Product;
import com.dogoo.ecommerce.productservice.entry.ProductEntry;
import com.dogoo.ecommerce.productservice.mapper.ProductMapper;
import com.dogoo.ecommerce.productservice.repository.ProductRepository;
import com.dogoo.exception.model.InvalidInputException;
import com.dogoo.exception.model.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.constructor.DuplicateKeyException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Random;
import java.util.UUID;
import java.util.logging.Level;

@Service
public class ProductService {

    private static final Logger log = LoggerFactory.getLogger(ProductService.class);

    private final ProductMapper mapper;

    private final ProductRepository repo;

    public ProductService(ProductMapper mapper, ProductRepository repo) {
        this.mapper = mapper;
        this.repo = repo;
    }

    public Mono<Product> getProductById(String productId, int delay, int faultPercent) {
        return Mono.just(productId)
                .map(UUID::fromString)
                .onErrorResume(ex -> Mono.error(new InvalidInputException("INVALID_UUID" + ex.getMessage())))
                .flatMap(repo::findById)
                .map(e -> throwErrorIfBadLuck(e, faultPercent))
                .switchIfEmpty(Mono.error(new NotFoundException("PRODUCT_NOT_FOUND")))
                .map(mapper::mapModelFromEntry)
                .log(log.getName(), Level.FINE)
                .doOnError(this::LOG)
                .delayElement(Duration.ofSeconds(delay)); //for demo
    }

    public Mono<Void> deleteProduct(String productId) {

        return Mono.just(productId)
                .map(UUID::fromString)
                .onErrorResume(ex -> Mono.error(new InvalidInputException("INVALID_UUID" + ex.getMessage())))
                .flatMap(repo::deleteById)
                .switchIfEmpty(Mono.error(new NotFoundException("PRODUCT_NOT_FOUND")))
                .log(log.getName(), Level.FINE)
                .doOnError(this::LOG);
    }

    public Mono<Product> postProduct(Product product) {

        return repo.existsByNameIgnoreCase(product.getName())
            .handle((input, sink) -> { //Valid for name not duplicate
                if (input) {
                    sink.error(new InvalidInputException("DUP_NAME"));
                }
            })
            .switchIfEmpty(Mono.just(new ProductEntry()))
            .map(e -> mapper.mapEntryFromModel(product))
            .flatMap(repo::save)
            .map(mapper::mapModelFromEntry)
            .log(log.getName(), Level.FINE)
            .onErrorMap(DuplicateKeyException.class,
                    ex -> new InvalidInputException("Duplicate key, Product Id"))
            .onErrorMap(Exception.class,
                    ex -> new InvalidInputException("Error " + ex.getMessage()))
            .doOnError(this::LOG); //to continuous process in case not found
    }

    public Mono<Product> saveProduct(String productId, Product product) {

        return Mono.just(productId)
            .map(UUID::fromString)
            .onErrorResume(ex -> Mono.error(new InvalidInputException("UUID_INVALID " + ex.getMessage()))) //Valid UUID
            .flatMap(repo::findById)
            .switchIfEmpty(Mono.error(new NotFoundException("PRODUCT_NOT_FOUND"))) //Valid exist
            .flatMap(e -> repo.findByNameIgnoreCase(product.getName())) 
            .switchIfEmpty(Mono.just(new ProductEntry())) //to continuous process in case not found
            .handle((input, sink) -> { //Valid for name not duplicate
                if (input.getId() != null && !input.getId().toString().equalsIgnoreCase(productId)) {
                    sink.error(new InvalidInputException("DUP_NAME"));
                } else {
                    sink.next(input);
                }
            })
            .flatMap(e -> repo.findById(UUID.fromString(productId)))
            .map(e -> mapper.mapToEntry(e, product))
            .flatMap(repo::save)
            .map(mapper::mapModelFromEntry)                    
            .log(log.getName(), Level.FINE)
            .onErrorMap(DuplicateKeyException.class,
                    ex -> new InvalidInputException("Duplicate key, Product Id"))
            .onErrorMap(Exception.class,
                    ex -> new InvalidInputException("Error " + ex.getMessage()))
            .doOnError(this::LOG);
    }

    public Flux<Product> searchProduct(String filter,
                                       Integer page,
                                       Integer pageSize,
                                       String search,
                                       String sort) {
        return repo.findAll().map(mapper::mapModelFromEntry);
    }


    private ProductEntry throwErrorIfBadLuck(ProductEntry entity,
                                             int faultPercent) {
        if (faultPercent == 0) {
            return entity;
        }

        int randomThreshold = getRandomNumber(1, 100);

        if (faultPercent < randomThreshold) {
            log.debug("We got lucky, no error occurred, {} < {}",
                    faultPercent, randomThreshold);
        } else {
            log.info("Bad luck, an error occurred, {} >= {}",
                    faultPercent, randomThreshold);
            throw new RuntimeException("Something went wrong...");
        }

        return entity;
    }

    private final Random randomNumberGenerator = new Random();

    private int getRandomNumber(int min, int max) {
        if (max < min) {
            throw new IllegalArgumentException("Max must be greater than min");
        }
        return randomNumberGenerator.nextInt((max - min) + 1) + min;
    }

    private void LOG(Throwable ex) {
        log.error("Exception {}", ex.getMessage());
    }

}
