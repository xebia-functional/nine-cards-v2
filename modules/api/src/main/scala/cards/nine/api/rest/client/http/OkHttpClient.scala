package cards.nine.api.rest.client.http

import cards.nine.commons.CatchAll
import cards.nine.commons.services.TaskService
import cards.nine.commons.services.TaskService._
import cards.nine.api.rest.client.http.Methods._
import play.api.libs.json.{Json, Writes}
import scala.collection.JavaConverters._

class OkHttpClient(okHttpClient: okhttp3.OkHttpClient = new okhttp3.OkHttpClient)
  extends HttpClient
  with ImplicitsHttpClientExceptions {

  val jsonMediaType = okhttp3.MediaType.parse("application/json")

  val textPlainMediaType = okhttp3.MediaType.parse("text/plain")

  override def doGet(
    url: String,
    httpHeaders: Seq[(String, String)]
    ): TaskService[HttpClientResponse] =
    doMethod(GET, url, httpHeaders)


  override def doDelete(
    url: String,
    httpHeaders: Seq[(String, String)]
    ): TaskService[HttpClientResponse] =
    doMethod(DELETE, url, httpHeaders)


  override def doPost(
    url: String,
    httpHeaders: Seq[(String, String)]
    ): TaskService[HttpClientResponse] =
    doMethod(POST, url, httpHeaders)


  override def doPost[Req: Writes](
    url: String,
    httpHeaders: Seq[(String, String)],
    body: Req
    ): TaskService[HttpClientResponse] =
    doMethod(POST, url, httpHeaders, Some(Json.toJson(body).toString()))

  override def doPut(
    url: String,
    httpHeaders: Seq[(String, String)]
    ): TaskService[HttpClientResponse] =
    doMethod(PUT, url, httpHeaders)


  override def doPut[Req: Writes](
    url: String,
    httpHeaders: Seq[(String, String)],
    body: Req
    ): TaskService[HttpClientResponse] =
    doMethod(PUT, url, httpHeaders, Some(Json.toJson(body).toString()))


  private[this] def doMethod[T](
    method: Method,
    url: String,
    httpHeaders: Seq[(String, String)],
    body: Option[String] = None,
    responseHandler: okhttp3.Response => T = defaultResponseHandler _): TaskService[T] = TaskService {
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

  private[this] def defaultResponseHandler(response: okhttp3.Response): HttpClientResponse =
    HttpClientResponse(response.code(), Option(response.body()) map (_.string()))

  private[this] def createBuilderRequest(url: String, httpHeaders: Seq[(String, String)]): okhttp3.Request.Builder =
    new okhttp3.Request.Builder()
      .url(url)
      .headers(createHeaders(httpHeaders))

  private[this] def createHeaders(httpHeaders: Seq[(String, String)]): okhttp3.Headers =
    okhttp3.Headers.of(httpHeaders.map {
      case (key, value) => key -> value
    }.toMap.asJava)

  private[this] def createBody(body: Option[String]) =
    body match {
      case Some(b) => okhttp3.RequestBody.create(jsonMediaType, b)
      case _ => okhttp3.RequestBody.create(textPlainMediaType, "")
    }

}

object Methods {

  sealed trait Method

  case object GET extends Method

  case object DELETE extends Method

  case object POST extends Method

  case object PUT extends Method

}