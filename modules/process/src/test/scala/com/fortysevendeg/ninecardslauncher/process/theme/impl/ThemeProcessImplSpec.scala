package com.fortysevendeg.ninecardslauncher.process.theme.impl

import android.content.res.Resources
import android.util.DisplayMetrics
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.utils.FileUtils
import com.fortysevendeg.ninecardslauncher.process.theme.ThemeException
import com.fortysevendeg.ninecardslauncher.process.theme.models._
import com.fortysevendeg.ninecardslauncher.commons.utils.AssetException
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope
import rapture.core.{Answer, Errata}

import scala.util.Success

trait ThemeProcessSpecification
  extends Specification
  with Mockito {

  val assetException = AssetException("")

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

    mockFileUtils.readFile(any)(any) returns Success(validThemeJson)
  }

  trait WrongJsonFileUtilsResponses
    extends ThemeProcessData {

    self: ThemeProcessScope =>

    mockFileUtils.readFile(any)(any) returns Success(wrongThemeJson)
  }

  trait WrongThemeStyleTypeFileUtilsResponses
    extends ThemeProcessData {

    self: ThemeProcessScope =>

    mockFileUtils.readFile(any)(any) returns Success(wrongThemeStyleTypeJson)
  }

  trait WrongThemeStyleColorFileUtilsResponses
    extends ThemeProcessData {

    self: ThemeProcessScope =>

    mockFileUtils.readFile(any)(any) returns Success(wrongThemeStyleColorJson)
  }

  trait ErrorFileUtilsResponses
    extends ThemeProcessData {

    self: ThemeProcessScope =>

    mockFileUtils.readFile(any)(any) throws assetException
  }

}

class ThemeProcessImplSpec
  extends ThemeProcessSpecification {

  "getSelectedTheme" should {

    "return a valid NineCardsTheme object for a valid request" in
      new ThemeProcessScope with ValidFileUtilsResponses {
        val result = themeProcess.getTheme("")(mockContextSupport).run.run

        result must beLike {
          case Answer(theme) =>
            theme.name mustEqual defaultThemeName
            theme.get(SearchBackgroundColor) mustEqual intSampleColorWithoutAlpha
            theme.get(SearchPressedColor) mustEqual intSampleColorWithAlpha
        }
      }

    "return a ThemeException if the JSON is not valid" in
      new ThemeProcessScope with WrongJsonFileUtilsResponses {
        val result = themeProcess.getTheme("")(mockContextSupport).run.run
        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, exception)) => exception must beAnInstanceOf[ThemeException]
          }
        }
      }

    "return a ThemeException if a wrong theme style type is included in the JSON" in
      new ThemeProcessScope with WrongThemeStyleTypeFileUtilsResponses {
        val result = themeProcess.getTheme("")(mockContextSupport).run.run
        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, exception)) => exception must beAnInstanceOf[ThemeException]
          }
        }
      }

    "return a ThemeException if a wrong theme style color is included in the JSON" in
      new ThemeProcessScope with WrongThemeStyleColorFileUtilsResponses {
        val result = themeProcess.getTheme("")(mockContextSupport).run.run
        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, exception)) => exception must beAnInstanceOf[ThemeException]
          }
        }
      }

    "return a AssetException if getJsonFromFile throws a exception" in
      new ThemeProcessScope with ErrorFileUtilsResponses {
        val result = themeProcess.getTheme("")(mockContextSupport).run.run
        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, exception)) => exception must beAnInstanceOf[AssetException]
          }
        }
      }
  }

}
