package com.github.pfrank13.context_loss.context_loss

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import

@Import(TestcontainersConfiguration::class)
@SpringBootTest
class ContextLossApplicationTests {

	@Test
	fun contextLoads() {
	}

}
