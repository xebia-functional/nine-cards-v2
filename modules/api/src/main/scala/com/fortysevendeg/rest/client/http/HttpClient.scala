package com.fortysevendeg.rest.client.http

import com.fortysevendeg.ninecardslauncher.commons.services.CatsService.CatsService
import play.api.libs.json.Writes

trait HttpClient {

  def doGet(
    url: String,
    httpHeaders: Seq[(String, String)]
    ): CatsService[HttpClientResponse]

  def doDelete(
    url: String,
    httpHeaders: Seq[(String, String)]
    ): CatsService[HttpClientResponse]

  def doPost(
    url: String,
    httpHeaders: Seq[(String, String)]
    ): CatsService[HttpClientResponse]

  def doPost[Req: Writes](
    url: String,
    httpHeaders: Seq[(String, String)],
    body: Req
    ): CatsService[HttpClientResponse]

  def doPut(
    url: String,
    httpHeaders: Seq[(String, String)]
    ): CatsService[HttpClientResponse]

  def doPut[Req: Writes](
    url: String,
    httpHeaders: Seq[(String, String)],
    body: Req
    ): CatsService[HttpClientResponse]

}
