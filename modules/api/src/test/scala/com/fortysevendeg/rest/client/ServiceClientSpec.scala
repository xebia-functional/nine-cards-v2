package com.fortysevendeg.rest.client

import com.fortysevendeg.BaseTestSupport
import org.specs2.mock.Mockito
import org.hamcrest.core.IsEqual
import org.specs2.mutable.Specification
import org.specs2.specification.Scope

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

trait WithSuccessfullyHttpClient
    extends ServiceClient
    with ClientSupport
    with Mockito
    with Scope {

    override val httpClient = mock[HttpClient]

    val sampleResponse = SampleResponse("Hello World!")

    httpClient.doGet[SampleResponse](any, any)(any) returns Future.successful(sampleResponse)

    httpClient.doDelete[SampleResponse](any, any)(any) returns Future.successful(sampleResponse)

    httpClient.doPost[SampleResponse](any, any)(any) returns Future.successful(sampleResponse)

    httpClient.doPost[SampleRequest, SampleResponse](any, any, any)(any, any) returns Future.successful(sampleResponse)

    httpClient.doPut[SampleResponse](any, any)(any) returns Future.successful(sampleResponse)

    httpClient.doPut[SampleRequest, SampleResponse](any, any, any)(any, any) returns Future.successful(sampleResponse)

}

trait WithFailedHttpClient
    extends ServiceClient
    with ClientSupport
    with Mockito
    with Scope {

    override val httpClient = mock[HttpClient]

    val exception = new IllegalArgumentException

    httpClient.doGet[SampleResponse](any, any)(any) returns Future.failed(exception)

    httpClient.doDelete[SampleResponse](any, any)(any) returns Future.failed(exception)

    httpClient.doPost[SampleResponse](any, any)(any) returns Future.failed(exception)

    httpClient.doPost[SampleRequest, SampleResponse](any, any, any)(any, any) returns Future.failed(exception)

    httpClient.doPut[SampleResponse](any, any)(any) returns Future.failed(exception)

    httpClient.doPut[SampleRequest, SampleResponse](any, any, any)(any, any) returns Future.failed(exception)

}

class ServiceClientSpec
    extends Specification
    with BaseTestSupport {

    "Service Client component" should {

        "returns a valid response for a valid call to get" in new WithSuccessfullyHttpClient {
            val response = Await.result(get[SampleResponse](baseUrl, Seq.empty), Duration.Inf)
            there was one(httpClient).doGet[SampleResponse](any, any)(any)
            there was noMoreCallsTo(httpClient)
            response shouldEqual sampleResponse
        }

        "returns a valid response for a valid call to delete" in new WithSuccessfullyHttpClient {
            val response = Await.result(delete[SampleResponse](baseUrl, Seq.empty), Duration.Inf)
            there was one(httpClient).doDelete[SampleResponse](any, any)(any)
            there was noMoreCallsTo(httpClient)
            response shouldEqual sampleResponse
        }

        "returns a valid response for a valid call to post" in new WithSuccessfullyHttpClient {
            val response = Await.result(emptyPost[SampleResponse](baseUrl, Seq.empty), Duration.Inf)
            there was one(httpClient).doPost[SampleResponse](any, any)(any)
            there was noMoreCallsTo(httpClient)
            response shouldEqual sampleResponse
        }

        "returns a valid response for a valid call to post with valid arguments" in new WithSuccessfullyHttpClient {
            val request = SampleRequest("sample-request")
            val response = Await.result(post[SampleRequest, SampleResponse](baseUrl, Seq.empty, request), Duration.Inf)
            there was one(httpClient).doPost[SampleRequest, SampleResponse](any, any, anArgThat(IsEqual.equalTo(request)))(any, any)
            there was noMoreCallsTo(httpClient)
            response shouldEqual sampleResponse
        }

        "returns a valid response for a valid call to put" in new WithSuccessfullyHttpClient {
            val response = Await.result(emptyPut[SampleResponse](baseUrl, Seq.empty), Duration.Inf)
            there was one(httpClient).doPut[SampleResponse](any, any)(any)
            there was noMoreCallsTo(httpClient)
            response shouldEqual sampleResponse
        }

        "returns a valid response for a valid call to put with valid arguments" in new WithSuccessfullyHttpClient {
            val request = SampleRequest("sample-request")
            val response = Await.result(put[SampleRequest, SampleResponse](baseUrl, Seq.empty, request), Duration.Inf)
            there was one(httpClient).doPut[SampleRequest, SampleResponse](any, any, anArgThat(IsEqual.equalTo(request)))(any, any)
            there was noMoreCallsTo(httpClient)
            response shouldEqual sampleResponse
        }

        "returns a failed response when the call to get method throw an exception" in new WithFailedHttpClient {
            Await.result(get[SampleResponse](baseUrl, Seq.empty), Duration.Inf) must throwA[IllegalArgumentException]
        }

        "returns a failed response when the call to delete method throw an exception" in new WithFailedHttpClient {
            Await.result(delete[SampleResponse](baseUrl, Seq.empty), Duration.Inf) must throwA[IllegalArgumentException]
        }

        "returns a failed response when the call to post method throw an exception" in new WithFailedHttpClient {
            Await.result(emptyPost[SampleResponse](baseUrl, Seq.empty), Duration.Inf) must throwA[IllegalArgumentException]
        }

        "returns a failed response when the call to put method throw an exception" in new WithFailedHttpClient {
            Await.result(emptyPut[SampleResponse](baseUrl, Seq.empty), Duration.Inf) must throwA[IllegalArgumentException]
        }

    }

}
