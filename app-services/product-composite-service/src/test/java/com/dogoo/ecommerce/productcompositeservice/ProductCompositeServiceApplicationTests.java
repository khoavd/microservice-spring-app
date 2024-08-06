package com.dogoo.ecommerce.productcompositeservice;

import static java.util.Collections.singletonList;
import static org.mockito.Mockito.when;

import com.dogoo.ecommerce.productcompositeservice.data.ProductDataUtils;
import com.dogoo.ecommerce.productcompositeservice.service.ProductApiService;
import com.dogoo.ecommerce.productcompositeservice.service.RecommendationApiService;
import com.dogoo.ecommerce.productcompositeservice.service.ReviewApiService;
import com.dogoo.exception.model.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(
		webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
		classes = {TestSecurityConfig.class},
		properties = {
				"eureka.client.enabled=false",
				"spring.main.allow-bean-definition-overriding=true",
				"spring.cloud.config.enabled=false"})
class ProductCompositeServiceApplicationTests {

	private static final String PRODUCT_ID_OK = "7abbd845-e078-477c-8d8a-d4508666ca5a";
	private static final String PRODUCT_ID_NOT_FOUND = "fa646cac-5aaf-4cb8-ade2-d9f844ead81b";

	private static final int DELAY = 1;
	private static final int FAULT_PERCENT = 50;

	@Autowired
	private WebTestClient client;

	@MockBean
	private ProductApiService productApiService;

	@MockBean
	private ReviewApiService reviewApiService;

	@MockBean
	private RecommendationApiService recApiService;

	@BeforeEach
	void setUp() {
		when(productApiService.getProductById(PRODUCT_ID_OK, DELAY, FAULT_PERCENT)).thenReturn(ProductDataUtils.buildProduct());
		when(reviewApiService.getReviewsByProductId(PRODUCT_ID_OK)).thenReturn(ProductDataUtils.buildReviews());
		when(recApiService.getRecommendationsByProductId(PRODUCT_ID_OK)).thenReturn(ProductDataUtils.buildRecommendations());

		when(productApiService.getProductById(PRODUCT_ID_NOT_FOUND, DELAY, FAULT_PERCENT)).thenThrow(new NotFoundException("NOT_FOUND"));
		when(reviewApiService.getReviewsByProductId(PRODUCT_ID_NOT_FOUND)).thenReturn(ProductDataUtils.buildReviews());
		when(recApiService.getRecommendationsByProductId(PRODUCT_ID_NOT_FOUND)).thenReturn(ProductDataUtils.buildRecommendations());
	}

	@Test
	void contextLoads() {
	}

	@Test
	@WithMockUser(roles= {"ADMIN", "MODERATOR", "USER"})
	void getProductById() {

		client.get()
				.uri("/api/v1/products-composite/" + PRODUCT_ID_OK + "?delay=" + DELAY + "&faultPercent=" + FAULT_PERCENT)
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isOk()
				.expectHeader().contentType(MediaType.APPLICATION_JSON)
				.expectBody()
				.jsonPath("$.id").isEqualTo(PRODUCT_ID_OK)
				.jsonPath("$.name").isEqualTo(ProductDataUtils.NAME)
				.jsonPath("$.recommendations.length()").isEqualTo(1)
				.jsonPath("$.reviews.length()").isEqualTo(1);
	}

	@Test
	@WithMockUser(roles= {"ADMIN", "MODERATOR", "USER"})
	void getProductNotFound() {

		client.get()
				.uri("/api/v1/products-composite/" + PRODUCT_ID_NOT_FOUND + "?delay=" + DELAY + "&faultPercent=" + FAULT_PERCENT)
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().is4xxClientError()
				.expectBody()
				.jsonPath("$.detail").isEqualTo("NOT_FOUND");
	}

}
