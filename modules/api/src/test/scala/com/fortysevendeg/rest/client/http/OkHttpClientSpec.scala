package com.fortysevendeg.rest.client.http

import cards.nine.commons.test.TaskServiceSpecification
import com.fortysevendeg.rest.client.SampleRequest
import okhttp3.Request
import org.specs2.matcher.DisjunctionMatchers
import org.specs2.mock.Mockito
import org.specs2.specification.Scope
import play.api.libs.json.Json

trait OkHttpClientSpecification
  extends TaskServiceSpecification
  with DisjunctionMatchers
  with Mockito {

  trait OkHttpClientScope
    extends Scope {

    val baseUrl = "http://sampleUrl"

    implicit val readsRequest = Json.reads[SampleRequest]
    implicit val writesRequest = Json.writes[SampleRequest]

    val acceptedMethod: Option[String] = None

    val acceptedBody: Option[SampleRequest] = None

    val acceptedHeaders = Seq(
      ("header1", "value1"),
      ("header2", "value2"))

    val request = new okhttp3.Request.Builder()
      .url(baseUrl)
      .build()

    val statusCode = 200

    val message = "Hello World!"

    val json = s"""{ "message" : "$message" }"""

    val baseException = new IllegalArgumentException("")

    val okHttpResponse = new okhttp3.Response.Builder()
      .protocol(okhttp3.Protocol.HTTP_1_1)
      .request(request)
      .code(statusCode)
      .message("Alright")
      .body(okhttp3.ResponseBody.create(okhttp3.MediaType.parse("application/json"), json))
      .build()

    private def isValidMethod(req: okhttp3.Request): Boolean =
      acceptedMethod.getOrElse(req.method) == req.method

    private def isValidBody(req: okhttp3.Request): Boolean =
      acceptedBody match {
        case Some(r) =>
          val buffer = new okio.Buffer()
          req.body().writeTo(buffer)
          Json.parse(buffer.readUtf8()).as[SampleRequest](readsRequest) == r
        case _ => true
      }

    private def isValidHeaders(req: okhttp3.Request): Boolean = {
      val headers = 0 until req.headers().size() map { index =>
        val name = req.headers().name(index)
        (name, req.header(name))
      }
      headers == acceptedHeaders
    }

    val okHttpClient = new OkHttpClient(new okhttp3.OkHttpClient() {
      override def newCall(theRequest: okhttp3.Request): okhttp3.Call = {
        new okhttp3.Call {

          override def request(): Request = theRequest

          override def cancel(): Unit = {}

          override def isCanceled: Boolean = false

          override def isExecuted: Boolean = false

          override def enqueue(responseCallback: okhttp3.Callback): Unit = {}

          override def execute(): okhttp3.Response = {
            if (isValidMethod(theRequest) && isValidHeaders(theRequest) && isValidBody(theRequest))
              okHttpResponse
            else
              throw baseException
          }
        }
      }
    })
  }

}

class OkHttpClientSpec
  extends OkHttpClientSpecification {

  "OkHttpClient component" should {

    "return the response for a successfully get request" in new OkHttpClientScope {

      override val acceptedMethod = Some(Methods.GET.toString)

      val response = okHttpClient.doGet(baseUrl, acceptedHeaders).run

      response shouldEqual Right(HttpClientResponse(statusCode, Some(json)))
    }

    "return the response for a successfully delete request" in new OkHttpClientScope {

      override val acceptedMethod = Some(Methods.DELETE.toString)

      val response = okHttpClient.doDelete(baseUrl, acceptedHeaders).run

      response shouldEqual Right(HttpClientResponse(statusCode, Some(json)))
    }

    "return the response for a successfully empty post request" in new OkHttpClientScope {

      override val acceptedMethod = Some(Methods.POST.toString)

      val response = okHttpClient.doPost(baseUrl, acceptedHeaders).run

      response shouldEqual Right(HttpClientResponse(statusCode, Some(json)))
    }

    "return the response for a successfully post request" in new OkHttpClientScope {

      override val acceptedMethod = Some(Methods.POST.toString)

      val sampleRequest = SampleRequest("request")
      override val acceptedBody = Some(sampleRequest)

      val response = okHttpClient.doPost[SampleRequest](baseUrl, acceptedHeaders, sampleRequest).run

      response shouldEqual Right(HttpClientResponse(statusCode, Some(json)))
    }

    "return the response for a successfully empty put request" in new OkHttpClientScope {

      override val acceptedMethod = Some(Methods.PUT.toString)

      val response = okHttpClient.doPut(baseUrl, acceptedHeaders).run

      response shouldEqual Right(HttpClientResponse(statusCode, Some(json)))
    }

    "return the response for a successfully put request" in new OkHttpClientScope {

      override val acceptedMethod = Some(Methods.PUT.toString)

      val sampleRequest = SampleRequest("request")
      override val acceptedBody = Some(sampleRequest)

      val response = okHttpClient.doPut[SampleRequest](baseUrl, acceptedHeaders, sampleRequest).run

      response shouldEqual Right(HttpClientResponse(statusCode, Some(json)))
    }

    "return an Exception for an unexpected method" in new OkHttpClientScope {

      override val acceptedMethod = Some(Methods.GET.toString)

      val response = okHttpClient.doDelete(baseUrl, Seq.empty).run

      response must beAnInstanceOf[Left[IllegalArgumentException, _]]
    }

    "return an Exception for an unexpected request" in new OkHttpClientScope {

      override val acceptedMethod = Some(Methods.POST.toString)

      override val acceptedBody = Some(SampleRequest("request"))

      val response = okHttpClient.doPut[SampleRequest](baseUrl, Seq.empty, SampleRequest("bad_request")).run

      response must beAnInstanceOf[Left[IllegalArgumentException, _]]
    }

  }

}
