package com.fortysevendeg.rest.client

import java.io.InputStream

import com.fortysevendeg.rest.client.BodyContent.{StringBody, Body, EmptyBody}
import com.fortysevendeg.rest.client.HttpMethods._
import com.squareup.okhttp
import io.taig.communicator.RichOkHttpRequestBuilder
import io.taig.communicator.request.Request
import io.taig.communicator.response.Plain
import io.taig.communicator.result.Parser
import org.apache.commons.lang3.text.StrSubstitutor
import play.api.libs.json.{Writes, Json, Reads}

import scala.concurrent.{ExecutionContext, Future}

trait ServiceClient {

  implicit val executionContext: ExecutionContext

  val baseUrl: String

  val okHttpClient: okhttp.OkHttpClient = new okhttp.OkHttpClient

  def get[T](
      path: String,
      headers: Seq[(String, String)] = Seq.empty,
      pathValues: Map[String, String] = Map.empty[String, String])(implicit reads: Reads[T]): Future[T] =
    call(path = path, method = GET, headers = headers, pathValues = pathValues)

  def rawBodyPost[T](
      path: String,
      headers: Seq[(String, String)] = Seq.empty,
      pathValues: Map[String, String] = Map.empty[String, String],
      rawBody: Body)(implicit reads: Reads[T]): Future[T] =
    call(path = path, method = POST, headers = headers, pathValues = pathValues, rawBody = rawBody)

  def post[T, S](
      path: String,
      headers: Seq[(String, String)] = Seq.empty,
      pathValues: Map[String, String] = Map.empty[String, String],
      body: S)(implicit reads: Reads[T], writes: Writes[S]): Future[T] =
    call[T](path = path, method = POST, headers = headers, pathValues = pathValues, rawBody = createBody(body))

  def rawBodyPut[T](
      path: String,
      headers: Seq[(String, String)] = Seq.empty,
      pathValues: Map[String, String] = Map.empty[String, String],
      rawBody: Body)(implicit reads: Reads[T]): Future[T] =
    call(path = path, method = PUT, headers = headers, pathValues = pathValues, rawBody = rawBody)

  def put[T, S](
      path: String,
      headers: Seq[(String, String)] = Seq.empty,
      pathValues: Map[String, String] = Map.empty[String, String],
      rawBody: S)(implicit reads: Reads[T], writes: Writes[S]): Future[T] =
    call[T](path = path, method = PUT, headers = headers, pathValues = pathValues, rawBody = createBody(rawBody))

  def delete[T](
      path: String,
      headers: Seq[(String, String)] = Seq.empty,
      pathValues: Map[String, String] = Map.empty[String, String])(implicit reads: Reads[T]): Future[T] =
    call(path = path, method = DELETE, headers = headers, pathValues = pathValues)

  def call[T](
    path: String,
    method: HttpMethod,
    headers: Seq[(String, String)] = Seq.empty,
    pathValues: Map[String, String] = Map.empty[String, String],
    rawBody: Body = EmptyBody())(implicit reads: Reads[T]): Future[T] = {

    val replacedPath = parseUrl(path = path, params = pathValues)

    val builder = Request(replacedPath)
      .headers(toHeaders(headers))

    addMethod(builder, method, rawBody.requestBody)
      .parse(okHttpClient, new DefaultParser[T])
      .transform(response => response.payload, throwable => throwable)
  }

  private def createBody[T](bodyObject: T)(implicit writes: Writes[T]): Body =
    StringBody("application/json", Json.toJson(bodyObject).toString())

  private def parseUrl(path: String, params: Map[String, String]): String = {
    import scala.collection.JavaConversions.mapAsJavaMap
    StrSubstitutor.replace(baseUrl.concat(path), params)
  }

  private def toHeaders(headers: Seq[(String, String)]): okhttp.Headers = {
    val headersBuilder = new okhttp.Headers.Builder()
    headers map { header =>
      headersBuilder.add(header._1, header._2)
    }
    headersBuilder.build()
  }

  private def addMethod(
      builder: okhttp.Request.Builder,
      method: HttpMethods.HttpMethod,
      requestBody: Option[okhttp.RequestBody]) =
    (method, requestBody) match {
      case (GET, _) => builder.get()
      case (DELETE, _) => builder.delete()
      case (HEAD, _) => builder.head()
      case (POST, Some(rb)) => builder.post(rb)
      case (PUT, Some(rb)) => builder.put(rb)
      case (PATCH, Some(rb)) => builder.patch(rb)
      case _ => throw new IllegalArgumentException
    }

  class DefaultParser[T](implicit reads: Reads[T]) extends Parser[T] {
    override def parse(response: Plain, stream: InputStream): T =
      Json.parse(scala.io.Source.fromInputStream(stream).mkString).as[T]
  }

}