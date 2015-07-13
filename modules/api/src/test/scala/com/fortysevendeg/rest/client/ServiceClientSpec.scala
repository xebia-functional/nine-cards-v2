package com.fortysevendeg.rest.client

import com.fortysevendeg.ninecardslauncher.commons.exceptions.Exceptions.NineCardsException
import com.fortysevendeg.rest.client.http.{HttpClient, HttpClientResponse}
import com.fortysevendeg.rest.client.messages.ServiceClientResponse
import org.hamcrest.core.IsEqual
import org.specs2.matcher.DisjunctionMatchers
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope
import play.api.libs.json.Json

import scala.concurrent.ExecutionContext
import scalaz.concurrent.Task
import scalaz.{-\/, \/-}

trait ServiceClientSpecification
  extends Specification
  with DisjunctionMatchers
  with Mockito {

  trait ServiceClientScope
    extends Scope {

    val baseUrl = "http://sampleUrl"

    implicit val executionContext: ExecutionContext = ExecutionContext.Implicits.global

    implicit val readsResponse = Json.reads[SampleResponse]
    implicit val writesRequest = Json.writes[SampleRequest]

    val httpClient = mock[HttpClient]

    val serviceClient = new ServiceClient(httpClient, baseUrl)

  }

  trait WithSuccessfullyHttpClientMock {

    self: ServiceClientScope =>

    val mockResponse = mock[HttpClientResponse]

    mockResponse.statusCode returns 200

    val message = "Hello World!"

    val json = s"""{ "message" : "$message" }"""

    val sampleResponse = Some(SampleResponse(message))

    mockResponse.body returns Some(json)

    httpClient.doGet(any, any) returns Task(\/-(mockResponse))

    httpClient.doDelete(any, any) returns Task(\/-(mockResponse))

    httpClient.doPost(any, any) returns Task(\/-(mockResponse))

    httpClient.doPost[SampleRequest](any, any, any)(any) returns Task(\/-(mockResponse))

    httpClient.doPut(any, any) returns Task(\/-(mockResponse))

    httpClient.doPut[SampleRequest](any, any, any)(any) returns Task(\/-(mockResponse))
  }

  trait WithFailedHttpClientMock {

    self: ServiceClientScope =>

    val exception = NineCardsException("")

    httpClient.doGet(any, any) returns Task(-\/(exception))

    httpClient.doDelete(any, any) returns Task(-\/(exception))

    httpClient.doPost(any, any) returns Task(-\/(exception))

    httpClient.doPost[SampleRequest](any, any, any)(any) returns Task(-\/(exception))

    httpClient.doPut(any, any) returns Task(-\/(exception))

    httpClient.doPut[SampleRequest](any, any, any)(any) returns Task(-\/(exception))
  }

}

case class Test(value: Int)

