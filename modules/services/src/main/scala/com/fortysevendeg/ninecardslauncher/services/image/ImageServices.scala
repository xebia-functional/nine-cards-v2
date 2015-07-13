package com.fortysevendeg.ninecardslauncher.services.image

import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.exceptions.Exceptions.NineCardsException

import scalaz.\/
import scalaz.concurrent.Task

trait ImageServices {

  def androidAppPackage(request: AppPackage)(implicit context: ContextSupport): Task[NineCardsException \/ String]

  def androidAppWebsite(request: AppWebsite)(implicit context: ContextSupport): Task[NineCardsException \/ String]

}
