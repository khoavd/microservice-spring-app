package com.dogoo.ecommerce.reviewservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "eureka.client.enabled=false",
                "spring.main.allow-bean-definition-overriding=true",
                "spring.cloud.config.enabled=false"})
class ReviewServiceApplicationTests {

    @Test
    void contextLoads() {
    }

}
