package com.fortysevendeg.ninecardslauncher.process.utils

import java.io.{IOException, InputStream}

import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope
import rapture.core.{Errata, Answer}

trait FileUtilsSpecification
  extends Specification
  with Mockito {

  trait FileUtilsScope
    extends Scope
    with UtilsData {

    val contextSupport = mock[ContextSupport]
    val mockInputStream = mock[InputStream]

    val mockFileUtils = new FileUtils {
      override def openFile(filename: String)(implicit context: ContextSupport) = mockInputStream
      override def makeStringFromInputStream(stream: InputStream) = fileJson
    }

  }

  trait ErrorUtilsScope
    extends Scope
    with FileUtilsScope
    with UtilsData {

    override val mockFileUtils = new FileUtils {
      override def openFile(filename: String)(implicit context: ContextSupport) = {
        throw new IOException
      }
    }

  }

}

class FileUtilsSpec
  extends FileUtilsSpecification {

  "File Utils" should {

    "returns a json string when a valid fileName is provided" in
      new FileUtilsScope {
        val result = mockFileUtils.getJsonFromFile(fileName)(contextSupport).run.run
        result must beLike {
          case Answer(resultJson) =>
            resultJson shouldEqual fileJson
        }
      }

    "returns an AssetException when the file can't be opened" in
      new ErrorUtilsScope {
        val result = mockFileUtils.getJsonFromFile(fileName)(contextSupport).run.run
        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, exception)) => exception must beAnInstanceOf[AssetException]
          }
        }
      }

  }

}
