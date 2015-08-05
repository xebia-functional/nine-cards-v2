package com.fortysevendeg.ninecardslauncher.process.theme.impl

import com.fortysevendeg.ninecardslauncher.commons.NineCardExtensions._
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.exceptions.Exceptions.NineCardsException
import com.fortysevendeg.ninecardslauncher.commons.services.Service
import com.fortysevendeg.ninecardslauncher.commons.services.Service._
import com.fortysevendeg.ninecardslauncher.process.theme.{ImplicitsThemeException, ThemeException, ThemeProcess}
import com.fortysevendeg.ninecardslauncher.process.theme.models.NineCardsTheme
import com.fortysevendeg.ninecardslauncher.process.theme.models.NineCardsThemeImplicits._
import com.fortysevendeg.ninecardslauncher.process.utils.FileUtils
import play.api.libs.json.Json

import scalaz.EitherT._
import scalaz.Scalaz._
import scalaz.\/
import scalaz.concurrent.Task

class ThemeProcessImpl
  extends ThemeProcess
  with ImplicitsThemeException {

  val fileUtils = new FileUtils()

  val defaultTheme = "theme_light"

  override def getSelectedTheme(implicit context: ContextSupport) = for {
    json <- fileUtils.getJsonFromFile(s"$defaultTheme.json")
    theme <- getNineCardsThemeFromJson(json)
  } yield theme

  private[this] def getNineCardsThemeFromJson(json: String) = Service {
    Task {
      CatchAll[ThemeException] {
          Json.parse(json).as[NineCardsTheme]
        }
    }
  }
}
