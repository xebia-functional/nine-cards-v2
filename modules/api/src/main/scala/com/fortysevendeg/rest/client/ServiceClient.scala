package com.fortysevendeg.rest.client

import com.fortysevendeg.rest.client.http.{HttpClientResponse, HttpClient}
import com.fortysevendeg.rest.client.messages.{ServiceClientResponse, ServiceClientException}
import play.api.libs.json.{Json, Reads, Writes}

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
      reads: Option[Reads[Res]] = None): Future[ServiceClientResponse[Res]] =
    for {
      clientResponse <- httpClient.doGet(baseUrl.concat(path), headers)
      response <- readResponse(clientResponse, reads)
    } yield ServiceClientResponse(clientResponse.statusCode.intValue, response)


  def emptyPost[Res: TypeTag](
      path: String,
      headers: Seq[(String, String)] = Seq.empty,
      reads: Option[Reads[Res]] = None): Future[ServiceClientResponse[Res]] =
    for {
      clientResponse <- httpClient.doPost(baseUrl.concat(path), headers)
      response <- readResponse(clientResponse, reads)
    } yield ServiceClientResponse(clientResponse.statusCode, response)


  def post[Req: TypeTag, Res: TypeTag](
      path: String,
      headers: Seq[(String, String)] = Seq.empty,
      body: Req,
      reads: Option[Reads[Res]] = None)(implicit writes: Writes[Req]): Future[ServiceClientResponse[Res]] =
    for {
      clientResponse <- httpClient.doPost[Req](baseUrl.concat(path), headers, body)
      response <- readResponse(clientResponse, reads)
    } yield ServiceClientResponse(clientResponse.statusCode, response)


  def emptyPut[Res: TypeTag](
      path: String,
      headers: Seq[(String, String)] = Seq.empty,
      reads: Option[Reads[Res]] = None): Future[ServiceClientResponse[Res]] =
    for {
      clientResponse <- httpClient.doPut(baseUrl.concat(path), headers)
      response <- readResponse(clientResponse, reads)
    } yield ServiceClientResponse(clientResponse.statusCode, response)

  def put[Req: TypeTag, Res: TypeTag](
      path: String,
      headers: Seq[(String, String)] = Seq.empty,
      body: Req,
      reads: Option[Reads[Res]] = None)(implicit writes: Writes[Req]): Future[ServiceClientResponse[Res]] =
    for {
      httpResponse <- httpClient.doPut[Req](baseUrl.concat(path), headers, body)
      response <- readResponse(httpResponse, reads)
    } yield ServiceClientResponse(httpResponse.statusCode, response)


  def delete[Res: TypeTag](
      path: String,
      headers: Seq[(String, String)] = Seq.empty,
      reads: Option[Reads[Res]] = None): Future[ServiceClientResponse[Res]] =
    for {
      clientResponse <- httpClient.doDelete(baseUrl.concat(path), headers)
      response <- readResponse(clientResponse, reads)
    } yield ServiceClientResponse(clientResponse.statusCode, response)

  private def readResponse[T: TypeTag](clientResponse: HttpClientResponse, maybeReads: Option[Reads[T]]): Future[Option[T]] = {
    import scala.reflect.runtime.universe._
    val unitType = typeOf[T] =:= typeOf[Unit]
    (clientResponse.body, unitType, maybeReads) match {
      case (Some(d), false, Some(r)) => transformResponse[T](d, r)
      case (None, false, _) => Future.failed(new ServiceClientException("No content"))
      case (Some(d), false, None) => Future.failed(new ServiceClientException("No transformer found for type"))
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