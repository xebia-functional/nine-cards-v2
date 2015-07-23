package com.fortysevendeg.ninecardslauncher.process.theme

import java.io.InputStream

import android.content.res.{AssetManager, Resources}
import android.util.DisplayMetrics
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.exceptions.Exceptions.NineCardsException
import com.fortysevendeg.ninecardslauncher.process.theme.impl.ThemeProcessImpl
import com.fortysevendeg.ninecardslauncher.process.theme.models.{SearchPressedColor, SearchBackgroundColor, NineCardsTheme}
import com.fortysevendeg.ninecardslauncher.process.utils.FileUtils
import org.specs2.matcher.DisjunctionMatchers
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope
import android.graphics.Color

import scala.io.{BufferedSource, Source}
import scalaz.{-\/, \/-}
import scalaz.concurrent.Task

trait ThemeProcessSpecification
  extends Specification
  with DisjunctionMatchers
  with Mockito {

  trait ThemeProcessScope
    extends Scope {

    val resources = mock[Resources]
    resources.getDisplayMetrics returns mock[DisplayMetrics]

    val mockContextSupport = mock[ContextSupport]
    val mockFileUtils = mock[FileUtils]

    val themeProcess = new ThemeProcessImpl {
      override val fileUtils = mockFileUtils
    }
  }

  trait ValidFileUtilsResponses
    extends ThemeProcessData {

    self: ThemeProcessScope =>

    mockFileUtils.getJsonFromFile(any)(any) returns Task(\/-(validThemeJson))
  }

  trait WrongJsonFileUtilsResponses
    extends ThemeProcessData {

    self: ThemeProcessScope =>

    mockFileUtils.getJsonFromFile(any)(any) returns Task(\/-(wrongThemeJson))
  }

  trait WrongThemeStyleTypeFileUtilsResponses
    extends ThemeProcessData {

    self: ThemeProcessScope =>

    mockFileUtils.getJsonFromFile(any)(any) returns Task(\/-(wrongThemeStyleTypeJson))
  }

  trait WrongThemeStyleColorFileUtilsResponses
    extends ThemeProcessData {

    self: ThemeProcessScope =>

    mockFileUtils.getJsonFromFile(any)(any) returns Task(\/-(wrongThemeStyleColorJson))
  }

  trait ErrorFileUtilsResponses
    extends ThemeProcessData {

    self: ThemeProcessScope =>

    val nineCardsException = NineCardsException("Irrelevant message")

    mockFileUtils.getJsonFromFile(any)(any) returns Task(-\/(nineCardsException))
  }

}

class ThemeProcessSpec extends ThemeProcessSpecification {

  "getSelectedTheme" should {

    "return a valid NineCardsTheme object for a valid request" in
      new ThemeProcessScope with ValidFileUtilsResponses {
        val result = themeProcess.getSelectedTheme(mockContextSupport)

        result.run must be_\/-[NineCardsTheme].which { theme =>
          theme.name mustEqual "light"
          theme.get(SearchBackgroundColor) mustEqual intSampleColorWithoutAlpha
          theme.get(SearchPressedColor) mustEqual intSampleColorWithAlpha
        }
      }

    "return a NineCardsException if the JSON is not valid" in
      new ThemeProcessScope with WrongJsonFileUtilsResponses {
        val result = themeProcess.getSelectedTheme(mockContextSupport)

        result.run must be_-\/[NineCardsException]
      }

    "return a NineCardsException if a wrong theme style type is included in the JSON" in
      new ThemeProcessScope with WrongThemeStyleTypeFileUtilsResponses {
        val result = themeProcess.getSelectedTheme(mockContextSupport)

        result.run must be_-\/[NineCardsException]
      }

    "return a NineCardsException if a wrong theme style color is included in the JSON" in
      new ThemeProcessScope with WrongThemeStyleColorFileUtilsResponses {
        val result = themeProcess.getSelectedTheme(mockContextSupport)

        result.run must be_-\/[NineCardsException]
      }

    "return a NineCardsException if getJsonFromFile throws a exception" in
      new ThemeProcessScope with ErrorFileUtilsResponses {
        val result = themeProcess.getSelectedTheme(mockContextSupport)

        result.run must be_-\/[NineCardsException]
      }
  }

}
