package com.fortysevendeg.rest.client

import com.fortysevendeg.BaseTestSupport
import com.fortysevendeg.rest.client.http.{HttpClient, HttpClientResponse}
import com.fortysevendeg.rest.client.messages.{ServiceClientException, ServiceClientResponse}
import org.hamcrest.core.IsEqual
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope
import play.api.libs.json.Json

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

trait ServiceClientSupport
    extends Mockito {

  val baseUrl = "http://sampleUrl"

  implicit val executionContext: ExecutionContext = ExecutionContext.Implicits.global

  implicit val readsResponse = Json.reads[SampleResponse]
  implicit val writesRequest = Json.writes[SampleRequest]

  val httpClient = mock[HttpClient]

  val serviceClient = new ServiceClient(httpClient, baseUrl)
}

trait WithSuccessfullyHttpClientMock
    extends Mockito
    with Scope {

  val httpClient: HttpClient

  val mockResponse = mock[HttpClientResponse]

  mockResponse.statusCode returns 200

  val message = "Hello World!"

  val json = s"""{ "message" : "$message" }"""

  val sampleResponse = ServiceClientResponse(200, Some(SampleResponse(message)))

  mockResponse.body returns Some(json)

  httpClient.doGet(any, any)(any) returns Future.successful(mockResponse)

  httpClient.doDelete(any, any)(any) returns Future.successful(mockResponse)

  httpClient.doPost(any, any)(any) returns Future.successful(mockResponse)

  httpClient.doPost[SampleRequest](any, any, any)(any, any) returns Future.successful(mockResponse)

  httpClient.doPut(any, any)(any) returns Future.successful(mockResponse)

  httpClient.doPut[SampleRequest](any, any, any)(any, any) returns Future.successful(mockResponse)

}

trait WithFailedHttpClientMock
    extends Mockito
    with Scope {

  val httpClient: HttpClient

  val exception = new IllegalArgumentException

  httpClient.doGet(any, any)(any) returns Future.failed(exception)

  httpClient.doDelete(any, any)(any) returns Future.failed(exception)

  httpClient.doPost(any, any)(any) returns Future.failed(exception)

  httpClient.doPost[SampleRequest](any, any, any)(any, any) returns Future.failed(exception)

  httpClient.doPut(any, any)(any) returns Future.failed(exception)

  httpClient.doPut[SampleRequest](any, any, any)(any, any) returns Future.failed(exception)

}

case class Test(value: Int)

class ServiceClientSpec
    extends Specification
    with BaseTestSupport {

  "Service Client component" should {

    "returns a valid response for a valid call to get with response" in
        new ServiceClientSupport with WithSuccessfullyHttpClientMock {
          val response = Await.result(serviceClient.get[SampleResponse](baseUrl, Seq.empty, Some(readsResponse)), Duration.Inf)
          there was one(httpClient).doGet(any, any)(any)
          there was noMoreCallsTo(httpClient)
          response shouldEqual sampleResponse
        }

    "returns a valid response for a valid call to get without response" in
        new ServiceClientSupport with WithSuccessfullyHttpClientMock {
          val response = Await.result(serviceClient.get(baseUrl, Seq.empty, None, true), Duration.Inf)
          there was one(httpClient).doGet(any, any)(any)
          there was noMoreCallsTo(httpClient)
          response.data shouldEqual None
        }

    "returns a valid response for a valid call to delete with response" in
        new ServiceClientSupport with WithSuccessfullyHttpClientMock {
          val response = Await.result(serviceClient.delete[SampleResponse](baseUrl, Seq.empty, Some(readsResponse)), Duration.Inf)
          there was one(httpClient).doDelete(any, any)(any)
          there was noMoreCallsTo(httpClient)
          response shouldEqual sampleResponse
        }

    "returns a valid response for a valid call to post" in
        new ServiceClientSupport with WithSuccessfullyHttpClientMock {
          val response = Await.result(serviceClient.emptyPost[SampleResponse](baseUrl, Seq.empty, Some(readsResponse)), Duration.Inf)
          there was one(httpClient).doPost(any, any)(any)
          there was noMoreCallsTo(httpClient)
          response shouldEqual sampleResponse
        }

    "returns a valid response for a valid call to post with valid arguments" in
        new ServiceClientSupport with WithSuccessfullyHttpClientMock {
          val request = SampleRequest("sample-request")
          val response = Await.result(serviceClient.post[SampleRequest, SampleResponse](baseUrl, Seq.empty, request, Some(readsResponse)), Duration.Inf)
          there was one(httpClient).doPost[SampleRequest](any, any, anArgThat(IsEqual.equalTo(request)))(any, any)
          there was noMoreCallsTo(httpClient)
          response shouldEqual sampleResponse
        }

    "returns a valid response for a valid call to put" in
        new ServiceClientSupport with WithSuccessfullyHttpClientMock {
          val response = Await.result(serviceClient.emptyPut[SampleResponse](baseUrl, Seq.empty, Some(readsResponse)), Duration.Inf)
          there was one(httpClient).doPut(any, any)(any)
          there was noMoreCallsTo(httpClient)
          response shouldEqual sampleResponse
        }

    "returns a valid response for a valid call to put with valid arguments" in
        new ServiceClientSupport with WithSuccessfullyHttpClientMock {
          val request = SampleRequest("sample-request")
          val response = Await.result(serviceClient.put[SampleRequest, SampleResponse](baseUrl, Seq.empty, request, Some(readsResponse)), Duration.Inf)
          there was one(httpClient).doPut[SampleRequest](any, any, anArgThat(IsEqual.equalTo(request)))(any, any)
          there was noMoreCallsTo(httpClient)
          response shouldEqual sampleResponse
        }

    "throws a ServiceClientException when no Reads found for the response type" in
        new ServiceClientSupport with WithSuccessfullyHttpClientMock {
          Await.result(
            serviceClient.get[Test](baseUrl, Seq.empty),
            Duration.Inf) must throwA[ServiceClientException]
        }

    "returns a failed response when the call to get method throw an exception" in
        new ServiceClientSupport with WithFailedHttpClientMock {
          Await.result(
            serviceClient.get[SampleResponse](baseUrl, Seq.empty, Some(readsResponse)),
            Duration.Inf) must throwA[IllegalArgumentException]
        }

    "returns a failed response when the call to delete method throw an exception" in
        new ServiceClientSupport with WithFailedHttpClientMock {
          Await.result(
            serviceClient.delete[SampleResponse](baseUrl, Seq.empty, Some(readsResponse)),
            Duration.Inf) must throwA[IllegalArgumentException]
        }

    "returns a failed response when the call to post method throw an exception" in
        new ServiceClientSupport with WithFailedHttpClientMock {
          Await.result(
            serviceClient.emptyPost[SampleResponse](baseUrl, Seq.empty, Some(readsResponse)),
            Duration.Inf) must throwA[IllegalArgumentException]
        }

    "returns a failed response when the call to put method throw an exception" in
        new ServiceClientSupport with WithFailedHttpClientMock {
          Await.result(
            serviceClient.emptyPut[SampleResponse](baseUrl, Seq.empty, Some(readsResponse)),
            Duration.Inf) must throwA[IllegalArgumentException]
        }

  }

}
