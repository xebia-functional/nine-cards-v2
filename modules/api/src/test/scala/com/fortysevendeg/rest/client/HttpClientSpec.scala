package com.fortysevendeg.rest.client

import com.fortysevendeg.BaseTestSupport
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope
import play.api.libs.json.Json
import spray.http._

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

case class SampleRequest(message: String)

case class SampleResponse(message: String)

trait HttpClientSupport
  extends HttpClient
  with ClientSupport
  with Mockito
  with Scope {

  val mockResponse = mock[HttpResponse]
  val mockStatus = mock[StatusCode]

  val acceptedMethod: Option[HttpMethod] = None

  val acceptedBody: Option[SampleRequest] = None

  override def sendAndReceive = {
    (req: HttpRequest) => {
      if (isValidMethod(req) && isValidBody(req))
        Future.successful(mockResponse)
      else
        Future.failed(new IllegalArgumentException)
    }
  }

  private def isValidMethod(req: HttpRequest): Boolean =
    acceptedMethod.getOrElse(req.method) == req.method

  private def isValidBody(req: HttpRequest): Boolean = {
    acceptedBody match {
      case Some(request) => Json.parse(req.entity.asString).as[SampleRequest] == request
      case _ => true
    }
  }

}

class HttpClientSpec
    extends Specification
    with BaseTestSupport {

  "HttpClient component" should {

    "returns the response for a successfully get request" in new HttpClientSupport {

      mockResponse.status returns mockStatus
      mockStatus.isSuccess returns true

      val message = "Hello World!"

      val json = s"""{ "message" : "$message" }"""

      val body = HttpEntity(MediaTypes.`application/json`, json.getBytes)
      mockResponse.entity returns body

      override val acceptedMethod = Some(HttpMethods.GET)

      val response = Await.result(doGet[SampleResponse](baseUrl, Seq.empty), Duration.Inf)
      response shouldEqual SampleResponse(message)
    }

    "returns the response for a successfully delete request" in new HttpClientSupport {

      mockResponse.status returns mockStatus
      mockStatus.isSuccess returns true

      val message = "Hello World!"

      val json = s"""{ "message" : "$message" }"""

      val body = HttpEntity(MediaTypes.`application/json`, json.getBytes)
      mockResponse.entity returns body

      override val acceptedMethod = Some(HttpMethods.DELETE)

      val response = Await.result(doDelete[SampleResponse](baseUrl, Seq.empty), Duration.Inf)
      response shouldEqual SampleResponse(message)
    }

    "returns the response for a successfully empty post request" in new HttpClientSupport {

      mockResponse.status returns mockStatus
      mockStatus.isSuccess returns true

      val message = "Hello World!"

      val json = s"""{ "message" : "$message" }"""

      val body = HttpEntity(MediaTypes.`application/json`, json.getBytes)
      mockResponse.entity returns body

      override val acceptedMethod = Some(HttpMethods.POST)

      val response = Await.result(doPost[SampleResponse](baseUrl, Seq.empty), Duration.Inf)
      response shouldEqual SampleResponse(message)
    }

    "returns the response for a successfully post request" in new HttpClientSupport {

      mockResponse.status returns mockStatus
      mockStatus.isSuccess returns true

      val message = "Hello World!"

      val json = s"""{ "message" : "$message" }"""

      val body = HttpEntity(MediaTypes.`application/json`, json.getBytes)
      mockResponse.entity returns body

      override val acceptedMethod = Some(HttpMethods.POST)

      val sampleRequest = SampleRequest("request")
      override val acceptedBody = Some(sampleRequest)

      val response = Await.result(doPost[SampleRequest, SampleResponse](baseUrl, Seq.empty, sampleRequest), Duration.Inf)
      response shouldEqual SampleResponse(message)
    }

    "returns the response for a successfully empty put request" in new HttpClientSupport {

      mockResponse.status returns mockStatus
      mockStatus.isSuccess returns true

      val message = "Hello World!"

      val json = s"""{ "message" : "$message" }"""

      val body = HttpEntity(MediaTypes.`application/json`, json.getBytes)
      mockResponse.entity returns body

      override val acceptedMethod = Some(HttpMethods.PUT)

      val response = Await.result(doPut[SampleResponse](baseUrl, Seq.empty), Duration.Inf)
      response shouldEqual SampleResponse(message)
    }

    "returns the response for a successfully put request" in new HttpClientSupport {

      mockResponse.status returns mockStatus
      mockStatus.isSuccess returns true

      val message = "Hello World!"

      val json = s"""{ "message" : "$message" }"""

      val body = HttpEntity(MediaTypes.`application/json`, json.getBytes)
      mockResponse.entity returns body

      override val acceptedMethod = Some(HttpMethods.PUT)

      val sampleRequest = SampleRequest("request")
      override val acceptedBody = Some(sampleRequest)

      val response = Await.result(doPut[SampleRequest, SampleResponse](baseUrl, Seq.empty, sampleRequest), Duration.Inf)
      response shouldEqual SampleResponse(message)
    }

    "returns Exception for an unexpected method" in new HttpClientSupport {

      mockResponse.status returns mockStatus
      mockStatus.isSuccess returns true

      val message = "Hello World!"

      val json = s"""{ "message" : "$message" }"""

      val body = HttpEntity(MediaTypes.`application/json`, json.getBytes)
      mockResponse.entity returns body

      override val acceptedMethod = Some(HttpMethods.GET)

      Await.result(doDelete[SampleResponse](baseUrl, Seq.empty), Duration.Inf) must throwA[IllegalArgumentException]
    }

    "returns Exception for an unexpected request" in new HttpClientSupport {

      mockResponse.status returns mockStatus
      mockStatus.isSuccess returns true

      val message = "Hello World!"

      val json = s"""{ "message" : "$message" }"""

      val body = HttpEntity(MediaTypes.`application/json`, json.getBytes)
      mockResponse.entity returns body

      override val acceptedMethod = Some(HttpMethods.POST)

      override val acceptedBody = Some(SampleRequest("request"))

      Await.result(doPut[SampleRequest, SampleResponse](baseUrl, Seq.empty, SampleRequest("bad_request")), Duration.Inf) must throwA[IllegalArgumentException]
    }

  }

}
