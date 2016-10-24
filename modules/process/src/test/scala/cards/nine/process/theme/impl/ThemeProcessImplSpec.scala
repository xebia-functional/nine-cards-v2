package cards.nine.process.theme.impl

import android.content.res.Resources
import android.util.DisplayMetrics
import cards.nine.commons.contexts.ContextSupport
import cards.nine.commons.test.TaskServiceTestOps._
import cards.nine.commons.utils.{AssetException, FileUtils}
import cards.nine.process.theme.ThemeException
import cards.nine.models._
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
          case Right(theme) =>
            theme.name mustEqual defaultThemeName
            theme.parent mustEqual themeParentLight
            theme.get(PrimaryColor) mustEqual intSampleColorWithAlpha
            theme.get(DrawerTextColor) mustEqual intSampleColorWithoutAlpha
        }
      }

    "return a ThemeException if the JSON is not valid" in
      new ThemeProcessScope {

        mockFileUtils.readFile(any)(any) returns Success(wrongThemeJson)
        val result = themeProcess.getTheme("")(mockContextSupport).value.run
        result must beAnInstanceOf[Left[ThemeException, _]]
      }

    "return a ThemeException if a wrong parent is included in the JSON" in
      new ThemeProcessScope  {

        mockFileUtils.readFile(any)(any) returns Success(wrongThemeParentJson)
        val result = themeProcess.getTheme("")(mockContextSupport).value.run
        result must beAnInstanceOf[Left[ThemeException, _]]
      }

    "return a ThemeException if a wrong theme style type is included in the JSON" in
      new ThemeProcessScope  {

        mockFileUtils.readFile(any)(any) returns Success(wrongThemeStyleTypeJson)
        val result = themeProcess.getTheme("")(mockContextSupport).value.run
        result must beAnInstanceOf[Left[ThemeException, _]]
      }

    "return a ThemeException if a wrong theme style color is included in the JSON" in
      new ThemeProcessScope{

        mockFileUtils.readFile(any)(any) returns Success(wrongThemeStyleColorJson)
        val result = themeProcess.getTheme("")(mockContextSupport).value.run
        result must beAnInstanceOf[Left[ThemeException, _]]
      }

    "return a AssetException if getJsonFromFile throws a exception" in
      new ThemeProcessScope {

        mockFileUtils.readFile(any)(any) throws assetException
        val result = themeProcess.getTheme("")(mockContextSupport).value.run
        result must beAnInstanceOf[Left[AssetException, _]]
      }
  }

}
