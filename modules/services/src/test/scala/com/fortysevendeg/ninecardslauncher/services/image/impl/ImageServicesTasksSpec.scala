package com.fortysevendeg.ninecardslauncher.services.image.impl

import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.services.image.{FileException, FileExceptionImpl}
import com.fortysevendeg.ninecardslauncher.services.utils.ResourceUtils
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope
import rapture.core.{Errata, Answer}

trait ImageServicesTasksSpecification
  extends Specification
  with Mockito {

  trait ImageServicesTasksScope
    extends Scope
    with ImageServicesImplData {

    class ImageServicesTaskImpl extends ImageServicesTasks

    val contextSupport = mock[ContextSupport]

    val mockResourceUtils = new ResourceUtils {
      override def getPath(filename: String)(implicit context: ContextSupport): String = filePath
    }

    val mockImageServicesTask = new ImageServicesTaskImpl {
      override val resourceUtils = mockResourceUtils
    }
  }

  trait ErrorImageServicesTasksScope
    extends Scope
    with ImageServicesImplData {

    self: ImageServicesTasksScope =>

    override val mockResourceUtils = new ResourceUtils {
      override def getPath(filename: String)(implicit context: ContextSupport): String = ""
    }

  }

}

class ImageServicesTasksSpec
  extends ImageServicesTasksSpecification{

  "Image Services Tasks" should {

    "returns a File when the file is created with a valid filename" in
      new ImageServicesTasksScope {
        val result = mockImageServicesTask.getPathByName(fileName)(contextSupport).run.run
        result must beLike {
          case Answer(resultFile) =>
            resultFile.getName shouldEqual fileName
            resultFile.getPath shouldEqual filePath
        }
      }

    "returns a FileException when an invalid filename is provided" in
      new ImageServicesTasksScope with ErrorImageServicesTasksScope {
        val result = mockImageServicesTask.getPathByName(fileName)(contextSupport).run.run
        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, exception)) =>
              exception must beAnInstanceOf[FileException]
          }
        }
      }

  }
}
