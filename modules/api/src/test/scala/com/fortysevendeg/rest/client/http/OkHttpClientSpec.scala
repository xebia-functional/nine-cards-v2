package com.fortysevendeg.rest.client.http

import com.fortysevendeg.ninecardslauncher.commons.exceptions.Exceptions.NineCardsException
import com.fortysevendeg.rest.client.SampleRequest
import com.squareup.{okhttp => okHttp}
import org.specs2.matcher.DisjunctionMatchers
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope
import play.api.libs.json.Json

trait OkHttpClientSpecification
  extends Specification
  with DisjunctionMatchers
  with Mockito {

  trait OkHttpClientScope
    extends Scope {

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

    val baseException = new IllegalArgumentException("")

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

    "returns the response for a successfully get request" in new OkHttpClientScope {

      override val acceptedMethod = Some(Methods.GET.toString)

      val response = okHttpClient.doGet(baseUrl, Seq.empty).run

      response must be_\/-[HttpClientResponse].which { response =>
        response.body shouldEqual Some(json)
      }
    }

    "returns the response for a successfully delete request" in new OkHttpClientScope {

      override val acceptedMethod = Some(Methods.DELETE.toString)

      val response = okHttpClient.doDelete(baseUrl, Seq.empty).run

      response must be_\/-[HttpClientResponse].which { response =>
        response.body shouldEqual Some(json)
      }
    }

    "returns the response for a successfully empty post request" in new OkHttpClientScope {

      override val acceptedMethod = Some(Methods.POST.toString)

      val response = okHttpClient.doPost(baseUrl, Seq.empty).run

      response must be_\/-[HttpClientResponse].which { response =>
        response.body shouldEqual Some(json)
      }
    }

    "returns the response for a successfully post request" in new OkHttpClientScope {

      override val acceptedMethod = Some(Methods.POST.toString)

      val sampleRequest = SampleRequest("request")
      override val acceptedBody = Some(sampleRequest)

      val response = okHttpClient.doPost[SampleRequest](baseUrl, Seq.empty, sampleRequest).run

      response must be_\/-[HttpClientResponse].which { response =>
        response.body shouldEqual Some(json)
      }
    }

    "returns the response for a successfully empty put request" in new OkHttpClientScope {

      override val acceptedMethod = Some(Methods.PUT.toString)

      val response = okHttpClient.doPut(baseUrl, Seq.empty).run

      response must be_\/-[HttpClientResponse].which { response =>
        response.body shouldEqual Some(json)
      }
    }

    "returns the response for a successfully put request" in new OkHttpClientScope {

      override val acceptedMethod = Some(Methods.PUT.toString)

      val sampleRequest = SampleRequest("request")
      override val acceptedBody = Some(sampleRequest)

      val response = okHttpClient.doPut[SampleRequest](baseUrl, Seq.empty, sampleRequest).run

      response must be_\/-[HttpClientResponse].which { response =>
        response.body shouldEqual Some(json)
      }
    }

    "returns Exception for an unexpected method" in new OkHttpClientScope {

      override val acceptedMethod = Some(Methods.GET.toString)

      val response = okHttpClient.doDelete(baseUrl, Seq.empty).run

      response  must be_-\/[NineCardsException].which { response =>
        response.cause shouldEqual Some(baseException)
      }
    }

    "returns Exception for an unexpected request" in new OkHttpClientScope {

      override val acceptedMethod = Some(Methods.POST.toString)

      override val acceptedBody = Some(SampleRequest("request"))

      val response = okHttpClient.doPut[SampleRequest](baseUrl, Seq.empty, SampleRequest("bad_request")).run

      response must be_-\/[NineCardsException].which { response =>
        response.cause shouldEqual Some(baseException)
      }
    }

  }

}
