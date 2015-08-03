package com.fortysevendeg.rest.client

import com.fortysevendeg.ninecardslauncher.commons.services.Service
import com.fortysevendeg.rest.client.http.{HttpClient, HttpClientExceptionImpl, HttpClientResponse}
import org.hamcrest.core.IsEqual
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope
import play.api.libs.json.Json
import rapture.core.{Answer, Errata}

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

    httpClient.doGet(any, any) returns Service { Task(Answer(mockResponse)) }

    httpClient.doDelete(any, any) returns Service { Task(Answer(mockResponse)) }

    httpClient.doPost(any, any) returns Service { Task(Answer(mockResponse)) }

    httpClient.doPost[SampleRequest](any, any, any)(any) returns Service { Task(Answer(mockResponse)) }

    httpClient.doPut(any, any) returns Service { Task(Answer(mockResponse)) }

    httpClient.doPut[SampleRequest](any, any, any)(any) returns Service { Task(Answer(mockResponse)) }
  }

  trait WithFailedHttpClientMock {

    self: ServiceClientScope =>

    val exception = HttpClientExceptionImpl("")

    httpClient.doGet(any, any) returns Service { Task(Errata(exception)) }

    httpClient.doDelete(any, any) returns Service { Task(Errata(exception)) }

    httpClient.doPost(any, any) returns Service { Task(Errata(exception)) }

    httpClient.doPost[SampleRequest](any, any, any)(any) returns Service { Task(Errata(exception)) }

    httpClient.doPut(any, any) returns Service { Task(Errata(exception)) }

    httpClient.doPut[SampleRequest](any, any, any)(any) returns Service { Task(Errata(exception)) }
  }

}

case class Test(value: Int)

class ServiceClientSpec
    extends ServiceClientSpecification {

  "Service Client component" should {

    "return a valid response for a valid call to get with response" in
        new ServiceClientScope with WithSuccessfullyHttpClientMock {
          val response = serviceClient.get[SampleResponse](baseUrl, Seq.empty, Some(readsResponse)).run.run
          there was one(httpClient).doGet(any, any)
          there was noMoreCallsTo(httpClient)
          response must beLike {
            case Answer(r) => r.data shouldEqual sampleResponse
          }
        }

    "return a valid response for a valid call to get without response" in
        new ServiceClientScope with WithSuccessfullyHttpClientMock {
          val response = serviceClient.get(baseUrl, Seq.empty, None, emptyResponse = true).run.run
          there was one(httpClient).doGet(any, any)
          there was noMoreCallsTo(httpClient)
          response must beLike {
            case Answer(r) => r.data must beNone
          }
        }

    "return a valid response for a valid call to delete with response" in
        new ServiceClientScope with WithSuccessfullyHttpClientMock {
          val response = serviceClient.delete[SampleResponse](baseUrl, Seq.empty, Some(readsResponse)).run.run
          there was one(httpClient).doDelete(any, any)
          there was noMoreCallsTo(httpClient)
          response must beLike {
            case Answer(r) => r.data shouldEqual sampleResponse
          }
        }

    "return a valid response for a valid call to post" in
        new ServiceClientScope with WithSuccessfullyHttpClientMock {
          val response = serviceClient.emptyPost[SampleResponse](baseUrl, Seq.empty, Some(readsResponse)).run.run
          there was one(httpClient).doPost(any, any)
          there was noMoreCallsTo(httpClient)
          response must beLike {
            case Answer(r) => r.data shouldEqual sampleResponse
          }
        }

    "return a valid response for a valid call to post with valid arguments" in
        new ServiceClientScope with WithSuccessfullyHttpClientMock {
          val request = SampleRequest("sample-request")
          val response = serviceClient.post[SampleRequest, SampleResponse](baseUrl, Seq.empty, request, Some(readsResponse)).run.run
          there was one(httpClient).doPost[SampleRequest](any, any, anArgThat(IsEqual.equalTo(request)))(any)
          there was noMoreCallsTo(httpClient)
          response must beLike {
            case Answer(r) => r.data shouldEqual sampleResponse
          }
        }

    "return a valid response for a valid call to put" in
        new ServiceClientScope with WithSuccessfullyHttpClientMock {
          val response = serviceClient.emptyPut[SampleResponse](baseUrl, Seq.empty, Some(readsResponse)).run.run
          there was one(httpClient).doPut(any, any)
          there was noMoreCallsTo(httpClient)
          response must beLike {
            case Answer(r) => r.data shouldEqual sampleResponse
          }
        }

    "return a valid response for a valid call to put with valid arguments" in
        new ServiceClientScope with WithSuccessfullyHttpClientMock {
          val request = SampleRequest("sample-request")
          val response = serviceClient.put[SampleRequest, SampleResponse](baseUrl, Seq.empty, request, Some(readsResponse)).run.run
          there was one(httpClient).doPut[SampleRequest](any, any, anArgThat(IsEqual.equalTo(request)))(any)
          there was noMoreCallsTo(httpClient)
          response must beLike {
            case Answer(r) => r.data shouldEqual sampleResponse
          }
        }

    "throws a ServiceClientException when no Reads found for the response type" in
        new ServiceClientScope with WithSuccessfullyHttpClientMock {
          val response = serviceClient.get[Test](baseUrl, Seq.empty).run.run
          response must beLike {
            case Errata(t) => t.headOption must beSome.which {
              case (_, (_, e)) => e must beAnInstanceOf[ServiceClientException]
            }
          }
        }

    "return a HttpClientException response when the call to get method throw an exception" in
        new ServiceClientScope with WithFailedHttpClientMock {
          val response = serviceClient.get[SampleResponse](baseUrl, Seq.empty, Some(readsResponse)).run.run
          response must beLike {
            case Errata(t) => t.headOption must beSome.which {
              case (_, (_, e)) => e shouldEqual exception
            }
          }
        }

    "return a HttpClientException when the call to delete method throw an exception" in
        new ServiceClientScope with WithFailedHttpClientMock {
          val response = serviceClient.delete[SampleResponse](baseUrl, Seq.empty, Some(readsResponse)).run.run
          response must beLike {
            case Errata(t) => t.headOption must beSome.which {
              case (_, (_, e)) => e shouldEqual exception
            }
          }
        }

    "return a HttpClientException when the call to post method throw an exception" in
        new ServiceClientScope with WithFailedHttpClientMock {
          val response = serviceClient.emptyPost[SampleResponse](baseUrl, Seq.empty, Some(readsResponse)).run.run
          response must beLike {
            case Errata(t) => t.headOption must beSome.which {
              case (_, (_, e)) => e shouldEqual exception
            }
          }
        }

    "return a HttpClientException when the call to put method throw an exception" in
        new ServiceClientScope with WithFailedHttpClientMock {
          val response = serviceClient.emptyPut[SampleResponse](baseUrl, Seq.empty, Some(readsResponse)).run.run
          response must beLike {
            case Errata(t) => t.headOption must beSome.which {
              case (_, (_, e)) => e shouldEqual exception
            }
          }
        }

  }

}
