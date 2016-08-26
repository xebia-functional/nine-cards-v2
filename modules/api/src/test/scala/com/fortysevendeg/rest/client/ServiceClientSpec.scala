package com.fortysevendeg.rest.client

import cats.data.Xor
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService
import com.fortysevendeg.rest.client.http.{HttpClient, HttpClientException, HttpClientResponse}
import org.hamcrest.core.IsEqual
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope
import play.api.libs.json.Json

import scalaz.concurrent.Task

trait ServiceClientSpecification
  extends Specification
    with Mockito {

  trait ServiceClientScope
    extends Scope {

    val baseUrl = "http://sampleUrl"

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

    httpClient.doGet(any, any) returns TaskService {
      Task(Xor.right(mockResponse))
    }

    httpClient.doDelete(any, any) returns TaskService {
      Task(Xor.right(mockResponse))
    }

    httpClient.doPost(any, any) returns TaskService {
      Task(Xor.right(mockResponse))
    }

    httpClient.doPost[SampleRequest](any, any, any)(any) returns TaskService {
      Task(Xor.right(mockResponse))
    }

    httpClient.doPut(any, any) returns TaskService {
      Task(Xor.right(mockResponse))
    }

    httpClient.doPut[SampleRequest](any, any, any)(any) returns TaskService {
      Task(Xor.right(mockResponse))
    }
  }

  trait WithFailedHttpClientMock {

    self: ServiceClientScope =>

    val exception = HttpClientException("")

    httpClient.doGet(any, any) returns TaskService {
      Task(Xor.left(exception))
    }

    httpClient.doDelete(any, any) returns TaskService {
      Task(Xor.left(exception))
    }

    httpClient.doPost(any, any) returns TaskService {
      Task(Xor.left(exception))
    }

    httpClient.doPost[SampleRequest](any, any, any)(any) returns TaskService {
      Task(Xor.left(exception))
    }

    httpClient.doPut(any, any) returns TaskService {
      Task(Xor.left(exception))
    }

    httpClient.doPut[SampleRequest](any, any, any)(any) returns TaskService {
      Task(Xor.left(exception))
    }
  }

}

case class Test(value: Int)

class ServiceClientSpec
  extends ServiceClientSpecification {

  "Service Client component" should {

    "return a valid response for a valid call to get with response" in
      new ServiceClientScope with WithSuccessfullyHttpClientMock {
        val response = serviceClient.get[SampleResponse](baseUrl, Seq.empty, Some(readsResponse)).value.run
        there was one(httpClient).doGet(any, any)
        there was noMoreCallsTo(httpClient)
        response must beLike {
          case Xor.Right(r) => r.data shouldEqual sampleResponse
        }
      }

    "return a valid response for a valid call to get without response" in
      new ServiceClientScope with WithSuccessfullyHttpClientMock {
        val response = serviceClient.get(baseUrl, Seq.empty, None, emptyResponse = true).value.run
        there was one(httpClient).doGet(any, any)
        there was noMoreCallsTo(httpClient)
        response must beLike {
          case Xor.Right(r) => r.data must beNone
        }
      }

    "return a valid response for a valid call to delete with response" in
      new ServiceClientScope with WithSuccessfullyHttpClientMock {
        val response = serviceClient.delete[SampleResponse](baseUrl, Seq.empty, Some(readsResponse)).value.run
        there was one(httpClient).doDelete(any, any)
        there was noMoreCallsTo(httpClient)
        response must beLike {
          case Xor.Right(r) => r.data shouldEqual sampleResponse
        }
      }

    "return a valid response for a valid call to post" in
      new ServiceClientScope with WithSuccessfullyHttpClientMock {
        val response = serviceClient.emptyPost[SampleResponse](baseUrl, Seq.empty, Some(readsResponse)).value.run
        there was one(httpClient).doPost(any, any)
        there was noMoreCallsTo(httpClient)
        response must beLike {
          case Xor.Right(r) => r.data shouldEqual sampleResponse
        }
      }

    "return a valid response for a valid call to post with valid arguments" in
      new ServiceClientScope with WithSuccessfullyHttpClientMock {
        val request = SampleRequest("sample-request")
        val response = serviceClient.post[SampleRequest, SampleResponse](baseUrl, Seq.empty, request, Some(readsResponse)).value.run
        there was one(httpClient).doPost[SampleRequest](any, any, anArgThat(IsEqual.equalTo(request)))(any)
        there was noMoreCallsTo(httpClient)
        response must beLike {
          case Xor.Right(r) => r.data shouldEqual sampleResponse
        }
      }

    "return a valid response for a valid call to put" in
      new ServiceClientScope with WithSuccessfullyHttpClientMock {
        val response = serviceClient.emptyPut[SampleResponse](baseUrl, Seq.empty, Some(readsResponse)).value.run
        there was one(httpClient).doPut(any, any)
        there was noMoreCallsTo(httpClient)
        response must beLike {
          case Xor.Right(r) => r.data shouldEqual sampleResponse
        }
      }

    "return a valid response for a valid call to put with valid arguments" in
      new ServiceClientScope with WithSuccessfullyHttpClientMock {
        val request = SampleRequest("sample-request")
        val response = serviceClient.put[SampleRequest, SampleResponse](baseUrl, Seq.empty, request, Some(readsResponse)).value.run
        there was one(httpClient).doPut[SampleRequest](any, any, anArgThat(IsEqual.equalTo(request)))(any)
        there was noMoreCallsTo(httpClient)
        response must beLike {
          case Xor.Right(r) => r.data shouldEqual sampleResponse
        }
      }

    "throws a ServiceClientException when no Reads found for the response type" in
      new ServiceClientScope with WithSuccessfullyHttpClientMock {
        val response = serviceClient.get[Test](baseUrl, Seq.empty).value.run
        response must beLike {
          case Xor.Left(e) => e must beAnInstanceOf[ServiceClientException]
          }
      }

    "return a HttpClientException response when the call to get method throw an exception" in
      new ServiceClientScope with WithFailedHttpClientMock {
        val response = serviceClient.get[SampleResponse](baseUrl, Seq.empty, Some(readsResponse)).value.run
        response must beLike {
          case Xor.Left(e) =>  e shouldEqual exception
          }
      }

    "return a HttpClientException when the call to delete method throw an exception" in
      new ServiceClientScope with WithFailedHttpClientMock {
        val response = serviceClient.delete[SampleResponse](baseUrl, Seq.empty, Some(readsResponse)).value.run
        response must beLike {
          case Xor.Left(e) =>  e shouldEqual exception
          }
      }

    "return a HttpClientException when the call to post method throw an exception" in
      new ServiceClientScope with WithFailedHttpClientMock {
        val response = serviceClient.emptyPost[SampleResponse](baseUrl, Seq.empty, Some(readsResponse)).value.run
        response must beLike {
          case Xor.Left(e) =>  e shouldEqual exception
          }
      }

    "return a HttpClientException when the call to put method throw an exception" in
      new ServiceClientScope with WithFailedHttpClientMock {
        val response = serviceClient.emptyPut[SampleResponse](baseUrl, Seq.empty, Some(readsResponse)).value.run
        response must beLike {
          case Xor.Left(e) => e shouldEqual exception
        }
      }

  }

}
