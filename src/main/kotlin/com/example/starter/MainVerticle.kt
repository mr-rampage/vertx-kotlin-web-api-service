package com.example.starter

import io.vertx.core.Future
import io.vertx.ext.web.api.contract.openapi3.OpenAPI3RouterFactory
import io.vertx.kotlin.core.eventbus.completionHandlerAwait
import io.vertx.kotlin.core.http.listenAwait
import io.vertx.kotlin.coroutines.CoroutineVerticle
import io.vertx.kotlin.coroutines.await
import io.vertx.serviceproxy.ServiceBinder

class MainVerticle : CoroutineVerticle() {

  override suspend fun start() {
    val routerFactoryFuture = Future.future<OpenAPI3RouterFactory>()
    OpenAPI3RouterFactory.create(vertx, "conduit.yaml", routerFactoryFuture)
    val routerFactory = routerFactoryFuture.await()

    try {
      val router = routerFactory.mountServicesFromExtensions().router

      vertx
        .createHttpServer()
        .requestHandler(router)
        .listenAwait(8080)

      ServiceBinder(vertx)
        .setAddress("pet.service")
        .registerLocal(PetService::class.java, PetServiceImpl())
        .completionHandlerAwait()

      println("Started server on port 8080")
    } catch (e: Exception) {
      println("Unable to start server")
    }
  }
}
