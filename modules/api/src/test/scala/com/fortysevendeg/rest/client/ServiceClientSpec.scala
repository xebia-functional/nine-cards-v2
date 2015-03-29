package com.fortysevendeg.rest.client

import com.fortysevendeg.BaseTestSupport
import com.fortysevendeg.rest.client.messages.{HttpClientException, HttpClientResponse}
import org.specs2.mock.Mockito
import org.hamcrest.core.IsEqual
import org.specs2.mutable.Specification
import org.specs2.specification.Scope
import spray.http.{MediaTypes, HttpEntity, StatusCode, HttpResponse}

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

trait WithSuccessfullyHttpClient
    extends ServiceClient
    with ClientSupport
    with Mockito
    with Scope {

  override val httpClient = mock[HttpClient]

  val mockResponse = mock[HttpResponse]
  val mockStatus = mock[StatusCode]

  mockResponse.status returns mockStatus
  mockStatus.isSuccess returns true
  mockStatus.intValue returns 200

  val message = "Hello World!"

  val json = s"""{ "message" : "$message" }"""

  val sampleResponse = HttpClientResponse(200, Some(SampleResponse(message)))

  val body = HttpEntity(MediaTypes.`application/json`, json.getBytes)
  mockResponse.entity returns body

  httpClient.doGet(any, any) returns Future.successful(mockResponse)

  httpClient.doDelete(any, any) returns Future.successful(mockResponse)

  httpClient.doPost(any, any) returns Future.successful(mockResponse)

  httpClient.doPost[SampleRequest](any, any, any)(any, any) returns Future.successful(mockResponse)

  httpClient.doPut(any, any) returns Future.successful(mockResponse)

  httpClient.doPut[SampleRequest](any, any, any)(any, any) returns Future.successful(mockResponse)

}

trait WithFailedHttpClient
    extends ServiceClient
    with ClientSupport
    with Mockito
    with Scope {

  override val httpClient = mock[HttpClient]

  val exception = new IllegalArgumentException

  httpClient.doGet(any, any) returns Future.failed(exception)

  httpClient.doDelete(any, any) returns Future.failed(exception)

  httpClient.doPost(any, any) returns Future.failed(exception)

  httpClient.doPost[SampleRequest](any, any, any)(any, any) returns Future.failed(exception)

  httpClient.doPut(any, any) returns Future.failed(exception)

  httpClient.doPut[SampleRequest](any, any, any)(any, any) returns Future.failed(exception)

}

case class Test(value: Int)

class ServiceClientSpec
    extends Specification
    with BaseTestSupport {

  "Service Client component" should {

    "returns a valid response for a valid call to get with response" in
        new WithSuccessfullyHttpClient {
          val response = Await.result(get[SampleResponse](baseUrl, Seq.empty), Duration.Inf)
          there was one(httpClient).doGet(any, any)
          there was noMoreCallsTo(httpClient)
          response shouldEqual sampleResponse
        }

    "returns a valid response for a valid call to get without response" in
        new WithSuccessfullyHttpClient {
          val response = Await.result(get[Unit](baseUrl, Seq.empty), Duration.Inf)
          there was one(httpClient).doGet(any, any)
          there was noMoreCallsTo(httpClient)
          response.data shouldEqual None
        }

    "returns a valid response for a valid call to delete with response" in
        new WithSuccessfullyHttpClient {
          val response = Await.result(delete[SampleResponse](baseUrl, Seq.empty), Duration.Inf)
          there was one(httpClient).doDelete(any, any)
          there was noMoreCallsTo(httpClient)
          response shouldEqual sampleResponse
        }

    "returns a valid response for a valid call to post" in
        new WithSuccessfullyHttpClient {
          val response = Await.result(emptyPost[SampleResponse](baseUrl, Seq.empty), Duration.Inf)
          there was one(httpClient).doPost(any, any)
          there was noMoreCallsTo(httpClient)
          response shouldEqual sampleResponse
        }

    "returns a valid response for a valid call to post with valid arguments" in
        new WithSuccessfullyHttpClient {
          val request = SampleRequest("sample-request")
          val response = Await.result(post[SampleRequest, SampleResponse](baseUrl, Seq.empty, request), Duration.Inf)
          there was one(httpClient).doPost[SampleRequest](any, any, anArgThat(IsEqual.equalTo(request)))(any, any)
          there was noMoreCallsTo(httpClient)
          response shouldEqual sampleResponse
        }

    "returns a valid response for a valid call to put" in
        new WithSuccessfullyHttpClient {
          val response = Await.result(emptyPut[SampleResponse](baseUrl, Seq.empty), Duration.Inf)
          there was one(httpClient).doPut(any, any)
          there was noMoreCallsTo(httpClient)
          response shouldEqual sampleResponse
        }

    "returns a valid response for a valid call to put with valid arguments" in
        new WithSuccessfullyHttpClient {
          val request = SampleRequest("sample-request")
          val response = Await.result(put[SampleRequest, SampleResponse](baseUrl, Seq.empty, request), Duration.Inf)
          there was one(httpClient).doPut[SampleRequest](any, any, anArgThat(IsEqual.equalTo(request)))(any, any)
          there was noMoreCallsTo(httpClient)
          response shouldEqual sampleResponse
        }

    "throws a HttpClientException when no Reads found for the response type" in
        new WithSuccessfullyHttpClient {
          Await.result(
            get[Test](baseUrl, Seq.empty),
            Duration.Inf) must throwA[HttpClientException]
        }

    "returns a failed response when the call to get method throw an exception" in
        new WithFailedHttpClient {
          Await.result(get[SampleResponse](baseUrl, Seq.empty), Duration.Inf) must throwA[IllegalArgumentException]
        }

    "returns a failed response when the call to delete method throw an exception" in
        new WithFailedHttpClient {
          Await.result(delete[SampleResponse](baseUrl, Seq.empty), Duration.Inf) must throwA[IllegalArgumentException]
        }

    "returns a failed response when the call to post method throw an exception" in
        new WithFailedHttpClient {
          Await.result(emptyPost[SampleResponse](baseUrl, Seq.empty), Duration.Inf) must throwA[IllegalArgumentException]
        }

    "returns a failed response when the call to put method throw an exception" in
        new WithFailedHttpClient {
          Await.result(emptyPut[SampleResponse](baseUrl, Seq.empty), Duration.Inf) must throwA[IllegalArgumentException]
        }

  }

}
