package com.fortysevendeg.rest.client.http

import com.fortysevendeg.ninecardslauncher.commons.services.Service.ServiceDef2
import play.api.libs.json.Writes

trait HttpClient {

  def doGet(
    url: String,
    httpHeaders: Seq[(String, String)]
    ): ServiceDef2[HttpClientResponse, HttpClientException]

  def doDelete(
    url: String,
    httpHeaders: Seq[(String, String)]
    ): ServiceDef2[HttpClientResponse, HttpClientException]

  def doPost(
    url: String,
    httpHeaders: Seq[(String, String)]
    ): ServiceDef2[HttpClientResponse, HttpClientException]

  def doPost[Req: Writes](
    url: String,
    httpHeaders: Seq[(String, String)],
    body: Req
    ): ServiceDef2[HttpClientResponse, HttpClientException]

  def doPut(
    url: String,
    httpHeaders: Seq[(String, String)]
    ): ServiceDef2[HttpClientResponse, HttpClientException]

  def doPut[Req: Writes](
    url: String,
    httpHeaders: Seq[(String, String)],
    body: Req
    ): ServiceDef2[HttpClientResponse, HttpClientException]

}
