package com.github.pfrank13.context_loss.ex1

import kotlinx.coroutines.*
import kotlinx.coroutines.future.await
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.slf4j.MDCContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono
import java.util.UUID
import java.util.concurrent.CompletableFuture

//TODO refactor to make it seem like it's call another service because if you see the async code istelf it's obvious
class MyReactiveService {
  companion object {
    val LOG: Logger = LoggerFactory.getLogger(MyReactiveService::class.java)
  }

  suspend fun logDispatcher() {
    val dispatchResult: String = coroutineScope {
      val deferredIoResult: Deferred<String> = async(Dispatchers.IO) {
        LOG.info("Fetching deferredIoResult")
        "IO Bound Foo"
      }
      val awaittedDeferredIoResult = deferredIoResult.await() //Actually non blocking waiting for the result to come back to "join" this coroutine
      LOG.info("awaittedDeferredIoResult: {}", awaittedDeferredIoResult)
      awaittedDeferredIoResult
    }
    LOG.info("DispatcherResult is {}", dispatchResult)
  }

  suspend fun logDispatchPushContext() {
    val dispatchResult: String = coroutineScope {
      val deferredIoResult: Deferred<String> =
        withContext(MDCContext()) {
          async(Dispatchers.IO) {
            LOG.info("Fetching deferredIoResult")
            "IO Bound Foo"
          }
        }
      val awaittedDeferredIoResult = deferredIoResult.await() //Actually non blocking waiting for the result to come back to "join" this coroutine
      LOG.info("awaittedDeferredIoResult: {}", awaittedDeferredIoResult)
      awaittedDeferredIoResult
    }
    LOG.info("DispatcherResult is {}", dispatchResult)
  }

  suspend fun executorLogDispatch(): String {
    val completableFuture: CompletableFuture<String> = CompletableFuture.supplyAsync{
      LOG.info("Supplying async")
      "Supplied Async"
    }
    val completableFutureResult = completableFuture.await()
    LOG.info("CompletableFutureResult is {}", completableFutureResult)

    return completableFutureResult
  }


  suspend fun log() {
    val result = callSomeReactorBasedService().awaitSingle()
    LOG.info("Result is {}", result)
  }

  private fun callSomeReactorBasedService(): Mono<String> {
    return Mono.fromCallable {
      "foo"
    }.doFinally({
      LOG.info("Logging After reactive call")
    })
  }
}

class LoggingFilter : WebFilter {
  companion object {
    val LOG: Logger = LoggerFactory.getLogger(LoggingFilter::class.java)
  }

  override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
    val traceId: String = exchange.request.headers["X-Trace-Id"]?.first() ?: UUID.randomUUID().toString()

    MDC.put("XTraceId", traceId)

    LOG.info("Before Filter Chain: X-Trace-Id")
      return chain.filter(exchange).doFinally({
        LOG.info("After Filter Chain: X-Trace-Id")
        MDC.clear()
      })
  }
}

@RestController
class MyController @Autowired constructor(val myReactiveService: MyReactiveService) {
  companion object {
    val LOG: Logger = LoggerFactory.getLogger(MyController::class.java)
  }

  @GetMapping("suspend/loss")
  suspend fun getLoss(): String {
    myReactiveService.logDispatcher()
    return "Suspend Done"
  }

  @GetMapping("suspend/noLoss")
  suspend fun getNoLoss(): String {
    myReactiveService.logDispatchPushContext()
    return "Suspend Done"
  }

  @GetMapping("executor/loss")
  suspend fun getLossExecutor(): String {
    myReactiveService.executorLogDispatch()
    return "Executor Done"
  }

  @GetMapping("suspend/error")
  suspend fun getLossError(): String {
    myReactiveService.logDispatcher()
    throw Exception("Suspend Done")
  }

  @ExceptionHandler
  fun handleException(x: Exception): String {
    LOG.error("Exception", x)
    return "error"
  }
}

@RestController
class MyWebFluxController {
  companion object {
    val LOG: Logger = LoggerFactory.getLogger(MyWebFluxController::class.java)
  }

  @GetMapping("webflux/loss")
  fun getLoss(): String {
    LOG.info("Inside WebFluxController")
    return "Webflux Done"
  }
}
