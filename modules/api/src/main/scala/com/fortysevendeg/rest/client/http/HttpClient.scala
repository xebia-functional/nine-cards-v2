package com.fortysevendeg.rest.client.http

import com.fortysevendeg.ninecardslauncher.commons.exceptions.Exceptions.NineCardsException
import play.api.libs.json.Writes

import scala.concurrent.{ExecutionContext, Future}
import scalaz.\/
import scalaz.concurrent.Task

trait HttpClient {

  def doGet(url: String, httpHeaders: Seq[(String, String)])(implicit executionContext: ExecutionContext): Future[HttpClientResponse]

  def doDelete(url: String, httpHeaders: Seq[(String, String)])(implicit executionContext: ExecutionContext): Future[HttpClientResponse]

  def doPost(url: String, httpHeaders: Seq[(String, String)])(implicit executionContext: ExecutionContext): Future[HttpClientResponse]

  def doPost[Req: Writes](url: String, httpHeaders: Seq[(String, String)], body: Req)(implicit executionContext: ExecutionContext): Future[HttpClientResponse]

  def doPostTask[Req: Writes](url: String, httpHeaders: Seq[(String, String)], body: Req): Task[NineCardsException \/ HttpClientResponse]

  def doPut(url: String, httpHeaders: Seq[(String, String)])(implicit executionContext: ExecutionContext): Future[HttpClientResponse]

  def doPut[Req: Writes](url: String, httpHeaders: Seq[(String, String)], body: Req)(implicit executionContext: ExecutionContext): Future[HttpClientResponse]

}
