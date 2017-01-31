/*
 * Copyright 2017 47 Degrees, LLC. <http://www.47deg.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cards.nine.commons.utils.impl

import java.io._
import java.util.zip.{GZIPInputStream, GZIPOutputStream}

import cards.nine.commons.contexts.ContextSupport
import cards.nine.commons.utils.StreamWrapper

import scala.io.Source

class StreamWrapperImpl extends StreamWrapper {

  def openAssetsFile(filename: String)(implicit context: ContextSupport) =
    context.getAssets.open(filename)

  def makeStringFromInputStream(stream: InputStream): String =
    Source.fromInputStream(stream, "UTF-8").mkString

  def createFileInputStream(file: File)  = new FileInputStream(file)
  def createFileOutputStream(file: File) = new FileOutputStream(file)

  def createGZIPInputStream(fileInputStream: FileInputStream) =
    new GZIPInputStream(fileInputStream)
  def createGZIPOutputStream(fileOutputStream: FileOutputStream) =
    new GZIPOutputStream(fileOutputStream)

  def createObjectInputStream(gzipInputStream: GZIPInputStream) =
    new ObjectInputStream(gzipInputStream)
  def createObjectOutputStream(gzipOutputStream: GZIPOutputStream) =
    new ObjectOutputStream(gzipOutputStream)

  def readObjectAsInstance[T](objectInputStream: ObjectInputStream): T =
    objectInputStream.readObject.asInstanceOf[T]
  def writeObject[T](out: ObjectOutputStream, obj: T): Unit = out.writeObject(obj)

}
