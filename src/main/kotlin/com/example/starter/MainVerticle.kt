package com.example.starter

import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import io.vertx.core.Promise
import io.vertx.core.http.HttpServer
import io.vertx.ext.web.Router
import io.vertx.ext.web.api.contract.openapi3.OpenAPI3RouterFactory
import io.vertx.kotlin.coroutines.CoroutineVerticle
import io.vertx.serviceproxy.ServiceBinder
import java.util.concurrent.CompletableFuture

class MainVerticle : CoroutineVerticle() {

  override fun start(startPromise: Promise<Void>) {
    val routerFactoryFuture = CompletableFuture<OpenAPI3RouterFactory>()
    OpenAPI3RouterFactory.create(vertx, "conduit.yaml", withPromise(routerFactoryFuture))

    val serviceBinder = ServiceBinder(vertx)
      .setAddress("pet.service")
      .registerLocal(PetService::class.java, PetServiceImpl())

    routerFactoryFuture
      .thenApply(this::createRouter)
      .thenApply(this::startHttpServer)
      .thenAccept {
        serviceBinder.completionHandler(startPromise)
        println("Started server on port 8080")
      }
      .exceptionally {
        println("Unable to start server")
        startPromise.fail(it.cause)
        null
      }
  }

  private fun<T> withPromise(future: CompletableFuture<T>): Handler<AsyncResult<T>> {
    return Handler {
      if (it.succeeded()) {
        future.complete(it.result())
      } else {
        future.completeExceptionally(it.cause())
      }
    }
  }

  private fun startHttpServer(router: Router) : HttpServer {
    return vertx
      .createHttpServer()
      .requestHandler(router)
      .listen(8080)
  }

  private fun createRouter(routerFactory: OpenAPI3RouterFactory): Router {
    return routerFactory.mountServicesFromExtensions().router
  }
}
