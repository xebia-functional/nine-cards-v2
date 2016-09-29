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
