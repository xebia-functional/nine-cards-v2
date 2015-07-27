package com.fortysevendeg.ninecardslauncher.process.userconfig

import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.exceptions.Exceptions.NineCardsException
import com.fortysevendeg.ninecardslauncher.process.userconfig.models.{UserCollection, UserInfo}

import scalaz.\/
import scalaz.concurrent.Task

trait UserConfigProcess {
  def getUserInfo(implicit context: ContextSupport): Task[NineCardsException \/ UserInfo]
  def getUserCollection(deviceId: String)(implicit context: ContextSupport): Task[NineCardsException \/ Seq[UserCollection]]
}
