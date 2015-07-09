package com.fortysevendeg.ninecardslauncher.services.image

import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.exceptions.Exceptions.NineCardsException

import scalaz.\/
import scalaz.concurrent.Task

trait ImageServices {

  def saveAppIcon(request: AppPackage)(implicit context: ContextSupport): Task[NineCardsException \/ String]

  def saveImageFromUrl(request: AppWebsite)(implicit context: ContextSupport): Task[NineCardsException \/ String]

}
