package com.fortysevendeg.ninecardslauncher.services.utils

import java.io._
import java.util.zip.{GZIPInputStream, GZIPOutputStream}

import scala.util.Try
import scala.util.control.Exception._

trait FileUtils {

  def loadFile[T](file: File): Try[T] =
    Try {
      withResource[FileInputStream, T](createFileInputStream(file)) {
        fileStream =>
          val gzip = createGZIPInputStream(fileStream)
          val in = createObjectInputStream(gzip)
          val obj: T = readObjectAsInstance[T](in)
          in.close()
          gzip.close()
          obj
      }
    }

  def writeFile[T](file: File, obj: T): Try[Unit] =
    Try {
      file.delete
      file.createNewFile
      withResource[FileOutputStream, Unit](createFileOutputStream(file)) {
        outputStream =>
          val gzos = createGZIPOutputStream(outputStream)
          val out = createObjectOutputStream(gzos)
          writeObject[T](out, obj)
          out.flush()
          out.close()
          gzos.flush()
          gzos.close()
      }
    }

  private[this] def withResource[C <: Closeable, R](closeable: C)(f: C => R) = {
    allCatch.andFinally(closeable.close())(f(closeable))
  }

  protected def createFileInputStream(file: File) = new FileInputStream(file)
  protected def createFileOutputStream(file: File) = new FileOutputStream(file)

  protected def createGZIPInputStream(fileInputStream: FileInputStream) = new GZIPInputStream(fileInputStream)
  protected def createGZIPOutputStream(fileOutputStream: FileOutputStream) = new GZIPOutputStream(fileOutputStream)

  protected def createObjectInputStream(gzipInputStream: GZIPInputStream) = new ObjectInputStream(gzipInputStream)
  protected def createObjectOutputStream(gzipOutputStream: GZIPOutputStream) = new ObjectOutputStream(gzipOutputStream)

  protected def readObjectAsInstance[T](objectInputStream: ObjectInputStream): T = objectInputStream.readObject.asInstanceOf[T]
  protected def writeObject[T](out: ObjectOutputStream, obj: T): Unit = out.writeObject(obj)

}
