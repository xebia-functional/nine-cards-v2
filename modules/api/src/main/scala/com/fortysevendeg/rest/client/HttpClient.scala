package com.fortysevendeg.rest.client

import akka.actor.ActorSystem
import spray.client.pipelining._
import spray.http.HttpHeaders.RawHeader
import spray.http.HttpRequest
import spray.httpx.marshalling.Marshaller
import spray.httpx.unmarshalling.Unmarshaller

import scala.concurrent.{ExecutionContext, Future}

trait HttpClient {

  implicit val executionContext: ExecutionContext

  implicit val actorSystem: ActorSystem

  def doGet[Res](url: String, httpHeaders: Seq[RawHeader])
      (implicit unmarshaller: Unmarshaller[Res]): Future[Res] = {
    val pipeline = preparePipeline[Res](httpHeaders)
    pipeline(Get(url))
  }

  def doDelete[Res](url: String, httpHeaders: Seq[RawHeader])
      (implicit unmarshaller: Unmarshaller[Res]): Future[Res] = {
    val pipeline = preparePipeline[Res](httpHeaders)
    pipeline(Delete(url))
  }

  def doPost[Res](url: String, httpHeaders: Seq[RawHeader])
      (implicit unmarshaller: Unmarshaller[Res]): Future[Res] = {
    val pipeline = preparePipeline[Res](httpHeaders)
    pipeline(Post(url))
  }

  def doPost[Req, Res](url: String, httpHeaders: Seq[RawHeader], body: Req)
      (implicit marshaller: Marshaller[Req], unmarshaller: Unmarshaller[Res]): Future[Res] = {
    val pipeline = preparePipeline[Res](httpHeaders)
    pipeline(Post(url, body))
  }

  def doPut[Res](url: String, httpHeaders: Seq[RawHeader])
      (implicit unmarshaller: Unmarshaller[Res]): Future[Res] = {
    val pipeline = preparePipeline[Res](httpHeaders)
    pipeline(Put(url))
  }

  def doPut[Req, Res](url: String, httpHeaders: Seq[RawHeader], body: Req)
      (implicit marshaller: Marshaller[Req], unmarshaller: Unmarshaller[Res]): Future[Res] = {
    val pipeline = preparePipeline[Res](httpHeaders)
    pipeline(Put(url, body))
  }

  def sendAndReceive = sendReceive

  private def preparePipeline[Res](httpHeaders: Seq[RawHeader])(implicit unmarshaller: Unmarshaller[Res]): HttpRequest => Future[Res] = {
    addHeaders(httpHeaders.toList) ~> sendAndReceive ~> unmarshal[Res]
  }


}
