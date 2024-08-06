package com.dogoo.office.authz;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(
		webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
		properties = {
				"eureka.client.enabled=false",
				"spring.cloud.config.enabled=false"})
class AuthzApplicationTests {

	@Test
	void contextLoads() {
	}

}
