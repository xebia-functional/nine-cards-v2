package com.fortysevendeg.ninecardslauncher.commons.utils

import java.io._
import java.util.zip.{GZIPOutputStream, GZIPInputStream}

import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport

trait StreamWrapper {

  def openAssetsFile(filename: String)(implicit context: ContextSupport): InputStream

  def makeStringFromInputStream(stream: InputStream): String

  def createFileInputStream(file: File): FileInputStream
  def createFileOutputStream(file: File): FileOutputStream

  def createGZIPInputStream(fileInputStream: FileInputStream): GZIPInputStream
  def createGZIPOutputStream(fileOutputStream: FileOutputStream): GZIPOutputStream

  def createObjectInputStream(gzipInputStream: GZIPInputStream): ObjectInputStream
  def createObjectOutputStream(gzipOutputStream: GZIPOutputStream): ObjectOutputStream

  def readObjectAsInstance[T](objectInputStream: ObjectInputStream): T
  def writeObject[T](out: ObjectOutputStream, obj: T): Unit

}
