package com.dogoo.gateway.gateway;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(
		webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
		properties = {
				"eureka.client.enabled=false",
				"spring.cloud.config.enabled=false"})
class GatewayApplicationTests {

	@Test
	void contextLoads() {
	}

}
