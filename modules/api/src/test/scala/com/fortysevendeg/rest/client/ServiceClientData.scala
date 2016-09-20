package com.fortysevendeg.rest.client

import com.fortysevendeg.rest.client.http.{HttpClientException, HttpClientResponse}
import play.api.libs.json.Json

trait ServiceClientData {

  case class SampleRequest(message: String)

  case class SampleResponse(message: String)

  val baseUrl = "http://sampleUrl"

  val path = "/myPath"

  val headers = Seq(
    ("header1", "value1"),
    ("header2", "value2"))

  implicit val readsResponse = Json.reads[SampleResponse]
  implicit val writesRequest = Json.writes[SampleRequest]

  val message = "Hello World!"

  val json = s"""{ "message" : "$message" }"""

  val invalidJson = s"""{ "unknownProperty" : false }"""

  val sampleRequest = SampleRequest("sample-request")

  val sampleResponse = Some(SampleResponse(message))

  val statusCodeOk = 200

  val statusCodeNotFound = 404

  val validHttpClientResponse = HttpClientResponse(statusCodeOk, Some(json))

  val validEmptyHttpClientResponse = HttpClientResponse(statusCodeOk, None)

  val notFoundHttpClientResponse = HttpClientResponse(statusCodeNotFound, None)

  val invalidHttpClientResponse = HttpClientResponse(statusCodeOk, Some(invalidJson))

  val exception = HttpClientException("")

}
