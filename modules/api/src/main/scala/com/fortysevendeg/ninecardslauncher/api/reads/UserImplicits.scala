package com.fortysevendeg.ninecardslauncher.api.reads

import com.fortysevendeg.ninecardslauncher.api.model._

trait UserImplicits {

  import play.api.libs.json._

  implicit val authAnonymousReads = Json.reads[AuthAnonymous]
  implicit val authFacebookReads = Json.reads[AuthFacebook]
  implicit val authTwitterReads = Json.reads[AuthTwitter]
  implicit val authGoogleReads = Json.reads[AuthGoogle]
  implicit val authGoogleDeviceReads = Json.reads[AuthGoogleDevice]
  implicit val authDataReads = Json.reads[AuthData]
  implicit val userReads = Json.reads[User]
  implicit val installationReads = Json.reads[Installation]

}
