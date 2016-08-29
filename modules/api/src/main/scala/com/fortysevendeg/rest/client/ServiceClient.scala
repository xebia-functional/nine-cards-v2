package com.fortysevendeg.rest.client

import com.fortysevendeg.ninecardslauncher.commons.services.Service
import com.fortysevendeg.ninecardslauncher.commons.services.Service.ServiceDef2
import com.fortysevendeg.rest.client.http.{HttpClient, HttpClientException, HttpClientResponse}
import com.fortysevendeg.rest.client.messages.ServiceClientResponse
import play.api.libs.json.{Json, Reads, Writes}
import rapture.core.{Answer, Errata, Result}

import scala.util.{Failure, Success, Try}
import scalaz.concurrent.Task

class ServiceClient(httpClient: HttpClient, val baseUrl: String) {

  def get[Res](
    path: String,
    headers: Seq[(String, String)] = Seq.empty,
    reads: Option[Reads[Res]] = None,
    emptyResponse: Boolean = false
    ): ServiceDef2[ServiceClientResponse[Res], HttpClientException with ServiceClientException] =
    for {
      clientResponse <- httpClient.doGet(baseUrl.concat(path), headers)
      response <- readResponse(clientResponse, reads, emptyResponse)
    } yield ServiceClientResponse(clientResponse.statusCode.intValue, response)


  def emptyPost[Res](
    path: String,
    headers: Seq[(String, String)] = Seq.empty,
    reads: Option[Reads[Res]] = None,
    emptyResponse: Boolean = false
    ): ServiceDef2[ServiceClientResponse[Res], HttpClientException with ServiceClientException] =
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
    )(implicit writes: Writes[Req]): ServiceDef2[ServiceClientResponse[Res], HttpClientException with ServiceClientException] =
    for {
      clientResponse <- httpClient.doPost[Req](baseUrl.concat(path), headers, body)
      response <- readResponse(clientResponse, reads, emptyResponse)
    } yield ServiceClientResponse(clientResponse.statusCode, response)

  def emptyPut[Res](
    path: String,
    headers: Seq[(String, String)] = Seq.empty,
    reads: Option[Reads[Res]] = None,
    emptyResponse: Boolean = false
    ): ServiceDef2[ServiceClientResponse[Res], HttpClientException with ServiceClientException] =
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
    )(implicit writes: Writes[Req]): ServiceDef2[ServiceClientResponse[Res], HttpClientException with ServiceClientException] =
    for {
      httpResponse <- httpClient.doPut[Req](baseUrl.concat(path), headers, body)
      response <- readResponse(httpResponse, reads, emptyResponse)
    } yield ServiceClientResponse(httpResponse.statusCode, response)


  def delete[Res](
    path: String,
    headers: Seq[(String, String)] = Seq.empty,
    reads: Option[Reads[Res]] = None,
    emptyResponse: Boolean = false
    ): ServiceDef2[ServiceClientResponse[Res], HttpClientException with ServiceClientException] =
    for {
      clientResponse <- httpClient.doDelete(baseUrl.concat(path), headers)
      response <- readResponse(clientResponse, reads, emptyResponse)
    } yield ServiceClientResponse(clientResponse.statusCode, response)

  private def readResponse[T](
    clientResponse: HttpClientResponse,
    maybeReads: Option[Reads[T]],
    emptyResponse: Boolean
    ): ServiceDef2[Option[T], ServiceClientException] = {

    def isError: Boolean =
      clientResponse.statusCode >= 400 && clientResponse.statusCode < 600

    Service {
      Task {
        if (isError) {
          val errorMessage = clientResponse.body getOrElse "No content"
          Errata(ServiceClientExceptionImpl(s"Status code ${clientResponse.statusCode}. $errorMessage"))
        } else {
          (clientResponse.body, emptyResponse, maybeReads) match {
            case (Some(d), false, Some(r)) => transformResponse[T](d, r)
            case (None, false, _) => Errata(ServiceClientExceptionImpl("No content"))
            case (Some(d), false, None) => Errata(ServiceClientExceptionImpl("No transformer found for type"))
            case _ => Answer(None)
          }
        }
      }
    }
  }

  private def transformResponse[T](
    content: String,
    reads: Reads[T]
    ): Result[Option[T], ServiceClientException] =
    Try(Json.parse(content).as[T](reads)) match {
      case Success(s) => Answer(Some(s))
      case Failure(e) => Errata(ServiceClientExceptionImpl(message = e.getMessage, cause = Some(e)))
    }

}

object ServiceClient {

}