package com.dogoo.ecommerce.productservice.consumer;

import com.dogoo.common.lib.Event;
import com.dogoo.ecommerce.product.api.model.Product;
import com.dogoo.ecommerce.productservice.service.ProductService;
import com.dogoo.exception.model.EventProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Configuration
public class ProductMessageProcess {
    private static final Logger LOG = LoggerFactory.getLogger(ProductMessageProcess.class);

    private final ProductService service;

    public ProductMessageProcess(ProductService service) {
        this.service = service;
    }

    @Bean
    public Consumer<Event<String, Product>> messageProcessor() {
        return event -> {
            LOG.info("Process message created at {}...", event.getEventCreatedAt());

            switch (event.getEventType()) {

                case CREATE:
                    Product product = event.getData();
                    LOG.info("Create product with ID: {}", product.getId());
                    service.postProduct(product).block();
                    break;

                case DELETE:
                    String productId = event.getKey();
                    LOG.info("Delete product with ProductID: {}", productId);
                    service.deleteProduct(productId).block();
                    break;

                case UPDATE:
                    productId = event.getKey();
                    product = event.getData();
                    LOG.info("Update product with ProductID: {}", productId);
                    service.saveProduct(productId, product).block();
                    break;

                default:
                    String errorMessage = "Incorrect event type: " + event.getEventType() + ", expected a CREATE or DELETE event";
                    LOG.warn(errorMessage);
                    throw new EventProcessingException(errorMessage);
            }

            LOG.info("Message processing done!");

        };
    }
}
