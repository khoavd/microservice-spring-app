package com.dogoo.ecommerce.productservice.mapper;

import com.dogoo.common.lib.ServiceUtil;
import com.dogoo.ecommerce.product.api.model.Product;
import com.dogoo.ecommerce.productservice.entry.ProductEntry;
import com.dogoo.ecommerce.productservice.repository.ProductRepository;
import com.dogoo.exception.model.InternalServerException;
import com.dogoo.exception.model.NotFoundException;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class ProductMapper {

    private final ProductRepository repo;
    
    private final ServiceUtil serviceUtil;

    public ProductMapper(ProductRepository repo,
                         ServiceUtil serviceUtil) {
        this.repo = repo;
        this.serviceUtil = serviceUtil;
    }

    public Product mapModelFromEntry(ProductEntry from) {
        Product to = new Product();

        to.setId(from.getId().toString());
        to.setName(from.getName());
        to.setWeight(from.getWeight());
        to.serviceAddress(serviceUtil.getServiceAddress());

        return to;
    }

    public ProductEntry mapEntryFromModel(Product from) {
        ProductEntry to = new ProductEntry();

        to.setId(UUID.fromString(from.getId()));
        to.setName(from.getName());
        to.setWeight(from.getWeight());

        return to;
    }

    public Mono<ProductEntry> mapEntryFromModel(String productId, Product from) {

        try {
            return repo.findById(UUID.fromString(productId))
                    .switchIfEmpty(Mono.error(new NotFoundException("PRODUCT_NOT_FOUND")))
                    .map(e -> mapToEntry(e, from));
        } catch (Exception e) {
            throw new InternalServerException(e.getMessage());
        }
    }

    public ProductEntry mapToEntry(ProductEntry src, Product from) {

        src.setUpdatedDate(LocalDateTime.now());
        src.setWeight(from.getWeight());
        src.setName(from.getName());

        return src;
    }
}
