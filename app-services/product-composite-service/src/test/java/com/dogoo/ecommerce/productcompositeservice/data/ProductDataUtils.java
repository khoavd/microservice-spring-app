package com.dogoo.ecommerce.productcompositeservice.data;

import com.dogoo.ecommerce.product.client.model.Product;
import com.dogoo.ecommerce.recommendation.client.model.Recommendation;
import com.dogoo.ecommerce.review.client.model.Review;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class ProductDataUtils {

    public static final String ID = "7abbd845-e078-477c-8d8a-d4508666ca5a";

    public static final String NAME = "PROD_NAME";

    public static final String SUBJECT = "PROD_SUBJECT";

    public static final String CONTENT = "CONTENT";

    public static final String AUTH = "AUTH";

    public static final Double RATE = 1.0;

    public static final Double WEIGHT = 1.0;

    public static Mono<Product> buildProduct() {
        Product data = new Product();

        data.setName(NAME);
        data.setWeight(WEIGHT);
        data.setId(ID);

        return Mono.just(data);
    }

    private static Review buildReview() {
        Review data = new Review();

        data.setId(ID);
        data.setAuthor(AUTH);
        data.setSubject(SUBJECT);
        data.setContent(CONTENT);

        return data;
    }

    public static Flux<Review> buildReviews() {
        return Flux.just(buildReview());
    }

    private static Recommendation buildRecommendation() {
        Recommendation data = new Recommendation();

        data.setAuthor(AUTH);
        data.setContent(CONTENT);
        data.setRate(RATE);

        return data;
    }

    public static Flux<Recommendation> buildRecommendations() {
        return Flux.just(buildRecommendation());
    }
}
