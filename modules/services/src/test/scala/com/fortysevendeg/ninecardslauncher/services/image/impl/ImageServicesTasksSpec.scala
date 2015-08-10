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
      override def getPath(filename: String)(implicit context: ContextSupport): String = s"$fileFolder/$filename"
    }

    val mockImageServicesTask = new ImageServicesTaskImpl {
      override val resourceUtils = mockResourceUtils
    }

  }

  trait FileNameErrorImageServicesTasksScope
  extends Scope
  with ImageServicesImplData {

    self: ImageServicesTasksScope =>

      override val mockResourceUtils = new ResourceUtils {
      override def getPath(filename: String)(implicit context: ContextSupport): String = ""
    }

  }

  trait PackageErrorImageServicesTasksScope
    extends Scope
    with ImageServicesImplData {

    self: ImageServicesTasksScope =>

    override val mockResourceUtils = new ResourceUtils {
      override def getPathPackage(packageName: String, className: String)(implicit context: ContextSupport): String = ""
    }

  }

}

class ImageServicesTasksSpec
  extends ImageServicesTasksSpecification{

  "Image Services Tasks" should {

    "returns a File when the file is created with a valid fileName" in
      new ImageServicesTasksScope {
        val result = mockImageServicesTask.getPathByName(fileName)(contextSupport).run.run
        result must beLike {
          case Answer(resultFile) =>
            resultFile.getName shouldEqual "C"
            resultFile.getPath shouldEqual s"$fileFolder/C"
        }
      }

    "returns a FileException when getPath in resourceUtils returns an empty string" in
      new ImageServicesTasksScope with FileNameErrorImageServicesTasksScope {
        val result = mockImageServicesTask.getPathByName(fileName)(contextSupport).run.run
        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, exception)) =>
              exception must beAnInstanceOf[FileException]
          }
        }
      }

    "returns a File when the file is created with a valid packageName" in
      new ImageServicesTasksScope {
        val result = mockImageServicesTask.getPathByApp(packageName, className)(contextSupport).run.run
        result must beLike {
          case Answer(resultFile) =>
            resultFile.getName shouldEqual fileName
            resultFile.getPath shouldEqual filePath
        }
      }

    "returns a FileException when getPathPackage in resourceUtils returns an empty string" in
      new ImageServicesTasksScope with PackageErrorImageServicesTasksScope {
        val result = mockImageServicesTask.getPathByApp(packageName, className)(contextSupport).run.run
        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, exception)) =>
              exception must beAnInstanceOf[FileException]
          }
        }
      }

    "returns a File when the file is created with a valid packageName" in
      new ImageServicesTasksScope {
        val result = mockImageServicesTask.getPathByPackageName(packageName)(contextSupport).run.run
        result must beLike {
          case Answer(resultFile) =>
            resultFile.getName shouldEqual packageName
            resultFile.getPath shouldEqual s"$fileFolder/$packageName"
        }
      }

    "returns a FileException when getPath in resourceUtils returns an empty string" in
      new ImageServicesTasksScope with FileNameErrorImageServicesTasksScope {
        val result = mockImageServicesTask.getPathByPackageName(packageName)(contextSupport).run.run
        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, exception)) =>
              exception must beAnInstanceOf[FileException]
          }
        }
      }

  }
}
