package com.dogoo.ecommerce.productcompositeservice.service;


import com.dogoo.ecommerce.product.client.ApiClient;
import com.dogoo.ecommerce.product.client.api.ProductApi;
import com.dogoo.ecommerce.product.client.model.Product;
import com.dogoo.ecommerce.productcompositeservice.config.ServiceProperties;
import com.dogoo.exception.HandlerUtils;

import java.util.logging.Level;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import jakarta.ws.rs.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.health.Health;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Service
public class ProductApiService {

    private final Logger LOG = LoggerFactory.getLogger(ProductApiService.class);

    private final ServiceProperties properties;

    private final HandlerUtils utils;

    private final WebClient webClient;

    public ProductApiService(ServiceProperties properties, 
                             HandlerUtils utils,
                             WebClient webClient) {
        this.properties = properties;
        this.utils = utils;
        this.webClient = webClient;
    }

    @Retry(name = "product")
    @TimeLimiter(name = "product")
    @CircuitBreaker(name = "product", fallbackMethod = "getProductFallbackValue")
    public Mono<Product> getProductById(String productId, int delay, int faultPercent) {

        ApiClient apiClient = new ApiClient(webClient);

        String port = properties.getProduct().getPort() == 0 ? "" : ":" + properties.getProduct().getPort();

        apiClient.setBasePath(properties.getProduct().getScheme() + "://" +
                properties.getProduct().getHost() + port + "/api/v1");

        ProductApi productApi = new ProductApi(apiClient);

        return productApi.getProduct(productId, delay, faultPercent)
                .onErrorMap(WebClientResponseException.class, utils::handleException);

    }

    private Mono<Product> getProductFallbackValue(String productId, int delay, int faultPercent, CallNotPermittedException ex) {

        LOG.warn("Creating a fail-fast fallback product for productId = {}, delay = {}, faultPercent = {} and exception = {} ",
                productId, delay, faultPercent, ex.toString());

        if (productId.contentEquals("58102aa3-8b33-4eb2-936e-a0b6ad1a50f6")) {
            String errMsg = "Product Id: " + productId + " not found in fallback cache!";
            LOG.warn(errMsg);
            throw new NotFoundException(errMsg);
        }

        Product product = new Product();

        product.setId(productId);
        product.setName("Fallback product");
        product.setWeight(2.0);

        return Mono.just(product);
    }


    public Mono<Health> getProductHealth() {
        
        String port = properties.getProduct().getPort() == 0 ? "" : ":" + properties.getProduct().getPort();

        String url = properties.getProduct().getScheme() + "://" +
                     properties.getProduct().getHost() + port + "/actuator/health";
        
        LOG.debug("Will call the Health API on URL: {}", url);
        
        return webClient.get().uri(url).retrieve().bodyToMono(String.class)
            .map(s -> new Health.Builder().up().build())
            .onErrorResume(ex -> Mono.just(new Health.Builder().down(ex).build()))
            .log(LOG.getName(), Level.FINE);
    }

}
