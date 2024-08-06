package com.dogoo.cloud.dogoo_cloud;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {"spring.cloud.config.enabled=false"})
class DogooCloudApplicationTests {

	@Test
	void contextLoads() {
	}

}
