package com.fortysevendeg.rest.client

import com.fortysevendeg.rest.client.messages.{HttpClientResponse, HttpClientException}
import play.api.libs.json.{Json, Reads, Writes}
import spray.http.{MediaTypes, HttpEntity}
import spray.http.HttpHeaders.RawHeader
import spray.httpx.marshalling.Marshaller

import scala.concurrent.{ExecutionContext, Future}
import scala.reflect.runtime.universe.TypeTag
import scala.util.{Success, Failure, Try}


trait ServiceClient {

  val httpClient: HttpClient

  implicit val executionContext: ExecutionContext

  val baseUrl: String

  def get[Res: TypeTag](
      path: String,
      headers: Seq[(String, String)] = Seq.empty,
      reads: Option[Reads[Res]] = None): Future[HttpClientResponse[Res]] =
    for {
      httpResponse <- httpClient.doGet(baseUrl.concat(path), toHttpHeader(headers))
      response <- verifyResponse(httpResponse.entity, reads)
    } yield HttpClientResponse(httpResponse.status.intValue, response)


  def emptyPost[Res: TypeTag](
      path: String,
      headers: Seq[(String, String)] = Seq.empty,
      reads: Option[Reads[Res]] = None): Future[HttpClientResponse[Res]] =
    for {
      httpResponse <- httpClient.doPost(baseUrl.concat(path), toHttpHeader(headers))
      response <- verifyResponse(httpResponse.entity, reads)
    } yield HttpClientResponse(httpResponse.status.intValue, response)


  def post[Req: TypeTag, Res: TypeTag](
      path: String,
      headers: Seq[(String, String)] = Seq.empty,
      body: Req,
      reads: Option[Reads[Res]] = None)(implicit writes: Writes[Req]): Future[HttpClientResponse[Res]] = {
    implicit val marshaller = createMarshaller[Req]
    for {
      httpResponse <- httpClient.doPost[Req](baseUrl.concat(path), toHttpHeader(headers), body)
      response <- verifyResponse(httpResponse.entity, reads)
    } yield HttpClientResponse(httpResponse.status.intValue, response)
  }

  def emptyPut[Res: TypeTag](
      path: String,
      headers: Seq[(String, String)] = Seq.empty,
      reads: Option[Reads[Res]] = None): Future[HttpClientResponse[Res]] =
    for {
      httpResponse <- httpClient.doPut(baseUrl.concat(path), toHttpHeader(headers))
      response <- verifyResponse(httpResponse.entity, reads)
    } yield HttpClientResponse(httpResponse.status.intValue, response)

  def put[Req: TypeTag, Res: TypeTag](
      path: String,
      headers: Seq[(String, String)] = Seq.empty,
      body: Req,
      reads: Option[Reads[Res]] = None)(implicit writes: Writes[Req]): Future[HttpClientResponse[Res]] = {
    implicit val marshaller = createMarshaller[Req]
    for {
      httpResponse <- httpClient.doPut[Req](baseUrl.concat(path), toHttpHeader(headers), body)
      response <- verifyResponse(httpResponse.entity, reads)
    } yield HttpClientResponse(httpResponse.status.intValue, response)
  }

  def delete[Res: TypeTag](
      path: String,
      headers: Seq[(String, String)] = Seq.empty,
      reads: Option[Reads[Res]] = None): Future[HttpClientResponse[Res]] =
    for {
      httpResponse <- httpClient.doDelete(baseUrl.concat(path), toHttpHeader(headers))
      response <- verifyResponse(httpResponse.entity, reads)
    } yield HttpClientResponse(httpResponse.status.intValue, response)
  
  private def toHttpHeader(headers: Seq[(String, String)]): Seq[RawHeader] = 
    headers map(t => RawHeader(t._1, t._2))

  private def createMarshaller[Res](implicit writes: Writes[Res]): Marshaller[Res] =
    Marshaller.of[Res](MediaTypes.`application/json`) {
      (value, contentType, ctx) => ctx.marshalTo(HttpEntity(contentType, Json.toJson(value).toString()))
    }

  private def verifyResponse[T: TypeTag](httpEntity: HttpEntity, maybeReads: Option[Reads[T]]): Future[Option[T]] = {
    import scala.reflect.runtime.universe._
    val unitType = typeOf[T] =:= typeOf[Unit]
    (httpEntity.isEmpty, unitType, maybeReads) match {
      case (false, false, Some(r)) => transformResponse[T](httpEntity.asString, r)
      case (true, false, _) => Future.failed(new HttpClientException("No content"))
      case (false, false, None) => Future.failed(new HttpClientException("No transformer found for type"))
      case _ => Future.successful(None)
    }
  }

  private def transformResponse[T](content: String, reads: Reads[T]): Future[Option[T]] =
    Future {
      Try(Json.parse(content).as[T](reads))
    }.flatMap {
      case Success(s) => Future.successful(Some(s))
      case Failure(fail) => Future.failed(fail)
    }

}