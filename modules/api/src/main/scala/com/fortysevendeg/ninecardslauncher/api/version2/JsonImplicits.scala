package com.fortysevendeg.ninecardslauncher.api.version2

object JsonImplicits {

  import play.api.libs.json._

  implicit val loginResponseReads = Json.reads[LoginResponse]
  implicit val installationResponseReads = Json.reads[InstallationResponse]

  implicit val loginRequestWrites = Json.writes[LoginRequest]
  implicit val installationRequestWrites = Json.writes[InstallationRequest]

}
