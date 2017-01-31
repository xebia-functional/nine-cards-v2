/*
 * Copyright 2017 47 Degrees, LLC. <http://www.47deg.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cards.nine.api.rest.client

import cards.nine.api.rest.client.http.{HttpClientException, HttpClientResponse}
import play.api.libs.json.Json

trait ServiceClientData {

  case class SampleRequest(message: String)

  case class SampleResponse(message: String)

  val baseUrl = "http://sampleUrl"

  val path = "/myPath"

  val headers = Seq(("header1", "value1"), ("header2", "value2"))

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
