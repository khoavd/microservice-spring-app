package com.dogoo.ecommerce.productservice.repository;

import com.dogoo.ecommerce.productservice.entry.ProductEntry;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface ProductRepository extends ReactiveCrudRepository<ProductEntry, UUID> {

    Mono<Boolean> existsByNameIgnoreCase(String name);

    Mono<Boolean> existsByNameIgnoreCaseAndIdNot(String name, UUID id);


    Mono<ProductEntry> findByNameIgnoreCase(String name);

}
