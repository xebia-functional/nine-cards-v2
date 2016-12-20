package cards.nine.process.social.impl

import cards.nine.commons.test.data.UserTestData
import cards.nine.commons.test.data.UserValues._
import cards.nine.services.plus.models.GooglePlusProfile

trait SocialProfileProcessImplData extends UserTestData {

  val account  = "example@domain.com"
  val clientId = "fake-client-id"

  val googlePlusProfile =
    GooglePlusProfile(name = Some(userName), avatarUrl = Some(avatar), coverUrl = Some(cover))

}
