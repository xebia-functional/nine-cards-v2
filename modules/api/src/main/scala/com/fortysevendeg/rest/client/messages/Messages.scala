package com.fortysevendeg.rest.client.messages

case class ServiceClientResponse[T](statusCode: Int, data: Option[T])

class ServiceClientException(message: String) extends RuntimeException(message)
