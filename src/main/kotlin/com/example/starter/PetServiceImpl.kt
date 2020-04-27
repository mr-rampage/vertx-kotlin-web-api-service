package com.example.starter

import io.vertx.core.AsyncResult
import io.vertx.core.Future
import io.vertx.core.Handler
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.api.OperationRequest
import io.vertx.ext.web.api.OperationResponse

class PetServiceImpl : PetService {
  override fun listPets(context: OperationRequest?, resultHandler: Handler<AsyncResult<OperationResponse>>?) {
    val response = Future.succeededFuture(OperationResponse.completedWithJson(JsonObject().put("test", "hello")).setStatusCode(200))
    resultHandler?.handle(response)
  }
}
