package com.fortysevendeg.rest.client.http

import akka.actor.ActorSystem
import play.api.libs.json.{Json, Writes}
import spray.client.pipelining._
import spray.http.HttpHeaders.RawHeader
import spray.http.{HttpEntity, MediaTypes, HttpRequest, HttpResponse}
import spray.httpx.marshalling.Marshaller

import scala.concurrent.{Future, ExecutionContext}
import scala.reflect.runtime.universe.TypeTag

trait SprayHttpClient extends HttpClient {

  implicit val executionContext: ExecutionContext

  implicit val actorSystem: ActorSystem

  override def doGet(url: String, httpHeaders:  Seq[(String, String)]): Future[HttpClientResponse] = {
    val pipeline = preparePipeline(toHttpHeader(httpHeaders))
    transformResponse(pipeline(Get(url)))
  }

  override def doDelete(url: String, httpHeaders:  Seq[(String, String)]): Future[HttpClientResponse] = {
    val pipeline = preparePipeline(toHttpHeader(httpHeaders))
    transformResponse(pipeline(Delete(url)))
  }

  override def doPost(url: String, httpHeaders:  Seq[(String, String)]): Future[HttpClientResponse] = {
    val pipeline = preparePipeline(toHttpHeader(httpHeaders))
    transformResponse(pipeline(Post(url)))
  }

  override def doPost[Req: TypeTag: Writes](url: String, httpHeaders:  Seq[(String, String)], body: Req): Future[HttpClientResponse] = {
    implicit val marshaller = createMarshaller[Req]
    val pipeline = preparePipeline(toHttpHeader(httpHeaders))
    transformResponse(pipeline(Post(url, body)))
  }

  override def doPut(url: String, httpHeaders:  Seq[(String, String)]): Future[HttpClientResponse] = {
    val pipeline = preparePipeline(toHttpHeader(httpHeaders))
    transformResponse(pipeline(Put(url)))
  }

  override def doPut[Req: TypeTag: Writes](url: String, httpHeaders:  Seq[(String, String)], body: Req): Future[HttpClientResponse] = {
    implicit val marshaller = createMarshaller[Req]
    val pipeline = preparePipeline(toHttpHeader(httpHeaders))
    transformResponse(pipeline(Put(url, body)))
  }

  def sendAndReceive = sendReceive

  private def preparePipeline(httpHeaders: Seq[RawHeader]): HttpRequest => Future[HttpResponse] = {
    addHeaders(httpHeaders.toList) ~> sendAndReceive
  }

  private def toHttpHeader(headers: Seq[(String, String)]): Seq[RawHeader] =
    headers map(t => RawHeader(t._1, t._2))

  private def createMarshaller[Req](implicit writes: Writes[Req]): Marshaller[Req] =
    Marshaller.of[Req](MediaTypes.`application/json`) {
      (value, contentType, ctx) => ctx.marshalTo(HttpEntity(contentType, Json.toJson(value).toString()))
    }

  private def transformResponse(httpResponse: Future[HttpResponse]): Future[HttpClientResponse] =
    httpResponse.transform(
      response => HttpClientResponse(response.status.intValue, readResponse(response.entity)),
      cause => cause
    )

  private def readResponse(httpEntity: HttpEntity): Option[String] =
    if (httpEntity.isEmpty) None else Some(httpEntity.asString)
}
