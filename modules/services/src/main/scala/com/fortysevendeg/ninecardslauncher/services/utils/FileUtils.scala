package com.fortysevendeg.ninecardslauncher.services.utils

import java.io._

import com.fortysevendeg.ninecardslauncher.services.utils.impl.StreamWrapper

import scala.util.Try
import scala.util.control.Exception._

class FileUtils(streamWrapper: StreamWrapper = new StreamWrapperImpl) {

  def loadFile[T](file: File): Try[T] =
    Try {
      withResource[FileInputStream, T](streamWrapper.createFileInputStream(file)) {
        fileStream =>
          val gzip = streamWrapper.createGZIPInputStream(fileStream)
          val in = streamWrapper.createObjectInputStream(gzip)
          val obj: T = streamWrapper.readObjectAsInstance[T](in)
          in.close()
          gzip.close()
          obj
      }
    }

  def writeFile[T](file: File, obj: T): Try[Unit] =
    Try {
      file.delete
      file.createNewFile
      withResource[FileOutputStream, Unit](streamWrapper.createFileOutputStream(file)) {
        outputStream =>
          val gzos = streamWrapper.createGZIPOutputStream(outputStream)
          val out = streamWrapper.createObjectOutputStream(gzos)
          streamWrapper.writeObject[T](out, obj)
          out.flush()
          out.close()
          gzos.flush()
          gzos.close()
      }
    }

  private[this] def withResource[C <: Closeable, R](closeable: C)(f: C => R) = {
    allCatch.andFinally(closeable.close())(f(closeable))
  }

}
