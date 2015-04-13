package com.fortysevendeg.rest.client.http

import play.api.libs.json.Writes

import scala.concurrent.Future
import scala.reflect.runtime.universe.TypeTag

trait HttpClient {

  def doGet(url: String, httpHeaders: Seq[(String, String)]): Future[HttpClientResponse]

  def doDelete(url: String, httpHeaders: Seq[(String, String)]): Future[HttpClientResponse]

  def doPost(url: String, httpHeaders: Seq[(String, String)]): Future[HttpClientResponse]

  def doPost[Req: TypeTag: Writes](url: String, httpHeaders: Seq[(String, String)], body: Req): Future[HttpClientResponse]

  def doPut(url: String, httpHeaders: Seq[(String, String)]): Future[HttpClientResponse]

  def doPut[Req: TypeTag: Writes](url: String, httpHeaders: Seq[(String, String)], body: Req): Future[HttpClientResponse]

}
