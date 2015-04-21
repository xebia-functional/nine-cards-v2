package com.fortysevendeg.ninecardslauncher.api.reads

import com.fortysevendeg.ninecardslauncher.api.model._

object UserImplicits {

  import play.api.libs.json._

  implicit val authAnonymousReads = Json.reads[AuthAnonymous]
  implicit val authFacebookReads = Json.reads[AuthFacebook]
  implicit val authTwitterReads = Json.reads[AuthTwitter]
  implicit val authGoogleDeviceReads = Json.reads[AuthGoogleDevice]
  implicit val authGoogleReads = Json.reads[AuthGoogle]
  implicit val authDataReads = Json.reads[AuthData]
  implicit val userReads = Json.reads[User]
  implicit val installationReads = Json.reads[Installation]


  implicit val authAnonymousWrites = Json.writes[AuthAnonymous]
  implicit val authFacebookWrites = Json.writes[AuthFacebook]
  implicit val authTwitterWrites = Json.writes[AuthTwitter]
  implicit val authGoogleDeviceWrites = Json.writes[AuthGoogleDevice]
  implicit val authGoogleWrites = Json.writes[AuthGoogle]
  implicit val authDataWrites = Json.writes[AuthData]
  implicit val userWrites = Json.writes[User]
  implicit val installationWrites = Json.writes[Installation]

}
