package com.fortysevendeg.ninecardslauncher.process.theme.impl

import com.fortysevendeg.ninecardslauncher.commons.NineCardExtensions._
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.Service
import com.fortysevendeg.ninecardslauncher.commons.utils.{ImplicitsAssetException, AssetException, FileUtils}
import com.fortysevendeg.ninecardslauncher.process.theme.models.NineCardsTheme
import com.fortysevendeg.ninecardslauncher.process.theme.models.NineCardsThemeImplicits._
import com.fortysevendeg.ninecardslauncher.process.theme.{ImplicitsThemeException, ThemeException, ThemeProcess}
import play.api.libs.json.Json

import scala.util.{Failure, Success}
import scalaz.concurrent.Task

class ThemeProcessImpl
  extends ThemeProcess
  with ImplicitsThemeException
  with ImplicitsAssetException {

  val fileUtils = new FileUtils()

  override def getTheme(themeFile: String)(implicit context: ContextSupport) = for {
    json <- getJsonFromThemeFile(themeFile)
    theme <- getNineCardsThemeFromJson(json)
  } yield theme

  private[this] def getJsonFromThemeFile(defaultTheme: String)(implicit context: ContextSupport) = Service {
    Task {
      CatchAll[AssetException] {
        fileUtils.readFile(s"$defaultTheme.json") match {
          case Success(json) => json
          case Failure(ex) => throw ex
        }
      }
    }
  }

  private[this] def getNineCardsThemeFromJson(json: String) = Service {
    Task {
      CatchAll[ThemeException] {
          Json.parse(json).as[NineCardsTheme]
        }
    }
  }
}
