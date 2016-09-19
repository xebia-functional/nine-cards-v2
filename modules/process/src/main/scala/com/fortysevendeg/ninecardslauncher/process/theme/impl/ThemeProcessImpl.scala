package com.fortysevendeg.ninecardslauncher.process.theme.impl

import com.fortysevendeg.ninecardslauncher.commons.CatchAll
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService._
import com.fortysevendeg.ninecardslauncher.commons.utils.{AssetException, FileUtils, ImplicitsAssetException}
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

  override def getTheme(themeFile: String)(implicit context: ContextSupport) = {

    def getJsonFromThemeFile = TaskService {
      CatchAll[AssetException] {
        fileUtils.readFile(s"$themeFile.json") match {
          case Success(json) => json
          case Failure(ex) => throw ex
        }
      }
    }

    def getNineCardsThemeFromJson(json: String) = TaskService {
      CatchAll[ThemeException](Json.parse(json).as[NineCardsTheme])
    }

    for {
      json <- getJsonFromThemeFile
      theme <- getNineCardsThemeFromJson(json)
    } yield theme
  }

}
