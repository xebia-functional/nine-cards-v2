package com.fortysevendeg.ninecardslauncher.process.utils.impl

import java.io.InputStream

import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.process.utils.StreamWrapper

import scala.io.Source

class StreamWrapperImpl
  extends StreamWrapper {

  override def openFile(filename: String)(implicit context: ContextSupport) = context.getAssets.open(filename)

  override def makeStringFromInputStream(stream: InputStream): String = Source.fromInputStream(stream, "UTF-8").mkString

}
