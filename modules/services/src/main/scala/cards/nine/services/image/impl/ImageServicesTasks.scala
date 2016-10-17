package cards.nine.services.image.impl

import java.io.{File, InputStream}

import android.graphics._
import cards.nine.commons.CatchAll
import cards.nine.commons.contexts.ContextSupport
import cards.nine.commons.services.TaskService
import cards.nine.commons.services.TaskService.TaskService
import cards.nine.commons.utils.impl.StreamWrapperImpl
import cards.nine.services.image._
import cards.nine.services.utils.ResourceUtils


trait ImageServicesTasks
  extends ImplicitsImageExceptions {

  val noDensity = 0

  val resourceUtils = new ResourceUtils

  val streamWrapper = new StreamWrapperImpl

  def getPathByName(name: String)(implicit context: ContextSupport): TaskService[File] = TaskService {
    CatchAll[FileException] {
      new File(resourceUtils.getPath(name))
    }
  }

  def getBitmapFromURL(uri: String): TaskService[Bitmap] = TaskService {
    CatchAll[BitmapTransformationException] {
      streamWrapper.createInputStream(uri) match {
        case is: InputStream => streamWrapper.createBitmapByInputStream(is)
        case _ => throw BitmapTransformationException(s"Unexpected error while fetching content from uri: $uri")
      }
    }
  }

  def saveBitmap(file: File, bitmap: Bitmap): TaskService[Unit] = TaskService {
    CatchAll[FileException] {
      val out = streamWrapper.createFileOutputStream(file)
      bitmap.compress(Bitmap.CompressFormat.PNG, 90, out)
      out.flush()
      out.close()
    }
  }
}

object ImageServicesTasks extends ImageServicesTasks