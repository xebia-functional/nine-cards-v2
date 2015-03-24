package com.fortysevendeg.rest.client

object HttpMethods {

  trait HttpMethod

  case object GET extends HttpMethod
  case object HEAD extends HttpMethod
  case object POST extends HttpMethod
  case object DELETE extends HttpMethod
  case object PUT extends HttpMethod
  case object PATCH extends HttpMethod

}
