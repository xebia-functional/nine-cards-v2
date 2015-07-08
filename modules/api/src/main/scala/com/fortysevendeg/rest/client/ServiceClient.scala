package com.fortysevendeg.rest.client

import com.fortysevendeg.ninecardslauncher.commons.exceptions.Exceptions.NineCardsException
import com.fortysevendeg.rest.client.http.{HttpClient, HttpClientResponse}
import com.fortysevendeg.rest.client.messages.{ServiceClientException, ServiceClientResponse}
import play.api.libs.json.{Json, Reads, Writes}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}
import scalaz._
import Scalaz._
import EitherT._
import com.fortysevendeg.ninecardslauncher.commons.services.Service._

import scalaz.concurrent.Task

class ServiceClient(httpClient: HttpClient, baseUrl: String) {

  def get[Res](
      path: String,
      headers: Seq[(String, String)] = Seq.empty,
      reads: Option[Reads[Res]] = None,
      emptyResponse: Boolean = false)(implicit executionContext: ExecutionContext): Future[ServiceClientResponse[Res]] =
    for {
      clientResponse <- httpClient.doGet(baseUrl.concat(path), headers)
      response <- readResponse(clientResponse, reads, emptyResponse)
    } yield ServiceClientResponse(clientResponse.statusCode.intValue, response)


  def emptyPost[Res](
      path: String,
      headers: Seq[(String, String)] = Seq.empty,
      reads: Option[Reads[Res]] = None,
      emptyResponse: Boolean = false)(implicit executionContext: ExecutionContext): Future[ServiceClientResponse[Res]] =
    for {
      clientResponse <- httpClient.doPost(baseUrl.concat(path), headers)
      response <- readResponse(clientResponse, reads, emptyResponse)
    } yield ServiceClientResponse(clientResponse.statusCode, response)


  def post[Req, Res](
      path: String,
      headers: Seq[(String, String)] = Seq.empty,
      body: Req,
      reads: Option[Reads[Res]] = None,
      emptyResponse: Boolean = false)(implicit executionContext: ExecutionContext, writes: Writes[Req]): Future[ServiceClientResponse[Res]] =
    for {
      clientResponse <- httpClient.doPost[Req](baseUrl.concat(path), headers, body)
      response <- readResponse(clientResponse, reads, emptyResponse)
    } yield ServiceClientResponse(clientResponse.statusCode, response)

  def postTask[Req, Res](
      path: String,
      headers: Seq[(String, String)] = Seq.empty,
      body: Req,
      reads: Option[Reads[Res]] = None,
      emptyResponse: Boolean = false)(implicit writes: Writes[Req]): Task[NineCardsException \/ ServiceClientResponse[Res]] =
    for {
      clientResponse <- httpClient.doPostTask[Req](baseUrl.concat(path), headers, body) ▹ eitherT
      response <- readResponseTask(clientResponse, reads, emptyResponse) ▹ eitherT
    } yield ServiceClientResponse(clientResponse.statusCode, response)


  def emptyPut[Res](
      path: String,
      headers: Seq[(String, String)] = Seq.empty,
      reads: Option[Reads[Res]] = None,
      emptyResponse: Boolean = false)(implicit executionContext: ExecutionContext): Future[ServiceClientResponse[Res]] =
    for {
      clientResponse <- httpClient.doPut(baseUrl.concat(path), headers)
      response <- readResponse(clientResponse, reads, emptyResponse)
    } yield ServiceClientResponse(clientResponse.statusCode, response)

  def put[Req, Res](
      path: String,
      headers: Seq[(String, String)] = Seq.empty,
      body: Req,
      reads: Option[Reads[Res]] = None,
      emptyResponse: Boolean = false)(implicit executionContext: ExecutionContext, writes: Writes[Req]): Future[ServiceClientResponse[Res]] =
    for {
      httpResponse <- httpClient.doPut[Req](baseUrl.concat(path), headers, body)
      response <- readResponse(httpResponse, reads, emptyResponse)
    } yield ServiceClientResponse(httpResponse.statusCode, response)


  def delete[Res](
      path: String,
      headers: Seq[(String, String)] = Seq.empty,
      reads: Option[Reads[Res]] = None,
      emptyResponse: Boolean = false)(implicit executionContext: ExecutionContext): Future[ServiceClientResponse[Res]] =
    for {
      clientResponse <- httpClient.doDelete(baseUrl.concat(path), headers)
      response <- readResponse(clientResponse, reads, emptyResponse)
    } yield ServiceClientResponse(clientResponse.statusCode, response)

  private def readResponse[T](
      clientResponse: HttpClientResponse,
      maybeReads: Option[Reads[T]],
      emptyResponse: Boolean)(implicit executionContext: ExecutionContext): Future[Option[T]] =
    (clientResponse.body, emptyResponse, maybeReads) match {
      case (Some(d), false, Some(r)) => transformResponse[T](d, r)
      case (None, false, _) => Future.failed(new ServiceClientException("No content"))
      case (Some(d), false, None) => Future.failed(new ServiceClientException("No transformer found for type"))
      case _ => Future.successful(None)
    }

  private def readResponseTask[T](
      clientResponse: HttpClientResponse,
      maybeReads: Option[Reads[T]],
      emptyResponse: Boolean): Task[NineCardsException \/ Option[T]] =
    Task {
      (clientResponse.body, emptyResponse, maybeReads) match {
        case (Some(d), false, Some(r)) => \/-(transformResponseTask[T](d, r))
        case (None, false, _) => -\/(new NineCardsException("No content")) // TODO - Make ServiceClientException extends NineCardsException
        case (Some(d), false, None) => -\/(new NineCardsException("No transformer found for type"))
        case _ => -\/(new NineCardsException("No expected"))
      }
    }

  private def transformResponse[T](
      content: String,
      reads: Reads[T])(implicit executionContext: ExecutionContext): Future[Option[T]] =
    Future {
      Try(Json.parse(content).as[T](reads))
    }.flatMap {
      case Success(s) => Future.successful(Some(s))
      case Failure(fail) => Future.failed(fail)
    }

  private def transformResponseTask[T](
      content: String,
      reads: Reads[T]): Option[T] =
    Try(Json.parse(content).as[T](reads)) match {
      case Success(s) => Some(s)
      case Failure(fail) => None
    }

}

object ServiceClient {

}