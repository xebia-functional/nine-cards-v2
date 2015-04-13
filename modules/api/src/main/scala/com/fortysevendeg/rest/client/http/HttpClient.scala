package com.fortysevendeg.rest.client.http

import play.api.libs.json.Writes

import scala.concurrent.{ExecutionContext, Future}
import scala.reflect.runtime.universe.TypeTag

trait HttpClient {

  def doGet(url: String, httpHeaders: Seq[(String, String)])(implicit executionContext: ExecutionContext): Future[HttpClientResponse]

  def doDelete(url: String, httpHeaders: Seq[(String, String)])(implicit executionContext: ExecutionContext): Future[HttpClientResponse]

  def doPost(url: String, httpHeaders: Seq[(String, String)])(implicit executionContext: ExecutionContext): Future[HttpClientResponse]

  def doPost[Req: TypeTag: Writes](url: String, httpHeaders: Seq[(String, String)], body: Req)(implicit executionContext: ExecutionContext): Future[HttpClientResponse]

  def doPut(url: String, httpHeaders: Seq[(String, String)])(implicit executionContext: ExecutionContext): Future[HttpClientResponse]

  def doPut[Req: TypeTag: Writes](url: String, httpHeaders: Seq[(String, String)], body: Req)(implicit executionContext: ExecutionContext): Future[HttpClientResponse]

}
