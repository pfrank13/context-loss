package com.github.pfrank13.context_loss.context_loss

import com.github.pfrank13.context_loss.ContextLossApplication
import org.springframework.boot.fromApplication
import org.springframework.boot.with


fun main(args: Array<String>) {
	fromApplication<ContextLossApplication>().with(TestcontainersConfiguration::class).run(*args)
}
