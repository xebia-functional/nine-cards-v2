package com.fortysevendeg.rest.client.http

import com.fortysevendeg.ninecardslauncher.commons.exceptions.Exceptions.NineCardsException
import play.api.libs.json.Writes

import scalaz.\/
import scalaz.concurrent.Task

trait HttpClient {

  def doGet(
    url: String,
    httpHeaders: Seq[(String, String)]
    ): Task[NineCardsException \/ HttpClientResponse]

  def doDelete(
    url: String,
    httpHeaders: Seq[(String, String)]
    ): Task[NineCardsException \/ HttpClientResponse]

  def doPost(
    url: String,
    httpHeaders: Seq[(String, String)]
    ): Task[NineCardsException \/ HttpClientResponse]

  def doPost[Req: Writes](
    url: String,
    httpHeaders: Seq[(String, String)],
    body: Req
    ): Task[NineCardsException \/ HttpClientResponse]

  def doPut(
    url: String,
    httpHeaders: Seq[(String, String)]
    ): Task[NineCardsException \/ HttpClientResponse]

  def doPut[Req: Writes](
    url: String,
    httpHeaders: Seq[(String, String)],
    body: Req
    ): Task[NineCardsException \/ HttpClientResponse]

}
