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

package cards.nine.commons.utils

import java.io._
import java.util.zip.{GZIPInputStream, GZIPOutputStream}

import cards.nine.commons.contexts.ContextSupport

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
