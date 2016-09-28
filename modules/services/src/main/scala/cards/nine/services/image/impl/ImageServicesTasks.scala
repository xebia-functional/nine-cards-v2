package cards.nine.services.image.impl

import java.io.{File, FileOutputStream, InputStream}
import java.net.URL

import android.graphics._
import cards.nine.commons.CatchAll
import cards.nine.commons.contexts.ContextSupport
import cards.nine.commons.services.TaskService
import cards.nine.commons.services.TaskService.TaskService
import cards.nine.services.image._
import cards.nine.services.utils.ResourceUtils


trait ImageServicesTasks
  extends ImplicitsImageExceptions {

  val noDensity = 0

  val resourceUtils = new ResourceUtils

  def getPathByName(name: String)(implicit context: ContextSupport): TaskService[File] = TaskService {
    CatchAll[FileException] {
      new File(resourceUtils.getPath(name))
    }
  }

  def getBitmapFromURL(uri: String): TaskService[Bitmap] = TaskService {
    CatchAll[BitmapTransformationException] {
      createInputStream(uri) match {
        case is: InputStream => createBitmapByInputStream(is)
        case _ => throw BitmapTransformationException(s"Unexpected error while fetching content from uri: $uri")
      }
    }
  }

  def saveBitmap(file: File, bitmap: Bitmap): TaskService[Unit] = TaskService {
    CatchAll[FileException] {
      val out = createFileOutputStream(file)
      bitmap.compress(Bitmap.CompressFormat.PNG, 90, out)
      out.flush()
      out.close()
    }
  }

  protected def createInputStream(uri: String) = new URL(uri).getContent

  protected def createBitmapByInputStream(is: InputStream) = BitmapFactory.decodeStream(is)

  protected def createFileOutputStream(file: File): FileOutputStream = new FileOutputStream(file)

}

object ImageServicesTasks extends ImageServicesTasks