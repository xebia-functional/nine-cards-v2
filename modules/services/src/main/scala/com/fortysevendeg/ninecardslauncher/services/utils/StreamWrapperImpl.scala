package com.fortysevendeg.ninecardslauncher.services.utils

import java.io._
import java.util.zip.{GZIPOutputStream, GZIPInputStream}

import com.fortysevendeg.ninecardslauncher.services.utils.impl.StreamWrapper

class StreamWrapperImpl
  extends StreamWrapper{

  def createFileInputStream(file: File) = new FileInputStream(file)
  def createFileOutputStream(file: File) = new FileOutputStream(file)

  def createGZIPInputStream(fileInputStream: FileInputStream) = new GZIPInputStream(fileInputStream)
  def createGZIPOutputStream(fileOutputStream: FileOutputStream) = new GZIPOutputStream(fileOutputStream)

  def createObjectInputStream(gzipInputStream: GZIPInputStream) = new ObjectInputStream(gzipInputStream)
  def createObjectOutputStream(gzipOutputStream: GZIPOutputStream) = new ObjectOutputStream(gzipOutputStream)

  def readObjectAsInstance[T](objectInputStream: ObjectInputStream): T = objectInputStream.readObject.asInstanceOf[T]
  def writeObject[T](out: ObjectOutputStream, obj: T): Unit = out.writeObject(obj)

}
