package com.fortysevendeg.rest.client

import akka.actor.ActorSystem
import spray.client.pipelining._
import spray.http.HttpHeaders.RawHeader
import spray.http.{HttpResponse, HttpRequest}
import spray.httpx.marshalling.Marshaller

import scala.concurrent.{ExecutionContext, Future}
import scala.reflect.runtime.universe.TypeTag

trait HttpClient {

  implicit val executionContext: ExecutionContext

  implicit val actorSystem: ActorSystem

  def doGet(url: String, httpHeaders: Seq[RawHeader]): Future[HttpResponse] = {
    val pipeline = preparePipeline(httpHeaders)
    pipeline(Get(url))
  }

  def doDelete(url: String, httpHeaders: Seq[RawHeader]): Future[HttpResponse] = {
    val pipeline = preparePipeline(httpHeaders)
    pipeline(Delete(url))
  }

  def doPost(url: String, httpHeaders: Seq[RawHeader]): Future[HttpResponse] = {
    val pipeline = preparePipeline(httpHeaders)
    pipeline(Post(url))
  }

  def doPost[Req: TypeTag: Marshaller](url: String, httpHeaders: Seq[RawHeader], body: Req): Future[HttpResponse] = {
    val pipeline = preparePipeline(httpHeaders)
    pipeline(Post(url, body))
  }

  def doPut(url: String, httpHeaders: Seq[RawHeader]): Future[HttpResponse] = {
    val pipeline = preparePipeline(httpHeaders)
    pipeline(Put(url))
  }

  def doPut[Req: TypeTag: Marshaller](url: String, httpHeaders: Seq[RawHeader], body: Req): Future[HttpResponse] = {
    val pipeline = preparePipeline(httpHeaders)
    pipeline(Put(url, body))
  }

  def sendAndReceive = sendReceive

  private def preparePipeline(httpHeaders: Seq[RawHeader]): HttpRequest => Future[HttpResponse] = {
    addHeaders(httpHeaders.toList) ~> sendAndReceive
  }


}
