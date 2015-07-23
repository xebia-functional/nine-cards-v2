package com.fortysevendeg.ninecardslauncher.process.theme.impl

import com.fortysevendeg.ninecardslauncher.commons.NineCardExtensions._
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.exceptions.Exceptions.NineCardsException
import com.fortysevendeg.ninecardslauncher.commons.services.Service._
import com.fortysevendeg.ninecardslauncher.process.theme.ThemeProcess
import com.fortysevendeg.ninecardslauncher.process.theme.models.NineCardsTheme
import com.fortysevendeg.ninecardslauncher.process.theme.models.NineCardsThemeImplicits._
import com.fortysevendeg.ninecardslauncher.process.utils.FileUtils
import play.api.libs.json.Json

import scalaz.EitherT._
import scalaz.Scalaz._
import scalaz.\/
import scalaz.concurrent.Task

class ThemeProcessImpl extends ThemeProcess {

  val fileUtils = new FileUtils()

  override def getSelectedTheme(implicit context: ContextSupport): Task[NineCardsException \/ NineCardsTheme] = {

    val selectedTheme = "light"

    for {
      json <- fileUtils.getJsonFromFile(s"theme_$selectedTheme.json") ▹ eitherT
      theme <- Task(fromTryCatchNineCardsException(Json.parse(json).as[NineCardsTheme])) ▹ eitherT
    } yield theme
  }
}
