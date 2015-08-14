package com.fortysevendeg.ninecardslauncher.services.utils.impl

import java.io._
import java.util.zip.{GZIPInputStream, GZIPOutputStream}

trait StreamWrapper {

   def createFileInputStream(file: File): FileInputStream
   def createFileOutputStream(file: File): FileOutputStream

   def createGZIPInputStream(fileInputStream: FileInputStream): GZIPInputStream
   def createGZIPOutputStream(fileOutputStream: FileOutputStream): GZIPOutputStream

   def createObjectInputStream(gzipInputStream: GZIPInputStream): ObjectInputStream
   def createObjectOutputStream(gzipOutputStream: GZIPOutputStream): ObjectOutputStream

   def readObjectAsInstance[T](objectInputStream: ObjectInputStream): T
   def writeObject[T](out: ObjectOutputStream, obj: T): Unit

 }
