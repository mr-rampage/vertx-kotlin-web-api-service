package com.example.starter

import io.vertx.core.AbstractVerticle
import io.vertx.core.Promise
import io.vertx.ext.web.api.contract.openapi3.OpenAPI3RouterFactory
import io.vertx.serviceproxy.ServiceBinder

class MainVerticle : AbstractVerticle() {

  override fun start(startPromise: Promise<Void>?) {

    val consumer = ServiceBinder(vertx)
      .setAddress("pet.service")
      .registerLocal(PetService::class.java, PetServiceImpl())

    OpenAPI3RouterFactory.create(vertx, "conduit.yaml") {
      if (it.succeeded()) {
        println("Loaded swagger file")
        val routerFactory = it.result()
        routerFactory.mountServicesFromExtensions()
        val router = routerFactory.router
        val server = vertx.createHttpServer()
        server
          .requestHandler(router)
          .listen(8080) {
            consumer.completionHandler(startPromise);
            println("Started server on port 8080")
          }
      } else {
        println("Failed to load swagger file")
        startPromise?.fail("Failed to load swagger file");
      }
    }
  }
}
