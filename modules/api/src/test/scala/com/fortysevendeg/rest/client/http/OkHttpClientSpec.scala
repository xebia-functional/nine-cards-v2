package com.fortysevendeg.rest.client.http


import com.fortysevendeg.BaseTestSupport
import com.fortysevendeg.rest.client.SampleRequest
import com.squareup.{okhttp => okHttp}
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope
import play.api.libs.json.Json

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext}

class OkHttpClientSupport
  extends Mockito
  with Scope {

  implicit val executionContext: ExecutionContext = ExecutionContext.Implicits.global

  val baseUrl = "http://sampleUrl"

  implicit val readsRequest = Json.reads[SampleRequest]
  implicit val writesRequest = Json.writes[SampleRequest]

  val acceptedMethod: Option[String] = None

  val acceptedBody: Option[SampleRequest] = None

  val request = new okHttp.Request.Builder()
      .url(baseUrl)
      .build()

  val message = "Hello World!"

  val json = s"""{ "message" : "$message" }"""

  val okHttpResponse = new okHttp.Response.Builder()
      .protocol(okHttp.Protocol.HTTP_1_1)
      .request(request)
      .code(200)
      .message("Alright")
      .body(okHttp.ResponseBody.create(okHttp.MediaType.parse("application/json"), json))
      .build()

  private def isValidMethod(req: okHttp.Request): Boolean =
    acceptedMethod.getOrElse(req.method) == req.method

  private def isValidBody(req: okHttp.Request): Boolean =
    acceptedBody match {
      case Some(r) =>
        val buffer = new okio.Buffer()
        req.body().writeTo(buffer)
        Json.parse(buffer.readUtf8()).as[SampleRequest](readsRequest) == r
      case _ => true
    }

  val okHttpClient = new OkHttpClient(new okHttp.OkHttpClient() {
    override def newCall(request: okHttp.Request): okHttp.Call = {
      new okHttp.Call(this, request) {
        override def cancel(): Unit = {}
        override def isCanceled: Boolean = false
        override def enqueue(responseCallback: okHttp.Callback): Unit = {}
        override def execute(): okHttp.Response = {
          if (isValidMethod(request) && isValidBody(request))
            okHttpResponse
          else
            throw new IllegalArgumentException("")
        }
      }
    }
  })

}

class OkHttpClientSpec
    extends Specification
    with BaseTestSupport {

  "OkHttpClient component" should {

    "returns the response for a successfully get request" in new OkHttpClientSupport {

      override val acceptedMethod = Some(Methods.GET.toString)

      val response = Await.result(okHttpClient.doGet(baseUrl, Seq.empty), Duration.Inf)
      response.body shouldEqual Some(json)
    }

    "returns the response for a successfully delete request" in new OkHttpClientSupport {

      override val acceptedMethod = Some(Methods.DELETE.toString)

      val response = Await.result(okHttpClient.doDelete(baseUrl, Seq.empty), Duration.Inf)
      response.body shouldEqual Some(json)
    }

    "returns the response for a successfully empty post request" in new OkHttpClientSupport {

      override val acceptedMethod = Some(Methods.POST.toString)

      val response = Await.result(okHttpClient.doPost(baseUrl, Seq.empty), Duration.Inf)
      response.body shouldEqual Some(json)
    }

    "returns the response for a successfully post request" in new OkHttpClientSupport {

      override val acceptedMethod = Some(Methods.POST.toString)

      val sampleRequest = SampleRequest("request")
      override val acceptedBody = Some(sampleRequest)

      val response = Await.result(okHttpClient.doPost[SampleRequest](baseUrl, Seq.empty, sampleRequest), Duration.Inf)
      response.body shouldEqual Some(json)
    }

    "returns the response for a successfully empty put request" in new OkHttpClientSupport {

      override val acceptedMethod = Some(Methods.PUT.toString)

      val response = Await.result(okHttpClient.doPut(baseUrl, Seq.empty), Duration.Inf)
      response.body shouldEqual Some(json)
    }

    "returns the response for a successfully put request" in new OkHttpClientSupport {

      override val acceptedMethod = Some(Methods.PUT.toString)

      val sampleRequest = SampleRequest("request")
      override val acceptedBody = Some(sampleRequest)

      val response = Await.result(okHttpClient.doPut[SampleRequest](baseUrl, Seq.empty, sampleRequest), Duration.Inf)
      response.body shouldEqual Some(json)
    }

    "returns Exception for an unexpected method" in new OkHttpClientSupport {

      override val acceptedMethod = Some(Methods.GET.toString)

      Await.result(okHttpClient.doDelete(baseUrl, Seq.empty), Duration.Inf) must throwA[IllegalArgumentException]
    }

    "returns Exception for an unexpected request" in new OkHttpClientSupport {

      override val acceptedMethod = Some(Methods.POST.toString)

      override val acceptedBody = Some(SampleRequest("request"))

      Await.result(okHttpClient.doPut[SampleRequest](baseUrl, Seq.empty, SampleRequest("bad_request")), Duration.Inf) must throwA[IllegalArgumentException]
    }

  }

}
