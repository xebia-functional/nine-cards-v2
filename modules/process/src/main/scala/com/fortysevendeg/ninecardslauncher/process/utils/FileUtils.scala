package com.fortysevendeg.ninecardslauncher.process.utils

import java.io.{Closeable, InputStream}

import com.fortysevendeg.ninecardslauncher.commons.NineCardExtensions._
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.exceptions.Exceptions.NineCardsException

import scala.io.Source
import scala.util.control.Exception._
import scalaz.\/
import scalaz.concurrent.Task

class FileUtils {

  def getJsonFromFile(filename: String)(implicit context: ContextSupport): Task[NineCardsException \/ String] =
    Task {
      fromTryCatchNineCardsException[String] {
        withResource[InputStream, String](context.getAssets.open(filename)) {
          stream =>
            Source.fromInputStream(stream, "UTF-8").mkString
        }
      }
    }

  private[this] def withResource[C <: Closeable, R](closeable: C)(f: C => R) = {
    allCatch.andFinally(closeable.close())(f(closeable))
  }
}
