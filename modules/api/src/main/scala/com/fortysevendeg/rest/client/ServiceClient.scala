package com.fortysevendeg.rest.client

import com.fortysevendeg.ninecardslauncher.commons.exceptions.Exceptions.NineCardsException
import com.fortysevendeg.ninecardslauncher.commons.services.Service._
import com.fortysevendeg.rest.client.http.{HttpClient, HttpClientResponse}
import com.fortysevendeg.rest.client.messages.ServiceClientResponse
import play.api.libs.json.{Json, Reads, Writes}

import scala.util.{Failure, Success, Try}
import scalaz.EitherT._
import scalaz.Scalaz._
import scalaz._
import scalaz.concurrent.Task

class ServiceClient(httpClient: HttpClient, baseUrl: String) {

  def get[Res](
    path: String,
    headers: Seq[(String, String)] = Seq.empty,
    reads: Option[Reads[Res]] = None,
    emptyResponse: Boolean = false
    ): Task[NineCardsException \/ ServiceClientResponse[Res]] =
    for {
      clientResponse <- httpClient.doGet(baseUrl.concat(path), headers) ▹ eitherT
      response <- readResponse(clientResponse, reads, emptyResponse) ▹ eitherT
    } yield ServiceClientResponse(clientResponse.statusCode.intValue, response)


  def emptyPost[Res](
    path: String,
    headers: Seq[(String, String)] = Seq.empty,
    reads: Option[Reads[Res]] = None,
    emptyResponse: Boolean = false
    ): Task[NineCardsException \/ ServiceClientResponse[Res]] =
    for {
      clientResponse <- httpClient.doPost(baseUrl.concat(path), headers) ▹ eitherT
      response <- readResponse(clientResponse, reads, emptyResponse) ▹ eitherT
    } yield ServiceClientResponse(clientResponse.statusCode, response)


  def post[Req, Res](
    path: String,
    headers: Seq[(String, String)] = Seq.empty,
    body: Req,
    reads: Option[Reads[Res]] = None,
    emptyResponse: Boolean = false
    )(implicit writes: Writes[Req]): Task[NineCardsException \/ ServiceClientResponse[Res]] =
    for {
      clientResponse <- httpClient.doPost[Req](baseUrl.concat(path), headers, body) ▹ eitherT
      response <- readResponse(clientResponse, reads, emptyResponse) ▹ eitherT
    } yield ServiceClientResponse(clientResponse.statusCode, response)

  def emptyPut[Res](
    path: String,
    headers: Seq[(String, String)] = Seq.empty,
    reads: Option[Reads[Res]] = None,
    emptyResponse: Boolean = false
    ): Task[NineCardsException \/ ServiceClientResponse[Res]] =
    for {
      clientResponse <- httpClient.doPut(baseUrl.concat(path), headers) ▹ eitherT
      response <- readResponse(clientResponse, reads, emptyResponse) ▹ eitherT
    } yield ServiceClientResponse(clientResponse.statusCode, response)

  def put[Req, Res](
    path: String,
    headers: Seq[(String, String)] = Seq.empty,
    body: Req,
    reads: Option[Reads[Res]] = None,
    emptyResponse: Boolean = false
    )(implicit writes: Writes[Req]): Task[NineCardsException \/ ServiceClientResponse[Res]] =
    for {
      httpResponse <- httpClient.doPut[Req](baseUrl.concat(path), headers, body) ▹ eitherT
      response <- readResponse(httpResponse, reads, emptyResponse) ▹ eitherT
    } yield ServiceClientResponse(httpResponse.statusCode, response)


  def delete[Res](
    path: String,
    headers: Seq[(String, String)] = Seq.empty,
    reads: Option[Reads[Res]] = None,
    emptyResponse: Boolean = false
    ): Task[NineCardsException \/ ServiceClientResponse[Res]] =
    for {
      clientResponse <- httpClient.doDelete(baseUrl.concat(path), headers) ▹ eitherT
      response <- readResponse(clientResponse, reads, emptyResponse) ▹ eitherT
    } yield ServiceClientResponse(clientResponse.statusCode, response)

  private def readResponse[T](
    clientResponse: HttpClientResponse,
    maybeReads: Option[Reads[T]],
    emptyResponse: Boolean
    ): Task[NineCardsException \/ Option[T]] =
    Task {
      (clientResponse.body, emptyResponse, maybeReads) match {
        case (Some(d), false, Some(r)) => \/-(transformResponseTask[T](d, r))
        case (None, false, _) => -\/(new NineCardsException("No content")) // TODO - Make ServiceClientException extends NineCardsException
        case (Some(d), false, None) => -\/(new NineCardsException("No transformer found for type"))
        case _ => \/-(None)
      }
    }

  private def transformResponseTask[T](
    content: String,
    reads: Reads[T]
    ): Option[T] =
    Try(Json.parse(content).as[T](reads)) match {
      case Success(s) => Some(s)
      case Failure(fail) => None
    }

}

object ServiceClient {

}