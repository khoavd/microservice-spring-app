package com.dogoo.ecommerce.productcompositeservice.config;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.service")
public class ServiceProperties {
    private ServiceInfo product;

    private ServiceInfo review;

    private ServiceInfo recommendation;

    public ServiceInfo getProduct() {
        return product;
    }

    public void setProduct(ServiceInfo product) {
        this.product = product;
    }

    public ServiceInfo getReview() {
        return review;
    }

    public void setReview(ServiceInfo review) {
        this.review = review;
    }

    public ServiceInfo getRecommendation() {
        return recommendation;
    }

    public void setRecommendation(ServiceInfo recommendation) {
        this.recommendation = recommendation;
    }
}
