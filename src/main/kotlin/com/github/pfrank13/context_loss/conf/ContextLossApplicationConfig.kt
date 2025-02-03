package com.github.pfrank13.context_loss.conf

import com.github.pfrank13.context_loss.ex1.LoggingFilter
import com.github.pfrank13.context_loss.ex1.MyReactiveService
import jakarta.annotation.PostConstruct
import kotlinx.coroutines.Dispatchers
import org.slf4j.MDC
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.server.WebFilter
import reactor.core.scheduler.Schedulers

@Configuration
class ContextLossApplicationConfig {

  @Bean
  fun loggingFilter(): WebFilter {
    return LoggingFilter()
  }

  @Bean
  fun loggingService(): MyReactiveService {
    return MyReactiveService()
  }

  /*
  @PostConstruct
  fun mdcReactorHandling() {
    Schedulers.onScheduleHook("mdc") { runnable: Runnable ->
      val map = MDC.getCopyOfContextMap()
      Runnable {
        if (map != null) {
          MDC.setContextMap(map)
        }
        try {
          runnable.run()
        } finally {
          MDC.clear()
        }
      }
    }
  }

   */
}