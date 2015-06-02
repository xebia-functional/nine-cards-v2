package com.fortysevendeg.rest.client.http

import akka.actor.ActorSystem
import com.fortysevendeg.BaseTestSupport
import com.fortysevendeg.rest.client.{SampleResponse, SampleRequest}
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope
import play.api.libs.json.Json
import spray.http._

import scala.concurrent.duration.Duration
import scala.concurrent.{ExecutionContext, Await, Future}

trait SprayHttpClientSupport
  extends Mockito
  with Scope {

  implicit val executionContext: ExecutionContext = ExecutionContext.Implicits.global

  implicit val actorSystem: ActorSystem = ActorSystem("http-spray-client")

  val baseUrl = "http://sampleUrl"

  val mockResponse = mock[HttpResponse]
  val mockStatus = mock[StatusCode]

  implicit val readsResponse = Json.reads[SampleResponse]
  implicit val readsRequest = Json.reads[SampleRequest]
  implicit val writesRequest = Json.writes[SampleRequest]

  val acceptedMethod: Option[HttpMethod] = None

  val acceptedBody: Option[SampleRequest] = None

  private def isValidMethod(req: HttpRequest): Boolean =
    acceptedMethod.getOrElse(req.method) == req.method

  private def isValidBody(req: HttpRequest): Boolean = {
    acceptedBody match {
      case Some(request) => Json.parse(req.entity.asString).as[SampleRequest](readsRequest) == request
      case _ => true
    }
  }

  val sprayHttpClient = new SprayHttpClient() {
    override def sendAndReceive(implicit executionContext: ExecutionContext) = {
      (req: HttpRequest) => {
        if (isValidMethod(req) && isValidBody(req))
          Future.successful(mockResponse)
        else
          Future.failed(new IllegalArgumentException)
      }
    }
  }

}

class SprayHttpClientSpec
    extends Specification
    with BaseTestSupport {

  "SprayHttpClient component" should {

    "returns the response for a successfully get request" in new SprayHttpClientSupport {

      mockResponse.status returns mockStatus
      mockStatus.isSuccess returns true

      val message = "Hello World!"

      val json = s"""{ "message" : "$message" }"""

      val body = HttpEntity(MediaTypes.`application/json`, json.getBytes)
      mockResponse.entity returns body

      override val acceptedMethod = Some(HttpMethods.GET)

      val response = Await.result(sprayHttpClient.doGet(baseUrl, Seq.empty), Duration.Inf)
      response.body shouldEqual Some(json)
    }

    "returns the response for a successfully delete request" in new SprayHttpClientSupport {

      mockResponse.status returns mockStatus
      mockStatus.isSuccess returns true

      val message = "Hello World!"

      val json = s"""{ "message" : "$message" }"""

      val body = HttpEntity(MediaTypes.`application/json`, json.getBytes)
      mockResponse.entity returns body

      override val acceptedMethod = Some(HttpMethods.DELETE)

      val response = Await.result(sprayHttpClient.doDelete(baseUrl, Seq.empty), Duration.Inf)
      response.body shouldEqual Some(json)
    }

    "returns the response for a successfully empty post request" in new SprayHttpClientSupport {

      mockResponse.status returns mockStatus
      mockStatus.isSuccess returns true

      val message = "Hello World!"

      val json = s"""{ "message" : "$message" }"""

      val body = HttpEntity(MediaTypes.`application/json`, json.getBytes)
      mockResponse.entity returns body

      override val acceptedMethod = Some(HttpMethods.POST)

      val response = Await.result(sprayHttpClient.doPost(baseUrl, Seq.empty), Duration.Inf)
      response.body shouldEqual Some(json)
    }

    "returns the response for a successfully post request" in new SprayHttpClientSupport {

      mockResponse.status returns mockStatus
      mockStatus.isSuccess returns true

      val message = "Hello World!"

      val json = s"""{ "message" : "$message" }"""

      val body = HttpEntity(MediaTypes.`application/json`, json.getBytes)
      mockResponse.entity returns body

      override val acceptedMethod = Some(HttpMethods.POST)

      val sampleRequest = SampleRequest("request")
      override val acceptedBody = Some(sampleRequest)

      val response = Await.result(sprayHttpClient.doPost[SampleRequest](baseUrl, Seq.empty, sampleRequest), Duration.Inf)
      response.body shouldEqual Some(json)
    }

    "returns the response for a successfully empty put request" in new SprayHttpClientSupport {

      mockResponse.status returns mockStatus
      mockStatus.isSuccess returns true

      val message = "Hello World!"

      val json = s"""{ "message" : "$message" }"""

      val body = HttpEntity(MediaTypes.`application/json`, json.getBytes)
      mockResponse.entity returns body

      override val acceptedMethod = Some(HttpMethods.PUT)

      val response = Await.result(sprayHttpClient.doPut(baseUrl, Seq.empty), Duration.Inf)
      response.body shouldEqual Some(json)
    }

    "returns the response for a successfully put request" in new SprayHttpClientSupport {

      mockResponse.status returns mockStatus
      mockStatus.isSuccess returns true

      val message = "Hello World!"

      val json = s"""{ "message" : "$message" }"""

      val body = HttpEntity(MediaTypes.`application/json`, json.getBytes)
      mockResponse.entity returns body

      override val acceptedMethod = Some(HttpMethods.PUT)

      val sampleRequest = SampleRequest("request")
      override val acceptedBody = Some(sampleRequest)

      val response = Await.result(sprayHttpClient.doPut[SampleRequest](baseUrl, Seq.empty, sampleRequest), Duration.Inf)
      response.body shouldEqual Some(json)
    }

    "returns Exception for an unexpected method" in new SprayHttpClientSupport {

      mockResponse.status returns mockStatus
      mockStatus.isSuccess returns true

      val message = "Hello World!"

      val json = s"""{ "message" : "$message" }"""

      val body = HttpEntity(MediaTypes.`application/json`, json.getBytes)
      mockResponse.entity returns body

      override val acceptedMethod = Some(HttpMethods.GET)

      Await.result(sprayHttpClient.doDelete(baseUrl, Seq.empty), Duration.Inf) must throwA[IllegalArgumentException]
    }

    "returns Exception for an unexpected request" in new SprayHttpClientSupport {

      mockResponse.status returns mockStatus
      mockStatus.isSuccess returns true

      val message = "Hello World!"

      val json = s"""{ "message" : "$message" }"""

      val body = HttpEntity(MediaTypes.`application/json`, json.getBytes)
      mockResponse.entity returns body

      override val acceptedMethod = Some(HttpMethods.POST)

      override val acceptedBody = Some(SampleRequest("request"))

      Await.result(sprayHttpClient.doPut[SampleRequest](baseUrl, Seq.empty, SampleRequest("bad_request")), Duration.Inf) must throwA[IllegalArgumentException]
    }

  }

}
