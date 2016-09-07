package com.fortysevendeg.ninecardslauncher.process.theme.impl

import android.content.res.Resources
import android.util.DisplayMetrics
import cats.data.Xor
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.utils.FileUtils
import com.fortysevendeg.ninecardslauncher.process.theme.ThemeException
import com.fortysevendeg.ninecardslauncher.process.theme.models._
import com.fortysevendeg.ninecardslauncher.commons.utils.AssetException
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope

import scala.util.Success

trait ThemeProcessSpecification
  extends Specification
    with Mockito {

  val assetException = AssetException("")

  trait ThemeProcessScope
    extends Scope
      with ThemeProcessData {

    val resources = mock[Resources]
    resources.getDisplayMetrics returns mock[DisplayMetrics]

    val mockContextSupport = mock[ContextSupport]
    val mockFileUtils = mock[FileUtils]

    val themeProcess = new ThemeProcessImpl {
      override val fileUtils = mockFileUtils
    }
  }
}

class ThemeProcessImplSpec
  extends ThemeProcessSpecification {

  "getTheme" should {

    "return a valid NineCardsTheme object for a valid request" in
      new ThemeProcessScope {

        mockFileUtils.readFile(any)(any) returns Success(validThemeJson)
        val result = themeProcess.getTheme("")(mockContextSupport).value.run

        result must beLike {
          case Xor.Right(theme) =>
            theme.name mustEqual defaultThemeName
            theme.get(SearchBackgroundColor) mustEqual intSampleColorWithoutAlpha
            theme.get(SearchPressedColor) mustEqual intSampleColorWithAlpha
        }
      }

    "return a ThemeException if the JSON is not valid" in
      new ThemeProcessScope {

        mockFileUtils.readFile(any)(any) returns Success(wrongThemeJson)
        val result = themeProcess.getTheme("")(mockContextSupport).value.run
        result must beAnInstanceOf[Xor.Left[ThemeException]]
      }

    "return a ThemeException if a wrong theme style type is included in the JSON" in
      new ThemeProcessScope  {

        mockFileUtils.readFile(any)(any) returns Success(wrongThemeStyleTypeJson)
        val result = themeProcess.getTheme("")(mockContextSupport).value.run
        result must beAnInstanceOf[Xor.Left[ThemeException]]
      }

    "return a ThemeException if a wrong theme style color is included in the JSON" in
      new ThemeProcessScope{

        mockFileUtils.readFile(any)(any) returns Success(wrongThemeStyleColorJson)
        val result = themeProcess.getTheme("")(mockContextSupport).value.run
        result must beAnInstanceOf[Xor.Left[ThemeException]]
      }

    "return a AssetException if getJsonFromFile throws a exception" in
      new ThemeProcessScope {

        mockFileUtils.readFile(any)(any) throws assetException
        val result = themeProcess.getTheme("")(mockContextSupport).value.run
        result must beAnInstanceOf[Xor.Left[AssetException]]
      }
  }

}
