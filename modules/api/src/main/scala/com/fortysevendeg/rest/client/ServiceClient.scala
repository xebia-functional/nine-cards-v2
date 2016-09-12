package com.fortysevendeg.rest.client

import com.fortysevendeg.ninecardslauncher.commons.services.TaskService
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService._
import com.fortysevendeg.rest.client.http.{HttpClient, HttpClientResponse}
import com.fortysevendeg.rest.client.messages.ServiceClientResponse
import monix.eval.Task
import play.api.libs.json.{Json, Reads, Writes}
import scala.util.{Failure, Success, Try}

class ServiceClient(httpClient: HttpClient, val baseUrl: String) {

  def get[Res](
    path: String,
    headers: Seq[(String, String)] = Seq.empty,
    reads: Option[Reads[Res]] = None,
    emptyResponse: Boolean = false
    ): TaskService[ServiceClientResponse[Res]] =
    for {
      clientResponse <- httpClient.doGet(baseUrl.concat(path), headers)
      response <- readResponse(clientResponse, reads, emptyResponse)
    } yield ServiceClientResponse(clientResponse.statusCode.intValue, response)


  def emptyPost[Res](
    path: String,
    headers: Seq[(String, String)] = Seq.empty,
    reads: Option[Reads[Res]] = None,
    emptyResponse: Boolean = false
    ): TaskService[ServiceClientResponse[Res]] =
    for {
      clientResponse <- httpClient.doPost(baseUrl.concat(path), headers)
      response <- readResponse(clientResponse, reads, emptyResponse)
    } yield ServiceClientResponse(clientResponse.statusCode, response)


  def post[Req, Res](
    path: String,
    headers: Seq[(String, String)] = Seq.empty,
    body: Req,
    reads: Option[Reads[Res]] = None,
    emptyResponse: Boolean = false
    )(implicit writes: Writes[Req]): TaskService[ServiceClientResponse[Res]] =
    for {
      clientResponse <- httpClient.doPost[Req](baseUrl.concat(path), headers, body)
      response <- readResponse(clientResponse, reads, emptyResponse)
    } yield ServiceClientResponse(clientResponse.statusCode, response)

  def emptyPut[Res](
    path: String,
    headers: Seq[(String, String)] = Seq.empty,
    reads: Option[Reads[Res]] = None,
    emptyResponse: Boolean = false
    ): TaskService[ServiceClientResponse[Res]] =
    for {
      clientResponse <- httpClient.doPut(baseUrl.concat(path), headers)
      response <- readResponse(clientResponse, reads, emptyResponse)
    } yield ServiceClientResponse(clientResponse.statusCode, response)

  def put[Req, Res](
    path: String,
    headers: Seq[(String, String)] = Seq.empty,
    body: Req,
    reads: Option[Reads[Res]] = None,
    emptyResponse: Boolean = false
    )(implicit writes: Writes[Req]): TaskService[ServiceClientResponse[Res]] =
    for {
      httpResponse <- httpClient.doPut[Req](baseUrl.concat(path), headers, body)
      response <- readResponse(httpResponse, reads, emptyResponse)
    } yield ServiceClientResponse(httpResponse.statusCode, response)


  def delete[Res](
    path: String,
    headers: Seq[(String, String)] = Seq.empty,
    reads: Option[Reads[Res]] = None,
    emptyResponse: Boolean = false
    ): TaskService[ServiceClientResponse[Res]] =
    for {
      clientResponse <- httpClient.doDelete(baseUrl.concat(path), headers)
      response <- readResponse(clientResponse, reads, emptyResponse)
    } yield ServiceClientResponse(clientResponse.statusCode, response)

  private def readResponse[T](
    clientResponse: HttpClientResponse,
    maybeReads: Option[Reads[T]],
    emptyResponse: Boolean
    ): TaskService[Option[T]] = {

    def isError: Boolean =
      clientResponse.statusCode >= 400 && clientResponse.statusCode < 600

    TaskService {
      Task {
        if (isError) {
          val errorMessage = clientResponse.body getOrElse "No content"
          Left(ServiceClientException(s"Status code ${clientResponse.statusCode}. $errorMessage"))
        } else {
          (clientResponse.body, emptyResponse, maybeReads) match {
            case (Some(d), false, Some(r)) => transformResponse[T](d, r)
            case (None, false, _) => Left(ServiceClientException("No content"))
            case (Some(d), false, None) => Left(ServiceClientException("No transformer found for type"))
            case _ => Right(None)
          }
        }
      }
    }
  }

  private def transformResponse[T](
    content: String,
    reads: Reads[T]
    ): Either[ServiceClientException, Some[T]] =
    Try(Json.parse(content).as[T](reads)) match {
      case Success(s) => Right(Some(s))
      case Failure(e) => Left(ServiceClientException(message = e.getMessage, cause = Some(e)))
    }

}

object ServiceClient {

}