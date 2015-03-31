package com.fortysevendeg.rest.client.messages

case class HttpClientResponse[T](statusCode: Int, data: Option[T])

class HttpClientException(message: String) extends RuntimeException(message)
