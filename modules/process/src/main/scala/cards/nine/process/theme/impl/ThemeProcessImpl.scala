/*
 * Copyright 2017 47 Degrees, LLC. <http://www.47deg.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cards.nine.process.theme.impl

import cards.nine.commons.CatchAll
import cards.nine.commons.contexts.ContextSupport
import cards.nine.commons.services.TaskService
import cards.nine.commons.services.TaskService._
import cards.nine.commons.utils.{AssetException, FileUtils, ImplicitsAssetException}
import cards.nine.models.NineCardsTheme
import cards.nine.models.NineCardsThemeImplicits._
import cards.nine.process.theme.{ImplicitsThemeException, ThemeException, ThemeProcess}
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
          case Failure(ex)   => throw ex
        }
      }
    }

    def getNineCardsThemeFromJson(json: String) = TaskService {
      CatchAll[ThemeException](Json.parse(json).as[NineCardsTheme])
    }

    for {
      json  <- getJsonFromThemeFile
      theme <- getNineCardsThemeFromJson(json)
    } yield theme
  }

}
