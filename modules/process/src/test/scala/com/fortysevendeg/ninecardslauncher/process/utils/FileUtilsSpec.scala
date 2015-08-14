package com.fortysevendeg.ninecardslauncher.process.utils

import java.io.{IOException, InputStream}

import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.process.utils.impl.StreamWrapperImpl
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

  }

  trait ValidUtilsScope {
    self: FileUtilsScope =>

    val mockStreamWrapper = new StreamWrapperImpl {
      override def openFile(filename: String)(implicit context: ContextSupport) = mockInputStream
      override def makeStringFromInputStream(stream: InputStream) = fileJson
    }


    val mockFileUtils = new FileUtils {
      override val streamWrapper = mockStreamWrapper
    }

  }

  trait ErrorUtilsScope {
    self: FileUtilsScope =>

    val mockStreamWrapper = new StreamWrapperImpl {
      override def openFile(filename: String)(implicit context: ContextSupport) = throw new IOException
      override def makeStringFromInputStream(stream: InputStream) = fileJson
    }

    val mockFileUtils = new FileUtils {
      override val streamWrapper = mockStreamWrapper
    }

  }

}

class FileUtilsSpec
  extends FileUtilsSpecification {

  "File Utils" should {

    "returns a json string when a valid fileName is provided" in
      new FileUtilsScope with ValidUtilsScope {
        val result = mockFileUtils.getJsonFromFile(fileName)(contextSupport).run.run
        result must beLike {
          case Answer(resultJson) =>
            resultJson shouldEqual fileJson
        }
      }

    "returns an AssetException when the file can't be opened" in
      new FileUtilsScope with ErrorUtilsScope {
        val result = mockFileUtils.getJsonFromFile(fileName)(contextSupport).run.run
        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, exception)) => exception must beAnInstanceOf[AssetException]
          }
        }
      }

  }

}
