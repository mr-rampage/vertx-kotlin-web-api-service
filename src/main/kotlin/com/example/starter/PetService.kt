package com.example.starter

import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import io.vertx.ext.web.api.OperationRequest
import io.vertx.ext.web.api.OperationResponse
import io.vertx.ext.web.api.generator.WebApiServiceGen

@WebApiServiceGen
interface PetService {
  fun listPets(context: OperationRequest, resultHandler: Handler<AsyncResult<OperationResponse>>)
}
