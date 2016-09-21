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

    val serviceClient = new ServiceClient(httpClient, baseUrl)

  }

}

case class Test(value: Int)

class ServiceClientSpec
  extends ServiceClientSpecification {

  "get method from ServiceClient" should {

    "return a valid response when service return a valid response" in
      new ServiceClientScope {

        httpClient.doGet(any, any) returns serviceRight(validHttpClientResponse)

        serviceClient.get(path, headers, Some(readsResponse), emptyResponse = false) mustRight { r =>
          r.statusCode shouldEqual statusCodeOk
          r.data shouldEqual sampleResponse
        }

        there was one(httpClient).doGet(s"$baseUrl$path", headers)
        there was noMoreCallsTo(httpClient)
      }

    "return None when the service return a valid empty response" in
      new ServiceClientScope {

        httpClient.doGet(any, any) returns serviceRight(validHttpClientResponse)

        serviceClient.get[Unit](path, headers, None, emptyResponse = true) mustRight { r =>
          r.statusCode shouldEqual statusCodeOk
          r.data must beNone
        }

        there was one(httpClient).doGet(s"$baseUrl$path", headers)
        there was noMoreCallsTo(httpClient)
      }

    "return ServiceClientException when the service return an exception" in
      new ServiceClientScope {

        httpClient.doGet(any, any) returns serviceLeft(exception)

        serviceClient.get(path, headers, Some(readsResponse), emptyResponse = false).mustLeft[ServiceClientException]

        there was one(httpClient).doGet(s"$baseUrl$path", headers)
        there was noMoreCallsTo(httpClient)
      }

    "return ServiceClientException when the service return a status code of 'Not Found'" in
      new ServiceClientScope {

        httpClient.doGet(any, any) returns serviceRight(notFoundHttpClientResponse)

        serviceClient.get(path, headers, Some(readsResponse), emptyResponse = false).mustLeft[ServiceClientException]

        there was one(httpClient).doGet(s"$baseUrl$path", headers)
        there was noMoreCallsTo(httpClient)
      }

    "return ServiceClientException when the service return a invalid JSON in the response" in
      new ServiceClientScope {

        httpClient.doGet(any, any) returns serviceRight(invalidHttpClientResponse)

        serviceClient.get(path, headers, Some(readsResponse), emptyResponse = false).mustLeft[ServiceClientException]

        there was one(httpClient).doGet(s"$baseUrl$path", headers)
        there was noMoreCallsTo(httpClient)
      }

    "return ServiceClientException when the service return an empty response but we're expecting some" in
      new ServiceClientScope {

        httpClient.doGet(any, any) returns serviceRight(validEmptyHttpClientResponse)

        serviceClient.get(path, headers, Some(readsResponse), emptyResponse = false).mustLeft[ServiceClientException]

        there was one(httpClient).doGet(s"$baseUrl$path", headers)
        there was noMoreCallsTo(httpClient)
      }

    "return ServiceClientException when the service return a valid response but the JSON reads is not provided'" in
      new ServiceClientScope {

        httpClient.doGet(any, any) returns serviceRight(validHttpClientResponse)

        serviceClient.get(path, headers, None, emptyResponse = false).mustLeft[ServiceClientException]

        there was one(httpClient).doGet(s"$baseUrl$path", headers)
        there was noMoreCallsTo(httpClient)
      }
  }

  "delete method from ServiceClient" should {

    "return a valid response when service return a valid response" in
      new ServiceClientScope {

        httpClient.doDelete(any, any) returns serviceRight(validHttpClientResponse)

        serviceClient.delete(path, headers, Some(readsResponse), emptyResponse = false) mustRight { r =>
          r.statusCode shouldEqual statusCodeOk
          r.data shouldEqual sampleResponse
        }

        there was one(httpClient).doDelete(s"$baseUrl$path", headers)
        there was noMoreCallsTo(httpClient)
      }

    "return None when the service return a valid empty response" in
      new ServiceClientScope {

        httpClient.doDelete(any, any) returns serviceRight(validHttpClientResponse)

        serviceClient.delete[Unit](path, headers, None, emptyResponse = true) mustRight { r =>
          r.statusCode shouldEqual statusCodeOk
          r.data must beNone
        }

        there was one(httpClient).doDelete(s"$baseUrl$path", headers)
        there was noMoreCallsTo(httpClient)
      }

    "return ServiceClientException when the service return an exception" in
      new ServiceClientScope {

        httpClient.doDelete(any, any) returns serviceLeft(exception)

        serviceClient.delete(path, headers, Some(readsResponse), emptyResponse = false).mustLeft[ServiceClientException]

        there was one(httpClient).doDelete(s"$baseUrl$path", headers)
        there was noMoreCallsTo(httpClient)
      }

    "return ServiceClientException when the service return a status code of 'Not Found'" in
      new ServiceClientScope {

        httpClient.doDelete(any, any) returns serviceRight(notFoundHttpClientResponse)

        serviceClient.delete(path, headers, Some(readsResponse), emptyResponse = false).mustLeft[ServiceClientException]

        there was one(httpClient).doDelete(s"$baseUrl$path", headers)
        there was noMoreCallsTo(httpClient)
      }

    "return ServiceClientException when the service return a invalid JSON in the response" in
      new ServiceClientScope {

        httpClient.doDelete(any, any) returns serviceRight(invalidHttpClientResponse)

        serviceClient.delete(path, headers, Some(readsResponse), emptyResponse = false).mustLeft[ServiceClientException]

        there was one(httpClient).doDelete(s"$baseUrl$path", headers)
        there was noMoreCallsTo(httpClient)
      }

    "return ServiceClientException when the service return an empty response but we're expecting some" in
      new ServiceClientScope {

        httpClient.doDelete(any, any) returns serviceRight(validEmptyHttpClientResponse)

        serviceClient.delete(path, headers, Some(readsResponse), emptyResponse = false).mustLeft[ServiceClientException]

        there was one(httpClient).doDelete(s"$baseUrl$path", headers)
        there was noMoreCallsTo(httpClient)
      }

    "return ServiceClientException when the service return a valid response but the JSON reads is not provided'" in
      new ServiceClientScope {

        httpClient.doDelete(any, any) returns serviceRight(validHttpClientResponse)

        serviceClient.delete(path, headers, None, emptyResponse = false).mustLeft[ServiceClientException]

        there was one(httpClient).doDelete(s"$baseUrl$path", headers)
        there was noMoreCallsTo(httpClient)
      }
  }

  "emptyPost method from ServiceClient" should {

    "return a valid response when service return a valid response" in
      new ServiceClientScope {

        httpClient.doPost(any, any) returns serviceRight(validHttpClientResponse)

        serviceClient.emptyPost(path, headers, Some(readsResponse), emptyResponse = false) mustRight { r =>
          r.statusCode shouldEqual statusCodeOk
          r.data shouldEqual sampleResponse
        }

        there was one(httpClient).doPost(s"$baseUrl$path", headers)
        there was noMoreCallsTo(httpClient)
      }

    "return None when the service return a valid empty response" in
      new ServiceClientScope {

        httpClient.doPost(any, any) returns serviceRight(validHttpClientResponse)

        serviceClient.emptyPost[Unit](path, headers, None, emptyResponse = true) mustRight { r =>
          r.statusCode shouldEqual statusCodeOk
          r.data must beNone
        }

        there was one(httpClient).doPost(s"$baseUrl$path", headers)
        there was noMoreCallsTo(httpClient)
      }

    "return ServiceClientException when the service return an exception" in
      new ServiceClientScope {

        httpClient.doPost(any, any) returns serviceLeft(exception)

        serviceClient.emptyPost(path, headers, Some(readsResponse), emptyResponse = false)
          .mustLeft[ServiceClientException]

        there was one(httpClient).doPost(s"$baseUrl$path", headers)
        there was noMoreCallsTo(httpClient)
      }

    "return ServiceClientException when the service return a status code of 'Not Found'" in
      new ServiceClientScope {

        httpClient.doPost(any, any) returns serviceRight(notFoundHttpClientResponse)

        serviceClient.emptyPost(path, headers, Some(readsResponse), emptyResponse = false)
          .mustLeft[ServiceClientException]

        there was one(httpClient).doPost(s"$baseUrl$path", headers)
        there was noMoreCallsTo(httpClient)
      }

    "return ServiceClientException when the service return a invalid JSON in the response" in
      new ServiceClientScope {

        httpClient.doPost(any, any) returns serviceRight(invalidHttpClientResponse)

        serviceClient.emptyPost(path, headers, Some(readsResponse), emptyResponse = false)
          .mustLeft[ServiceClientException]

        there was one(httpClient).doPost(s"$baseUrl$path", headers)
        there was noMoreCallsTo(httpClient)
      }

    "return ServiceClientException when the service return an empty response but we're expecting some" in
      new ServiceClientScope {

        httpClient.doPost(any, any) returns serviceRight(validEmptyHttpClientResponse)

        serviceClient.emptyPost(path, headers, Some(readsResponse), emptyResponse = false)
          .mustLeft[ServiceClientException]

        there was one(httpClient).doPost(s"$baseUrl$path", headers)
        there was noMoreCallsTo(httpClient)
      }

    "return ServiceClientException when the service return a valid response but the JSON reads is not provided'" in
      new ServiceClientScope {

        httpClient.doPost(any, any) returns serviceRight(validHttpClientResponse)

        serviceClient.emptyPost(path, headers, None, emptyResponse = false)
          .mustLeft[ServiceClientException]

        there was one(httpClient).doPost(s"$baseUrl$path", headers)
        there was noMoreCallsTo(httpClient)
      }
  }

  "post method from ServiceClient" should {

    "return a valid response when service return a valid response" in
      new ServiceClientScope {

        httpClient.doPost(any, any, any)(any) returns serviceRight(validHttpClientResponse)

        serviceClient.post(path, headers, sampleRequest, Some(readsResponse), emptyResponse = false) mustRight { r =>
          r.statusCode shouldEqual statusCodeOk
          r.data shouldEqual sampleResponse
        }

        there was one(httpClient).doPost(s"$baseUrl$path", headers, sampleRequest)(writesRequest)
        there was noMoreCallsTo(httpClient)
      }

    "return None when the service return a valid empty response" in
      new ServiceClientScope {

        httpClient.doPost(any, any, any)(any) returns serviceRight(validHttpClientResponse)

        serviceClient.post[SampleRequest, Unit](path, headers, sampleRequest, None, emptyResponse = true) mustRight { r =>
          r.statusCode shouldEqual statusCodeOk
          r.data must beNone
        }

        there was one(httpClient).doPost(s"$baseUrl$path", headers, sampleRequest)(writesRequest)
        there was noMoreCallsTo(httpClient)
      }

    "return ServiceClientException when the service return an exception" in
      new ServiceClientScope {

        httpClient.doPost(any, any, any)(any) returns serviceLeft(exception)

        serviceClient.post(path, headers, sampleRequest, Some(readsResponse), emptyResponse = false)
          .mustLeft[ServiceClientException]

        there was one(httpClient).doPost(s"$baseUrl$path", headers, sampleRequest)(writesRequest)
        there was noMoreCallsTo(httpClient)
      }

    "return ServiceClientException when the service return a status code of 'Not Found'" in
      new ServiceClientScope {

        httpClient.doPost(any, any, any)(any) returns serviceRight(notFoundHttpClientResponse)

        serviceClient.post(path, headers, sampleRequest, Some(readsResponse), emptyResponse = false)
          .mustLeft[ServiceClientException]

        there was one(httpClient).doPost(s"$baseUrl$path", headers, sampleRequest)(writesRequest)
        there was noMoreCallsTo(httpClient)
      }

    "return ServiceClientException when the service return a invalid JSON in the response" in
      new ServiceClientScope {

        httpClient.doPost(any, any, any)(any) returns serviceRight(invalidHttpClientResponse)

        serviceClient.post(path, headers, sampleRequest, Some(readsResponse), emptyResponse = false)
          .mustLeft[ServiceClientException]

        there was one(httpClient).doPost(s"$baseUrl$path", headers, sampleRequest)(writesRequest)
        there was noMoreCallsTo(httpClient)
      }

    "return ServiceClientException when the service return an empty response but we're expecting some" in
      new ServiceClientScope {

        httpClient.doPost(any, any, any)(any) returns serviceRight(validEmptyHttpClientResponse)

        serviceClient.post(path, headers, sampleRequest, Some(readsResponse), emptyResponse = false)
          .mustLeft[ServiceClientException]

        there was one(httpClient).doPost(s"$baseUrl$path", headers, sampleRequest)(writesRequest)
        there was noMoreCallsTo(httpClient)
      }

    "return ServiceClientException when the service return a valid response but the JSON reads is not provided'" in
      new ServiceClientScope {

        httpClient.doPost(any, any, any)(any) returns serviceRight(validHttpClientResponse)

        serviceClient.post(path, headers, sampleRequest, None, emptyResponse = false)
          .mustLeft[ServiceClientException]

        there was one(httpClient).doPost(s"$baseUrl$path", headers, sampleRequest)(writesRequest)
        there was noMoreCallsTo(httpClient)
      }
  }

  "emptyPut method from ServiceClient" should {

    "return a valid response when service return a valid response" in
      new ServiceClientScope {

        httpClient.doPut(any, any) returns serviceRight(validHttpClientResponse)

        serviceClient.emptyPut(path, headers, Some(readsResponse), emptyResponse = false) mustRight { r =>
          r.statusCode shouldEqual statusCodeOk
          r.data shouldEqual sampleResponse
        }

        there was one(httpClient).doPut(s"$baseUrl$path", headers)
        there was noMoreCallsTo(httpClient)
      }

    "return None when the service return a valid empty response" in
      new ServiceClientScope {

        httpClient.doPut(any, any) returns serviceRight(validHttpClientResponse)

        serviceClient.emptyPut[Unit](path, headers, None, emptyResponse = true) mustRight { r =>
          r.statusCode shouldEqual statusCodeOk
          r.data must beNone
        }

        there was one(httpClient).doPut(s"$baseUrl$path", headers)
        there was noMoreCallsTo(httpClient)
      }

    "return ServiceClientException when the service return an exception" in
      new ServiceClientScope {

        httpClient.doPut(any, any) returns serviceLeft(exception)

        serviceClient.emptyPut(path, headers, Some(readsResponse), emptyResponse = false)
          .mustLeft[ServiceClientException]

        there was one(httpClient).doPut(s"$baseUrl$path", headers)
        there was noMoreCallsTo(httpClient)
      }

    "return ServiceClientException when the service return a status code of 'Not Found'" in
      new ServiceClientScope {

        httpClient.doPut(any, any) returns serviceRight(notFoundHttpClientResponse)

        serviceClient.emptyPut(path, headers, Some(readsResponse), emptyResponse = false)
          .mustLeft[ServiceClientException]

        there was one(httpClient).doPut(s"$baseUrl$path", headers)
        there was noMoreCallsTo(httpClient)
      }

    "return ServiceClientException when the service return a invalid JSON in the response" in
      new ServiceClientScope {

        httpClient.doPut(any, any) returns serviceRight(invalidHttpClientResponse)

        serviceClient.emptyPut(path, headers, Some(readsResponse), emptyResponse = false)
          .mustLeft[ServiceClientException]

        there was one(httpClient).doPut(s"$baseUrl$path", headers)
        there was noMoreCallsTo(httpClient)
      }

    "return ServiceClientException when the service return an empty response but we're expecting some" in
      new ServiceClientScope {

        httpClient.doPut(any, any) returns serviceRight(validEmptyHttpClientResponse)

        serviceClient.emptyPut(path, headers, Some(readsResponse), emptyResponse = false)
          .mustLeft[ServiceClientException]

        there was one(httpClient).doPut(s"$baseUrl$path", headers)
        there was noMoreCallsTo(httpClient)
      }

    "return ServiceClientException when the service return a valid response but the JSON reads is not provided'" in
      new ServiceClientScope {

        httpClient.doPut(any, any) returns serviceRight(validHttpClientResponse)

        serviceClient.emptyPut(path, headers, None, emptyResponse = false)
          .mustLeft[ServiceClientException]

        there was one(httpClient).doPut(s"$baseUrl$path", headers)
        there was noMoreCallsTo(httpClient)
      }
  }

  "put method from ServiceClient" should {

    "return a valid response when service return a valid response" in
      new ServiceClientScope {

        httpClient.doPut(any, any, any)(any) returns serviceRight(validHttpClientResponse)

        serviceClient.put(path, headers, sampleRequest, Some(readsResponse), emptyResponse = false) mustRight { r =>
          r.statusCode shouldEqual statusCodeOk
          r.data shouldEqual sampleResponse
        }

        there was one(httpClient).doPut(s"$baseUrl$path", headers, sampleRequest)(writesRequest)
        there was noMoreCallsTo(httpClient)
      }

    "return None when the service return a valid empty response" in
      new ServiceClientScope {

        httpClient.doPut(any, any, any)(any) returns serviceRight(validHttpClientResponse)

        serviceClient.put[SampleRequest, Unit](path, headers, sampleRequest, None, emptyResponse = true) mustRight { r =>
          r.statusCode shouldEqual statusCodeOk
          r.data must beNone
        }

        there was one(httpClient).doPut(s"$baseUrl$path", headers, sampleRequest)(writesRequest)
        there was noMoreCallsTo(httpClient)
      }

    "return ServiceClientException when the service return an exception" in
      new ServiceClientScope {

        httpClient.doPut(any, any, any)(any) returns serviceLeft(exception)

        serviceClient.put(path, headers, sampleRequest, Some(readsResponse), emptyResponse = false)
          .mustLeft[ServiceClientException]

        there was one(httpClient).doPut(s"$baseUrl$path", headers, sampleRequest)(writesRequest)
        there was noMoreCallsTo(httpClient)
      }

    "return ServiceClientException when the service return a status code of 'Not Found'" in
      new ServiceClientScope {

        httpClient.doPut(any, any, any)(any) returns serviceRight(notFoundHttpClientResponse)

        serviceClient.put(path, headers, sampleRequest, Some(readsResponse), emptyResponse = false)
          .mustLeft[ServiceClientException]

        there was one(httpClient).doPut(s"$baseUrl$path", headers, sampleRequest)(writesRequest)
        there was noMoreCallsTo(httpClient)
      }

    "return ServiceClientException when the service return a invalid JSON in the response" in
      new ServiceClientScope {

        httpClient.doPut(any, any, any)(any) returns serviceRight(invalidHttpClientResponse)

        serviceClient.put(path, headers, sampleRequest, Some(readsResponse), emptyResponse = false)
          .mustLeft[ServiceClientException]

        there was one(httpClient).doPut(s"$baseUrl$path", headers, sampleRequest)(writesRequest)
        there was noMoreCallsTo(httpClient)
      }

    "return ServiceClientException when the service return an empty response but we're expecting some" in
      new ServiceClientScope {

        httpClient.doPut(any, any, any)(any) returns serviceRight(validEmptyHttpClientResponse)

        serviceClient.put(path, headers, sampleRequest, Some(readsResponse), emptyResponse = false)
          .mustLeft[ServiceClientException]

        there was one(httpClient).doPut(s"$baseUrl$path", headers, sampleRequest)(writesRequest)
        there was noMoreCallsTo(httpClient)
      }

    "return ServiceClientException when the service return a valid response but the JSON reads is not provided'" in
      new ServiceClientScope {

        httpClient.doPut(any, any, any)(any) returns serviceRight(validHttpClientResponse)

        serviceClient.put(path, headers, sampleRequest, None, emptyResponse = false)
          .mustLeft[ServiceClientException]

        there was one(httpClient).doPut(s"$baseUrl$path", headers, sampleRequest)(writesRequest)
        there was noMoreCallsTo(httpClient)
      }
  }

}
