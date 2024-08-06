package com.dogoo.ecommerce.reviewservice.repository;

import com.dogoo.ecommerce.reviewservice.entry.ReviewEntry;

import java.util.UUID;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface ReviewRepository extends ReactiveCrudRepository<ReviewEntry, UUID> {
    Flux<ReviewEntry> getAllByProductId(String productId);
}
