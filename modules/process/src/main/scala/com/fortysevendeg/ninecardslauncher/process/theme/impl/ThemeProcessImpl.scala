package com.fortysevendeg.ninecardslauncher.process.theme.impl

import cards.nine.commons.CatchAll
import cards.nine.commons.contexts.ContextSupport
import cards.nine.commons.services.TaskService
import cards.nine.commons.services.TaskService._
import cards.nine.commons.utils.{AssetException, FileUtils, ImplicitsAssetException}
import com.fortysevendeg.ninecardslauncher.process.theme.models.NineCardsTheme
import com.fortysevendeg.ninecardslauncher.process.theme.models.NineCardsThemeImplicits._
import com.fortysevendeg.ninecardslauncher.process.theme.{ImplicitsThemeException, ThemeException, ThemeProcess}
import play.api.libs.json.Json

import scala.util.{Failure, Success}

class ThemeProcessImpl
  extends ThemeProcess
  with ImplicitsThemeException
  with ImplicitsAssetException {

  val fileUtils = new FileUtils()

  override def getTheme(themeFile: String)(implicit context: ContextSupport) = for {
    json <- getJsonFromThemeFile(themeFile)
    theme <- getNineCardsThemeFromJson(json)
  } yield theme

  private[this] def getJsonFromThemeFile(defaultTheme: String)(implicit context: ContextSupport) = TaskService {
      CatchAll[AssetException] {
        fileUtils.readFile(s"$defaultTheme.json") match {
          case Success(json) => json
          case Failure(ex) => throw ex
        }
    }
  }

  private[this] def getNineCardsThemeFromJson(json: String) = TaskService {
      CatchAll[ThemeException] {
          Json.parse(json).as[NineCardsTheme]
        }
  }
}
