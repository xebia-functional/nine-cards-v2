package cards.nine.commons.utils

import java.io._

import android.graphics.Bitmap
import cards.nine.commons.contexts.ContextSupport

trait StreamWrapper {

  def openAssetsFile(filename: String)(implicit context: ContextSupport): InputStream

  def makeStringFromInputStream(stream: InputStream): String

  def createFileOutputStream(file: File): FileOutputStream

  def createInputStream(uri: String): AnyRef

  def createBitmapByInputStream(is: InputStream): Bitmap

}
