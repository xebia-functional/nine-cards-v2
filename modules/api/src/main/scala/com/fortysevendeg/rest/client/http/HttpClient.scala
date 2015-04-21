package com.fortysevendeg.rest.client.http

import play.api.libs.json.Writes

import scala.concurrent.{ExecutionContext, Future}

trait HttpClient {

  def doGet(url: String, httpHeaders: Seq[(String, String)])(implicit executionContext: ExecutionContext): Future[HttpClientResponse]

  def doDelete(url: String, httpHeaders: Seq[(String, String)])(implicit executionContext: ExecutionContext): Future[HttpClientResponse]

  def doPost(url: String, httpHeaders: Seq[(String, String)])(implicit executionContext: ExecutionContext): Future[HttpClientResponse]

  def doPost[Req: Writes](url: String, httpHeaders: Seq[(String, String)], body: Req)(implicit executionContext: ExecutionContext): Future[HttpClientResponse]

  def doPut(url: String, httpHeaders: Seq[(String, String)])(implicit executionContext: ExecutionContext): Future[HttpClientResponse]

  def doPut[Req: Writes](url: String, httpHeaders: Seq[(String, String)], body: Req)(implicit executionContext: ExecutionContext): Future[HttpClientResponse]

}
