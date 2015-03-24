package com.fortysevendeg.rest.client

import java.io.File

import com.squareup.okhttp.{MediaType, RequestBody}

object BodyContent {

  val emptyTextBody = StringBody("text/plain", "")

  abstract class Body {
    def requestBody: Option[RequestBody]
  }

  case class EmptyBody() extends Body {
    override def requestBody: Option[RequestBody] = None
  }

  case class StringBody(contentType: String, content: String) extends Body {
    override def requestBody: Option[RequestBody] = Some(RequestBody.create(MediaType.parse(contentType), content))
  }

  case class ByteArrayBody(contentType: String, content: Array[Byte]) extends Body {
    override def requestBody: Option[RequestBody] = Some(RequestBody.create(MediaType.parse(contentType), content))
  }

  case class FileBody(contentType: String, content: File) extends Body {
    override def requestBody: Option[RequestBody] = Some(RequestBody.create(MediaType.parse(contentType), content))
  }

}
