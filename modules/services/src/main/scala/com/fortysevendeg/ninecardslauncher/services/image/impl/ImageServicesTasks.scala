package com.fortysevendeg.ninecardslauncher.services.image.impl

import java.io.{File, FileOutputStream, InputStream}
import java.net.URL

import android.graphics._
import com.fortysevendeg.ninecardslauncher.commons.XorCatchAll
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.CatsService
import com.fortysevendeg.ninecardslauncher.commons.services.CatsService.CatsService
import com.fortysevendeg.ninecardslauncher.services.image._
import com.fortysevendeg.ninecardslauncher.services.utils.ResourceUtils

import scalaz.concurrent.Task

trait ImageServicesTasks
  extends ImplicitsImageExceptions {

  val noDensity = 0

  val resourceUtils = new ResourceUtils

  def getPathByName(name: String)(implicit context: ContextSupport): CatsService[File] = CatsService {
    Task {
      XorCatchAll[FileException] {
        new File(resourceUtils.getPath(name))
      }
    }
  }

  def getBitmapFromURL(uri: String): CatsService[Bitmap] = CatsService {
    Task {
      XorCatchAll[BitmapTransformationException] {
        createInputStream(uri) match {
          case is: InputStream => createBitmapByInputStream(is)
          case _ => throw BitmapTransformationExceptionImpl(s"Unexpected error while fetching content from uri: $uri")
        }
      }
    }
  }

  def saveBitmap(file: File, bitmap: Bitmap): CatsService[Unit] = CatsService {
    Task {
      XorCatchAll[FileException] {
        val out = createFileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.PNG, 90, out)
        out.flush()
        out.close()
      }
    }
  }

  protected def createInputStream(uri: String) = new URL(uri).getContent

  protected def createBitmapByInputStream(is: InputStream) = BitmapFactory.decodeStream(is)

  protected def createFileOutputStream(file: File): FileOutputStream = new FileOutputStream(file)

}

object ImageServicesTasks extends ImageServicesTasks