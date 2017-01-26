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

package cards.nine.api.rest.client.http

import cards.nine.commons.services.TaskService.TaskService
import play.api.libs.json.Writes

trait HttpClient {

  def doGet(
      url: String,
      httpHeaders: Seq[(String, String)]
  ): TaskService[HttpClientResponse]

  def doDelete(
      url: String,
      httpHeaders: Seq[(String, String)]
  ): TaskService[HttpClientResponse]

  def doPost(
      url: String,
      httpHeaders: Seq[(String, String)]
  ): TaskService[HttpClientResponse]

  def doPost[Req: Writes](
      url: String,
      httpHeaders: Seq[(String, String)],
      body: Req
  ): TaskService[HttpClientResponse]

  def doPut(
      url: String,
      httpHeaders: Seq[(String, String)]
  ): TaskService[HttpClientResponse]

  def doPut[Req: Writes](
      url: String,
      httpHeaders: Seq[(String, String)],
      body: Req
  ): TaskService[HttpClientResponse]

}
