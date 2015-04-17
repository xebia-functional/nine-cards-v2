package com.fortysevendeg.ninecardslauncher.utils

import java.io._
import java.util.zip.{GZIPInputStream, GZIPOutputStream}
import com.fortysevendeg.ninecardslauncher.utils.ResourceUtils._
import scala.util.Try

trait FileUtils {

  def loadFile[T](file: File): Try[T] =
    Try {
      withResource[FileInputStream, T](new FileInputStream(file)) {
        fileStream =>
          val gzip = new GZIPInputStream(fileStream)
          val in = new ObjectInputStream(gzip)
          val obj: T = in.readObject.asInstanceOf[T]
          in.close()
          gzip.close()
          obj
      }
    }

  def writeFile[T](file: File, obj: T): Try[Unit] =
    Try {
      file.delete
      file.createNewFile
      withResource[FileOutputStream, Unit](new FileOutputStream(file)) {
        outputStream =>
          val gzos = new GZIPOutputStream(outputStream)
          val out = new ObjectOutputStream(gzos)
          out.writeObject(obj)
          out.flush()
          out.close()
          gzos.flush()
          gzos.close()
      }
    }

}
