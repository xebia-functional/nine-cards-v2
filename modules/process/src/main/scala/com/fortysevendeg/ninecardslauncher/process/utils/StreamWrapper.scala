package com.fortysevendeg.ninecardslauncher.process.utils

import java.io.InputStream

import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport

trait StreamWrapper {

  def openFile(filename: String)(implicit context: ContextSupport): InputStream

  def makeStringFromInputStream(stream: InputStream): String

}
