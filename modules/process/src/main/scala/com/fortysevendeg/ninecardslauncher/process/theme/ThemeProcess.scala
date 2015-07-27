package com.fortysevendeg.ninecardslauncher.process.theme

import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.exceptions.Exceptions.NineCardsException
import com.fortysevendeg.ninecardslauncher.process.theme.models.NineCardsTheme

import scalaz.\/
import scalaz.concurrent.Task

trait ThemeProcess {
  def getSelectedTheme(implicit context: ContextSupport): Task[NineCardsException \/ NineCardsTheme]
}
