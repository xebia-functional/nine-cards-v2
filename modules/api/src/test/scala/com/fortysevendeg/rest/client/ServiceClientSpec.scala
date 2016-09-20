package com.fortysevendeg.rest.client

import com.fortysevendeg.ninecardslauncher.commons.services.TaskService
import com.fortysevendeg.rest.client.http.{HttpClient, HttpClientException, HttpClientResponse}
import org.hamcrest.core.IsEqual
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope
import play.api.libs.json.Json
import com.fortysevendeg.ninecardslauncher.commons.test.TaskServiceTestOps._
import monix.eval.Task
import cats.syntax.either._
import com.fortysevendeg.ninecardslauncher.commons.test.TaskServiceSpecification

trait ServiceClientSpecification
  extends TaskServiceSpecification
  with Mockito
  with ServiceClientData {

  trait ServiceClientScope
    extends Scope {

    val httpClient = mock[HttpClient]

    val mockResponse = mock[HttpClientResponse]

    val serviceClient = new ServiceClient(httpClient, baseUrl)

  }

  trait WithValidResponse {

    self: ServiceClientScope =>

    mockResponse.statusCode returns 200
    mockResponse.body returns Some(json)

  }

  trait WithValidEmptyResponse {

    self: ServiceClientScope =>

    mockResponse.statusCode returns 200
    mockResponse.body returns None

  }

  trait WithSuccessfullyHttpClientMock {

    self: ServiceClientScope =>

    mockResponse.statusCode returns 200

    mockResponse.body returns Some(json)

    httpClient.doGet(any, any) returns TaskService(Task(Either.right(mockResponse)))

    httpClient.doDelete(any, any) returns TaskService {
      Task(Either.right(mockResponse))
    }

    httpClient.doPost(any, any) returns TaskService {
      Task(Either.right(mockResponse))
    }

    httpClient.doPost[SampleRequest](any, any, any)(any) returns TaskService {
      Task(Either.right(mockResponse))
    }

    httpClient.doPut(any, any) returns TaskService {
      Task(Either.right(mockResponse))
    }

    httpClient.doPut[SampleRequest](any, any, any)(any) returns TaskService {
      Task(Either.right(mockResponse))
    }
  }

  trait WithFailedHttpClientMock {

    self: ServiceClientScope =>

    val exception = HttpClientException("")

    httpClient.doGet(any, any) returns TaskService {
      Task(Either.left(exception))
    }

    httpClient.doDelete(any, any) returns TaskService {
      Task(Either.left(exception))
    }

    httpClient.doPost(any, any) returns TaskService {
      Task(Either.left(exception))
    }

    httpClient.doPost[SampleRequest](any, any, any)(any) returns TaskService {
      Task(Either.left(exception))
    }

    httpClient.doPut(any, any) returns TaskService {
      Task(Either.left(exception))
    }

    httpClient.doPut[SampleRequest](any, any, any)(any) returns TaskService {
      Task(Either.left(exception))
    }
  }

}

case class Test(value: Int)

