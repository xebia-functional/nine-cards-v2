package com.fortysevendeg.ninecardslauncher.services.utils

import java.io.File

import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope

trait ResourceUtilsSpecification
  extends Specification
  with Mockito {

  trait ResourceUtilsScope
    extends Scope
    with UtilsData {

    val contextSupport = mock[ContextSupport]

    val resourceUtils = new ResourceUtils

    val mockFile = mock[File]

    contextSupport.getAppIconsDir returns mockFile
    mockFile.getPath returns fileFolder

  }

}

class ResourceUtilsSpec
  extends ResourceUtilsSpecification {

  "Resource Utils" should {

    "return the file path when a valid file name is provided" in
      new ResourceUtilsScope {
        val result = resourceUtils.getPath(fileName)(contextSupport)
        result shouldEqual resultFilePath
      }

    "return a package path when a valid packageName and a valid className are provided" in
      new ResourceUtilsScope {
        val result = resourceUtils.getPathPackage(packageName, className)(contextSupport)
        result shouldEqual resultFilePath
      }

  }

}
