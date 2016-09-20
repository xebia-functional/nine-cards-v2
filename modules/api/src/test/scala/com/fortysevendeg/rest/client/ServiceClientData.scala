package com.fortysevendeg.rest.client

import com.fortysevendeg.rest.client.http.HttpClientException
import play.api.libs.json.Json

trait ServiceClientData {

  case class SampleRequest(message: String)

  case class SampleResponse(message: String)

  val baseUrl = "http://sampleUrl"

  implicit val readsResponse = Json.reads[SampleResponse]
  implicit val writesRequest = Json.writes[SampleRequest]

  val message = "Hello World!"

  val json = s"""{ "message" : "$message" }"""

  val sampleResponse = Some(SampleResponse(message))

  val exception = HttpClientException("")

}
