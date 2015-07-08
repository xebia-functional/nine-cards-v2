package com.fortysevendeg.ninecardslauncher.services.apps

import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.exceptions.Exceptions.NineCardsException
import com.fortysevendeg.ninecardslauncher.services.apps.models.Application

import scala.concurrent.Future
import scalaz.\/
import scalaz.concurrent.Task

trait AppsServices {
  def getInstalledApps(implicit context: ContextSupport): Task[NineCardsException \/ Seq[Application]]
}
