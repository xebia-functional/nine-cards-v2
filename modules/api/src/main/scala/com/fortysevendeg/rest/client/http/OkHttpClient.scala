package com.fortysevendeg.rest.client.http

import com.fortysevendeg.ninecardslauncher.commons.NineCardExtensions._
import com.fortysevendeg.ninecardslauncher.commons.services.Service
import com.fortysevendeg.ninecardslauncher.commons.services.Service.ServiceDef2
import com.fortysevendeg.rest.client.http.Methods._
import com.squareup.{okhttp => okHttp}
import play.api.libs.json.{Json, Writes}
import com.facebook.stetho.okhttp.StethoInterceptor

import scalaz.concurrent.Task

class OkHttpClient(okHttpClient: okHttp.OkHttpClient = new okHttp.OkHttpClient)
  extends HttpClient
  with ImplicitsHttpClientExceptions {

  val jsonMediaType = okHttp.MediaType.parse("application/json")

  val textPlainMediaType = okHttp.MediaType.parse("text/plain")

  okHttpClient.networkInterceptors().add(new StethoInterceptor())

  override def doGet(
    url: String,
    httpHeaders: Seq[(String, String)]
    ): ServiceDef2[HttpClientResponse, HttpClientException] =
    doMethod(GET, url, httpHeaders)


  override def doDelete(
    url: String,
    httpHeaders: Seq[(String, String)]
    ): ServiceDef2[HttpClientResponse, HttpClientException] =
    doMethod(DELETE, url, httpHeaders)


  override def doPost(
    url: String,
    httpHeaders: Seq[(String, String)]
    ): ServiceDef2[HttpClientResponse, HttpClientException] =
    doMethod(POST, url, httpHeaders)


  override def doPost[Req: Writes](
    url: String,
    httpHeaders: Seq[(String, String)],
    body: Req
    ): ServiceDef2[HttpClientResponse, HttpClientException] =
    doMethod(POST, url, httpHeaders, Some(Json.toJson(body).toString()))

  override def doPut(
    url: String,
    httpHeaders: Seq[(String, String)]
    ): ServiceDef2[HttpClientResponse, HttpClientException] =
    doMethod(PUT, url, httpHeaders)


  override def doPut[Req: Writes](
    url: String,
    httpHeaders: Seq[(String, String)],
    body: Req
    ): ServiceDef2[HttpClientResponse, HttpClientException] =
    doMethod(PUT, url, httpHeaders, Some(Json.toJson(body).toString()))


  private[this] def doMethod[T](
    method: Method,
    url: String,
    httpHeaders: Seq[(String, String)],
    body: Option[String] = None,
    responseHandler: okHttp.Response => T = defaultResponseHandler _): ServiceDef2[T, HttpClientException] = Service {
    Task {
      CatchAll[HttpClientException] {
        val builder = createBuilderRequest(url, httpHeaders)
        val request = (method match {
          case GET => builder.get()
          case DELETE => builder.delete()
          case POST => builder.post(createBody(body))
          case PUT => builder.put(createBody(body))
        }).build()
        responseHandler(okHttpClient.newCall(request).execute())
      }
    }
  }

  private[this] def defaultResponseHandler(response: okHttp.Response): HttpClientResponse =
    HttpClientResponse(response.code(), Option(response.body()) map (_.string()))

  private[this] def createBuilderRequest(url: String, httpHeaders: Seq[(String, String)]): okHttp.Request.Builder =
    new okHttp.Request.Builder()
      .url(url)
      .headers(createHeaders(httpHeaders))

  private[this] def createHeaders(httpHeaders: Seq[(String, String)]): okHttp.Headers = {
    import scala.collection.JavaConverters._
    okHttp.Headers.of(httpHeaders.map(t => t._1 -> t._2).toMap.asJava)
  }

  private[this] def createBody(body: Option[String]) =
    body match {
      case Some(b) => okHttp.RequestBody.create(jsonMediaType, b)
      case _ => okHttp.RequestBody.create(textPlainMediaType, "")
    }

}

object Methods {

  sealed trait Method

  case object GET extends Method

  case object DELETE extends Method

  case object POST extends Method

  case object PUT extends Method

}