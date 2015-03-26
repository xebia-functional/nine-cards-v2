package com.fortysevendeg.rest.client

import play.api.libs.json.{Json, Reads, Writes}
import spray.http.HttpHeaders.RawHeader
import spray.http.{HttpEntity, MediaTypes}
import spray.httpx.marshalling.Marshaller
import spray.httpx.unmarshalling._

import scala.concurrent.{ExecutionContext, Future}

trait ServiceClient {

  val httpClient: HttpClient

  implicit val executionContext: ExecutionContext

  val baseUrl: String

  def get[Res](
      path: String,
      headers: Seq[(String, String)] = Seq.empty)(implicit reads: Reads[Res]): Future[Res] = 
    httpClient.doGet[Res](
      baseUrl.concat(path),
      toHttpHeader(headers))(createUnmarshaller[Res])

  def emptyPost[Res](
      path: String,
      headers: Seq[(String, String)] = Seq.empty)(implicit reads: Reads[Res]): Future[Res] =
    httpClient.doPost[Res](
      baseUrl.concat(path),
      toHttpHeader(headers))(createUnmarshaller[Res])

  def post[Req, Res](
      path: String,
      headers: Seq[(String, String)] = Seq.empty,
      body: Req)(implicit reads: Reads[Res], writes: Writes[Req]): Future[Res] =
    httpClient.doPost[Req, Res](
      baseUrl.concat(path),
      toHttpHeader(headers),
      body)(createMarshaller[Req], createUnmarshaller[Res])

  def emptyPut[Res](
      path: String,
      headers: Seq[(String, String)] = Seq.empty)(implicit reads: Reads[Res]): Future[Res] =
    httpClient.doPut[Res](
      baseUrl.concat(path),
      toHttpHeader(headers))(createUnmarshaller[Res])

  def put[Req, Res](
      path: String,
      headers: Seq[(String, String)] = Seq.empty,
      body: Req)(implicit reads: Reads[Res], writes: Writes[Req]): Future[Res] =
    httpClient.doPut[Req, Res](
      baseUrl.concat(path),
      toHttpHeader(headers),
      body)(createMarshaller[Req], createUnmarshaller[Res])

  def delete[Res](
      path: String,
      headers: Seq[(String, String)] = Seq.empty)(implicit reads: Reads[Res]): Future[Res] =
    httpClient.doDelete[Res](
      baseUrl.concat(path),
      toHttpHeader(headers))(createUnmarshaller[Res])
  
  private def toHttpHeader(headers: Seq[(String, String)]): Seq[RawHeader] = 
    headers map(t => RawHeader(t._1, t._2))

  private def createUnmarshaller[Res](implicit reads: Reads[Res]): Unmarshaller[Res] =
    Unmarshaller[Res](MediaTypes.`text/plain`, MediaTypes.`application/json`) {
      case HttpEntity.NonEmpty(contentType, data) =>
        Json.parse(scala.io.Source.fromBytes(data.toByteArray, contentType.charset.value).mkString).as[Res]
    }
  
  private def createMarshaller[Res](implicit writes: Writes[Res]): Marshaller[Res] =
    Marshaller.of[Res](MediaTypes.`application/json`) { 
      (value, contentType, ctx) => ctx.marshalTo(HttpEntity(contentType, Json.toJson(value).toString()))
    }

}