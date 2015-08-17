package com.fortysevendeg.ninecardslauncher.process.utils

import java.io.InputStream

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
    val mockStreamWrapper = mock[StreamWrapper]
    val mockInputStream = mock[InputStream]

    val fileUtils = new FileUtils(mockStreamWrapper)

  }

  trait ValidUtilsScope {
    self: FileUtilsScope =>

    mockStreamWrapper.openFile(fileName)(contextSupport) returns mockInputStream
    mockStreamWrapper.makeStringFromInputStream(mockInputStream) returns fileJson

  }

  trait ErrorUtilsScope {
    self: FileUtilsScope =>

    mockStreamWrapper.openFile(fileName)(contextSupport) throws new RuntimeException("")
    mockStreamWrapper.makeStringFromInputStream(mockInputStream) returns fileJson

  }

}

class FileUtilsSpec
  extends FileUtilsSpecification {

  "File Utils" should {

    "returns a json string when a valid fileName is provided" in
      new FileUtilsScope with ValidUtilsScope {
        val result = fileUtils.getJsonFromFile(fileName)(contextSupport).run.run
        result must beLike {
          case Answer(resultJson) =>
            resultJson shouldEqual fileJson
        }
      }

    "returns an AssetException when the file can't be opened" in
      new FileUtilsScope with ErrorUtilsScope {
        val result = fileUtils.getJsonFromFile(fileName)(contextSupport).run.run
        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, exception)) => exception must beAnInstanceOf[AssetException]
          }
        }
      }

  }

}
