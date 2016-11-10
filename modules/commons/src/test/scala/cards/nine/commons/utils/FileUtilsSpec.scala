package cards.nine.commons.utils

import java.io._

import cards.nine.commons.contexts.ContextSupport
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope

import scala.util.{Try, Success}

trait FileUtilsSpecification
  extends Specification
    with Mockito {

  trait FileUtilsScope
    extends Scope
      with FileUtilsData {

    val mockContextSupport = mock[ContextSupport]
    val mockStreamWrapper = mock[StreamWrapper]
    val mockInputStream = mock[InputStream]

    val fileUtils = new FileUtils(mockStreamWrapper)

    val assetException = AssetException("")

  }
}

class FileUtilsSpec
  extends FileUtilsSpecification {

  "File Utils" should {

    "returns a json string when a valid fileName is provided" in
      new FileUtilsScope {

        mockStreamWrapper.openAssetsFile(fileName)(mockContextSupport) returns mockInputStream
        mockStreamWrapper.makeStringFromInputStream(mockInputStream) returns fileJson

        val result = fileUtils.readFile(fileName)(mockContextSupport)
        result mustEqual Success(fileJson)
      }.pendingUntilFixed

    "returns an Exception when the file can't be opened" in
      new FileUtilsScope {

        mockStreamWrapper.openAssetsFile(fileName)(mockContextSupport) throws new AssetException("")
        val result = fileUtils.readFile(fileName)(mockContextSupport)
        result must beFailedTry
      }.pendingUntilFixed

  }

}
