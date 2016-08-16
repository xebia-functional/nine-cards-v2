package com.fortysevendeg.ninecardslauncher.api.version2

object JsonImplicits {

  import play.api.libs.json._

  implicit val loginResponseReads = Json.reads[LoginResponse]

  implicit val loginRequestWrites = Json.writes[LoginRequest]

}
