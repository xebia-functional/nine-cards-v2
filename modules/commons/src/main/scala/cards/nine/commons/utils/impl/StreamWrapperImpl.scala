package cards.nine.commons.utils.impl

import java.io._
import java.net.URL

import cards.nine.commons.contexts.ContextSupport
import cards.nine.commons.utils.StreamWrapper
import android.graphics._

import scala.io.Source

class StreamWrapperImpl
  extends StreamWrapper {

  def openAssetsFile(filename: String)(implicit context: ContextSupport) = context.getAssets.open(filename)

  def makeStringFromInputStream(stream: InputStream): String = Source.fromInputStream(stream, "UTF-8").mkString

  def createFileOutputStream(file: File): FileOutputStream = new FileOutputStream(file)

  def createInputStream(uri: String): AnyRef = new URL(uri).getContent

  def createBitmapByInputStream(is: InputStream): Bitmap = BitmapFactory.decodeStream(is)

}
