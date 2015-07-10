package com.fortysevendeg.ninecardslauncher.services.image

import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.exceptions.Exceptions.NineCardsException

import scalaz.\/
import scalaz.concurrent.Task

trait ImageServices {

  def getAppPackagePathAndSaveIfNotExists(request: AppPackage)(implicit context: ContextSupport): Task[NineCardsException \/ String]

  def getAppWebsitePathAndSaveIfNotExists(request: AppWebsite)(implicit context: ContextSupport): Task[NineCardsException \/ String]

}
