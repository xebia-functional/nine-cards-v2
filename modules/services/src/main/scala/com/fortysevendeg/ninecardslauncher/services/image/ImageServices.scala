package com.fortysevendeg.ninecardslauncher.services.image

import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.exceptions.Exceptions.NineCardsException

import scalaz.\/
import scalaz.concurrent.Task

trait ImageServices {

  /** Obtains the path from package creating a new entry if non is found the very first time */
  def saveAppIcon(request: AppPackage)(implicit context: ContextSupport): Task[NineCardsException \/ String]

  /** Obtains the path from url creating a new entry if non is found the very first time */
  def saveAppIcon(request: AppWebsite)(implicit context: ContextSupport): Task[NineCardsException \/ String]

}
