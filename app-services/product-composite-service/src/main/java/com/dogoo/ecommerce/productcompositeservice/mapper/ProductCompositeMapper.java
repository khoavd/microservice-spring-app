package com.dogoo.ecommerce.productcompositeservice.mapper;


import com.dogoo.ecommerce.product.client.model.Product;
import com.dogoo.ecommerce.product.composite.api.model.ProductComposite;
import com.dogoo.ecommerce.product.composite.api.model.RecommendationComposite;
import com.dogoo.ecommerce.product.composite.api.model.ReviewComposite;
import com.dogoo.ecommerce.recommendation.client.model.Recommendation;
import com.dogoo.ecommerce.review.client.model.Review;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class ProductCompositeMapper {
    public ProductComposite mapProductComposite(Product product,
                                                List<Review> review,
                                                List<Recommendation> recommendations) {

        ProductComposite source = new ProductComposite();

        if (product != null) {
            source.setId(product.getId());
            source.setName(product.getName());
            source.setWeight(product.getWeight());
            source.serviceAddress(product.getServiceAddress());
        }

        if (review != null && !review.isEmpty()) {
            source.setReviews(mapReviewListFromService(review));
        }

        if (recommendations != null && !recommendations.isEmpty()) {
            source.setRecommendations(mapRecommendationListFromService(recommendations));
        }

        return source;
    }

    public Product mapProductFromComposite(ProductComposite from) {
        Product to = new Product();

        to.setId(from.getId());
        to.setWeight(from.getWeight());
        to.setName(from.getName());

        return to;
    }

    public Review mapReviewFromComposite(String productId, ReviewComposite from) {
        Review to = new Review();

        if (from.getId() == null  || from.getId().isBlank() || from.getId().isEmpty()) {                    
            from.setId(UUID.randomUUID().toString());
        }
        
        to.setProductId(productId);
        to.setContent(from.getContent());
        to.setSubject(from.getSubject());
        to.setAuthor(from.getAuthor());
        to.setId(from.getId());

        return to;
    }

    public Recommendation mapRecommendationFromComposite(String productId, RecommendationComposite from) {
        Recommendation to = new Recommendation();
        
        if (from.getId() == null  || from.getId().isBlank() || from.getId().isEmpty()) {                    
            from.setId(UUID.randomUUID().toString());
        }

        to.setAuthor(from.getAuthor());
        to.setContent(from.getContent());
        to.setId(from.getId());
        to.setProductId(productId);
        to.setRate(from.getRate());

        return to;
    }

    private ReviewComposite mapReviewModelFromService(Review from) {
        ReviewComposite to = new ReviewComposite();

        to.setId(from.getId());
        to.setProductId(from.getProductId());
        to.setAuthor(from.getAuthor());
        to.setContent(from.getContent());
        to.setSubject(from.getSubject());

        return to;
    }

    public Recommendation mapRcmdFromComposite(RecommendationComposite from) {
        Recommendation to = new Recommendation();

        to.setId(from.getId());
        to.setProductId(from.getProductId());
        to.setAuthor(from.getAuthor());
        to.setContent(from.getContent());
        to.setRate(from.getRate());

        return to;
    }

    private RecommendationComposite mapRecModelFromService(Recommendation from) {
        RecommendationComposite to = new RecommendationComposite();

        to.setId(from.getId());
        to.setProductId(from.getProductId());
        to.setAuthor(from.getAuthor());
        to.setContent(from.getContent());
        to.setRate(from.getRate());

        return to;
    }

    private List<ReviewComposite> mapReviewListFromService(List<Review> from) {
        return from.stream().map(this::mapReviewModelFromService).collect(Collectors.toList());
    }

    private List<RecommendationComposite> mapRecommendationListFromService(List<Recommendation> from) {
        return from.stream().map(this::mapRecModelFromService).collect(Collectors.toList());
    }

}
