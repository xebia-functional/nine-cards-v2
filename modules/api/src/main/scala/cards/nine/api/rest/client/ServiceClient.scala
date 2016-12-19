package cards.nine.api.rest.client

import cards.nine.commons.NineCardExtensions._
import cards.nine.commons.services.TaskService
import cards.nine.commons.services.TaskService._
import cards.nine.api.rest.client.http.{HttpClient, HttpClientResponse}
import cards.nine.api.rest.client.messages.ServiceClientResponse
import monix.eval.Task
import play.api.libs.json.{Json, Reads, Writes}

import scala.util.{Failure, Success, Try}

class ServiceClient(httpClient: HttpClient, val baseUrl: String)
    extends ImplicitsServiceClientExceptions {

  def get[Res](
      path: String,
      headers: Seq[(String, String)] = Seq.empty,
      reads: Option[Reads[Res]] = None,
      emptyResponse: Boolean = false
  ): TaskService[ServiceClientResponse[Res]] =
    for {
      clientResponse <- httpClient
        .doGet(baseUrl.concat(path), headers)
        .resolve[ServiceClientException]
      response <- readResponse(clientResponse, reads, emptyResponse)
    } yield ServiceClientResponse(clientResponse.statusCode.intValue, response)

  def emptyPost[Res](
      path: String,
      headers: Seq[(String, String)] = Seq.empty,
      reads: Option[Reads[Res]] = None,
      emptyResponse: Boolean = false
  ): TaskService[ServiceClientResponse[Res]] =
    for {
      clientResponse <- httpClient
        .doPost(baseUrl.concat(path), headers)
        .resolve[ServiceClientException]
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
      clientResponse <- httpClient
        .doPost[Req](baseUrl.concat(path), headers, body)
        .resolve[ServiceClientException]
      response <- readResponse(clientResponse, reads, emptyResponse)
    } yield ServiceClientResponse(clientResponse.statusCode, response)

  def emptyPut[Res](
      path: String,
      headers: Seq[(String, String)] = Seq.empty,
      reads: Option[Reads[Res]] = None,
      emptyResponse: Boolean = false
  ): TaskService[ServiceClientResponse[Res]] =
    for {
      clientResponse <- httpClient
        .doPut(baseUrl.concat(path), headers)
        .resolve[ServiceClientException]
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
      httpResponse <- httpClient
        .doPut[Req](baseUrl.concat(path), headers, body)
        .resolve[ServiceClientException]
      response <- readResponse(httpResponse, reads, emptyResponse)
    } yield ServiceClientResponse(httpResponse.statusCode, response)

  def delete[Res](
      path: String,
      headers: Seq[(String, String)] = Seq.empty,
      reads: Option[Reads[Res]] = None,
      emptyResponse: Boolean = false
  ): TaskService[ServiceClientResponse[Res]] =
    for {
      clientResponse <- httpClient
        .doDelete(baseUrl.concat(path), headers)
        .resolve[ServiceClientException]
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
          Left(
            ServiceClientException(
              s"Error making request. Status code ${clientResponse.statusCode}"))
        } else {
          (clientResponse.body, emptyResponse, maybeReads) match {
            case (Some(d), false, Some(r)) => transformResponse[T](d, r)
            case (None, false, _)          => Left(ServiceClientException("No content"))
            case (Some(d), false, None) =>
              Left(ServiceClientException("No transformer found for type"))
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