class ServiceClientSpec
    extends ServiceClientSpecification {

  "Service Client component" should {

    "returns a valid response for a valid call to get with response" in
        new ServiceClientScope with WithSuccessfullyHttpClientMock {
          val response = serviceClient.get[SampleResponse](baseUrl, Seq.empty, Some(readsResponse)).run
          there was one(httpClient).doGet(any, any)
          there was noMoreCallsTo(httpClient)
          response must be_\/-[ServiceClientResponse[SampleResponse]].which { r =>
            r.data shouldEqual sampleResponse
          }
        }

    "returns a valid response for a valid call to get without response" in
        new ServiceClientScope with WithSuccessfullyHttpClientMock {
          val response = serviceClient.get(baseUrl, Seq.empty, None, emptyResponse = true).run
          there was one(httpClient).doGet(any, any)
          there was noMoreCallsTo(httpClient)
          response must be_\/-[ServiceClientResponse[Nothing]].which { r =>
            r.data must beNone
          }
        }

    "returns a valid response for a valid call to delete with response" in
        new ServiceClientScope with WithSuccessfullyHttpClientMock {
          val response = serviceClient.delete[SampleResponse](baseUrl, Seq.empty, Some(readsResponse)).run
          there was one(httpClient).doDelete(any, any)
          there was noMoreCallsTo(httpClient)
          response must be_\/-[ServiceClientResponse[SampleResponse]].which { r =>
            r.data shouldEqual sampleResponse
          }
        }

    "returns a valid response for a valid call to post" in
        new ServiceClientScope with WithSuccessfullyHttpClientMock {
          val response = serviceClient.emptyPost[SampleResponse](baseUrl, Seq.empty, Some(readsResponse)).run
          there was one(httpClient).doPost(any, any)
          there was noMoreCallsTo(httpClient)
          response must be_\/-[ServiceClientResponse[SampleResponse]].which { r =>
            r.data shouldEqual sampleResponse
          }
        }

    "returns a valid response for a valid call to post with valid arguments" in
        new ServiceClientScope with WithSuccessfullyHttpClientMock {
          val request = SampleRequest("sample-request")
          val response = serviceClient.post[SampleRequest, SampleResponse](baseUrl, Seq.empty, request, Some(readsResponse)).run
          there was one(httpClient).doPost[SampleRequest](any, any, anArgThat(IsEqual.equalTo(request)))(any)
          there was noMoreCallsTo(httpClient)
          response must be_\/-[ServiceClientResponse[SampleResponse]].which { r =>
            r.data shouldEqual sampleResponse
          }
        }

    "returns a valid response for a valid call to put" in
        new ServiceClientScope with WithSuccessfullyHttpClientMock {
          val response = serviceClient.emptyPut[SampleResponse](baseUrl, Seq.empty, Some(readsResponse)).run
          there was one(httpClient).doPut(any, any)
          there was noMoreCallsTo(httpClient)
          response must be_\/-[ServiceClientResponse[SampleResponse]].which { r =>
            r.data shouldEqual sampleResponse
          }
        }

    "returns a valid response for a valid call to put with valid arguments" in
        new ServiceClientScope with WithSuccessfullyHttpClientMock {
          val request = SampleRequest("sample-request")
          val response = serviceClient.put[SampleRequest, SampleResponse](baseUrl, Seq.empty, request, Some(readsResponse)).run
          there was one(httpClient).doPut[SampleRequest](any, any, anArgThat(IsEqual.equalTo(request)))(any)
          there was noMoreCallsTo(httpClient)
          response must be_\/-[ServiceClientResponse[SampleResponse]].which { r =>
            r.data shouldEqual sampleResponse
          }
        }

    "throws a ServiceClientException when no Reads found for the response type" in
        new ServiceClientScope with WithSuccessfullyHttpClientMock {
          val response = serviceClient.get[Test](baseUrl, Seq.empty).run
          response must be_-\/[NineCardsException]
        }

    "returns a failed response when the call to get method throw an exception" in
        new ServiceClientScope with WithFailedHttpClientMock {
          val response = serviceClient.get[SampleResponse](baseUrl, Seq.empty, Some(readsResponse)).run
          response must be_-\/[NineCardsException]
        }

    "returns a failed response when the call to delete method throw an exception" in
        new ServiceClientScope with WithFailedHttpClientMock {
          val response = serviceClient.delete[SampleResponse](baseUrl, Seq.empty, Some(readsResponse)).run
          response must be_-\/[NineCardsException]
        }

    "returns a failed response when the call to post method throw an exception" in
        new ServiceClientScope with WithFailedHttpClientMock {
          val response = serviceClient.emptyPost[SampleResponse](baseUrl, Seq.empty, Some(readsResponse)).run
          response must be_-\/[NineCardsException]
        }

    "returns a failed response when the call to put method throw an exception" in
        new ServiceClientScope with WithFailedHttpClientMock {
          val response = serviceClient.emptyPut[SampleResponse](baseUrl, Seq.empty, Some(readsResponse)).run
          response must be_-\/[NineCardsException]
        }

  }

}