class ServiceClientSpec
  extends ServiceClientSpecification {

  "Service Client component" should {

    "return a valid response for a valid call to get with response" in
      new ServiceClientScope with WithValidResponse {

        httpClient.doGet(any, any) returns serviceRight(mockResponse)

        serviceClient.get(baseUrl, Seq.empty, Some(readsResponse)) mustRight {
          _.data shouldEqual sampleResponse
        }

        there was one(httpClient).doGet(any, any)
        there was noMoreCallsTo(httpClient)
      }

    "return a valid response for a valid call to get without response" in
      new ServiceClientScope with WithValidEmptyResponse {

        httpClient.doGet(any, any) returns serviceRight(mockResponse)

        serviceClient.get[Unit](baseUrl, Seq.empty, None, emptyResponse = true) mustRight (_.data must beNone)

        there was one(httpClient).doGet(any, any)
        there was noMoreCallsTo(httpClient)
      }

    "return a valid response for a valid call to delete with response" in
      new ServiceClientScope with WithValidResponse {

        httpClient.doDelete(any, any) returns serviceRight(mockResponse)

        serviceClient.delete[SampleResponse](baseUrl, Seq.empty, Some(readsResponse)) mustRight {
          _.data shouldEqual sampleResponse
        }

        there was one(httpClient).doDelete(any, any)
        there was noMoreCallsTo(httpClient)
      }

    "return a valid response for a valid call to post" in
      new ServiceClientScope with WithValidResponse {

        httpClient.doPost(any, any) returns serviceRight(mockResponse)

        serviceClient.emptyPost[SampleResponse](baseUrl, Seq.empty, Some(readsResponse)) mustRight {
          _.data shouldEqual sampleResponse
        }

        there was one(httpClient).doPost(any, any)
        there was noMoreCallsTo(httpClient)
      }

    "return a valid response for a valid call to post with valid arguments" in
      new ServiceClientScope with WithValidResponse {

        httpClient.doPost(any, any, any)(any) returns serviceRight(mockResponse)

        val request = SampleRequest("sample-request")
        serviceClient.post(baseUrl, Seq.empty, request, Some(readsResponse)) mustRight {
          _.data shouldEqual sampleResponse
        }

        there was one(httpClient).doPost[SampleRequest](any, any, ===(request))(any)
        there was noMoreCallsTo(httpClient)
      }

    "return a valid response for a valid call to put" in
      new ServiceClientScope with WithValidResponse {

        httpClient.doPut(any, any) returns serviceRight(mockResponse)

        serviceClient.emptyPut(baseUrl, Seq.empty, Some(readsResponse)) mustRight (_.data shouldEqual sampleResponse)

        there was one(httpClient).doPut(any, any)
        there was noMoreCallsTo(httpClient)
      }

    "return a valid response for a valid call to put with valid arguments" in
      new ServiceClientScope with WithValidResponse {

        httpClient.doPut(any, any, any)(any) returns serviceRight(mockResponse)

        val request = SampleRequest("sample-request")
        serviceClient.put(baseUrl, Seq.empty, request, Some(readsResponse)) mustRight {
          _.data shouldEqual sampleResponse
        }

        there was one(httpClient).doPut[SampleRequest](any, any, anArgThat(IsEqual.equalTo(request)))(any)
        there was noMoreCallsTo(httpClient)
      }

    "throws a ServiceClientException when no Reads found for the response type" in
      new ServiceClientScope {
        httpClient.doGet(any, any) returns serviceLeft(exception)
        serviceClient.get[Test](baseUrl, Seq.empty).mustLeft[ServiceClientException]
      }

    "return a HttpClientException response when the call to get method throw an exception" in
      new ServiceClientScope with WithFailedHttpClientMock {
        httpClient.doGet(any, any) returns serviceLeft(exception)
        serviceClient.get(baseUrl, Seq.empty, Some(readsResponse)).mustLeft[ServiceClientException]
      }

    "return a HttpClientException when the call to delete method throw an exception" in
      new ServiceClientScope with WithFailedHttpClientMock {
        httpClient.doDelete(any, any) returns serviceLeft(exception)
        serviceClient.delete(baseUrl, Seq.empty, Some(readsResponse)).mustLeft[ServiceClientException]
      }

    "return a HttpClientException when the call to post method throw an exception" in
      new ServiceClientScope with WithFailedHttpClientMock {
        httpClient.doPost(any, any) returns serviceLeft(exception)
        serviceClient.emptyPost(baseUrl, Seq.empty, Some(readsResponse)).mustLeft[ServiceClientException]
      }

    "return a HttpClientException when the call to put method throw an exception" in
      new ServiceClientScope with WithFailedHttpClientMock {
        httpClient.doPut(any, any) returns serviceLeft(exception)
        serviceClient.emptyPut(baseUrl, Seq.empty, Some(readsResponse)).mustLeft[ServiceClientException]
      }

  }

}
