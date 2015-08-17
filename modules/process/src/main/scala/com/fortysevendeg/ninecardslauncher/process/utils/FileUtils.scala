package com.fortysevendeg.ninecardslauncher.process.utils

import java.io.{Closeable, InputStream}

import com.fortysevendeg.ninecardslauncher.commons.NineCardExtensions._
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.Service
import com.fortysevendeg.ninecardslauncher.commons.services.Service.ServiceDef2
import com.fortysevendeg.ninecardslauncher.process.utils.impl.StreamWrapperImpl

import scala.util.control.Exception._
import scalaz.concurrent.Task

class FileUtils(streamWrapper: StreamWrapper = new StreamWrapperImpl)
  extends ImplicitsUtilsException {

  def getJsonFromFile(filename: String)(implicit context: ContextSupport): ServiceDef2[String, AssetException] =
    Service {
      Task {
        CatchAll[AssetException] {
          withResource[InputStream, String](streamWrapper.openFile(filename)) {
            stream => {
              streamWrapper.makeStringFromInputStream(stream)
            }
          }
        }
      }
    }

  private[this] def withResource[C <: Closeable, R](closeable: C)(f: C => R) = {
    allCatch.andFinally(closeable.close())(f(closeable))
  }

}
